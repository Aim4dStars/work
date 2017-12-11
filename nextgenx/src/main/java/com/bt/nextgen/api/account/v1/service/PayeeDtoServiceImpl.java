package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.fees.validation.PaymentDtoErrorMapper;
import com.bt.nextgen.api.movemoney.v3.util.DepositUtils;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.BpayBiller;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.payments.repository.Bsb;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.BankAccountImpl;
import com.bt.nextgen.service.avaloq.account.BillerImpl;
import com.bt.nextgen.service.avaloq.account.LinkedAccountImpl;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus;
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.bt.nextgen.service.integration.account.PayeeRequest;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("PayeeDtoServiceV1")
// Suppressed warnings in V1. To be fixed for V2
@SuppressWarnings("all")
public class PayeeDtoServiceImpl implements PayeeDtoService {
    private static final Logger logger = LoggerFactory.getLogger(PayeeDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService avaloqAccountIntegrationServiceImpl;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private BpayBillerCodeRepository bpayBillerCodeRepository;

    @Autowired
    private BsbCodeRepository bsbCodeRepository;

    @Autowired
    private PaymentDtoErrorMapper paymentsErrorMapper;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private PrmService prmService;

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    @Qualifier("customerDataManagementService")
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;


    @Override
    public PaymentDto submit(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        if (null != paymentDtoKeyedObj.getOpType() && !paymentDtoKeyedObj.getOpType().isEmpty()) {
            switch (paymentDtoKeyedObj.getOpType()) {
            case Attribute.ADD:
                return addPayee(paymentDtoKeyedObj, serviceErrors);
            case Attribute.DELETE:
                return deletePayee(paymentDtoKeyedObj, serviceErrors);
            case Attribute.UPDATE:
                return updatePayee(paymentDtoKeyedObj, serviceErrors);
            }
        }

        return null;
    }

    @Override
    public PaymentDto validate(PaymentDto keyedObject, ServiceErrors serviceErrors) {

        PayeeDetails payeeDetails = loadPayeeDetails(keyedObject.getKey().getAccountId(), serviceErrors);
        List<Biller> payeeList = fetchBpayBillers(payeeDetails);
        PayeeType payeeType = getPayeeType(keyedObject);
        if (null != payeeType && payeeType.equals(PayeeType.BPAY)) {
            if (null != payeeList && !payeeList.isEmpty() && validateDuplicateCRN(payeeList, keyedObject)) {
                this.logError(keyedObject, Constants.DUPLICATE_CRN);
                return keyedObject;
            }
            if (null != payeeList && !payeeList.isEmpty() && !validateNickName(payeeList, keyedObject)) {
                this.logError(keyedObject, Constants.DUPLICATE_NICKNAME);
                return keyedObject;
            }

        } else if (null != payeeType && (payeeType.equals(PayeeType.PAY_ANYONE)) || payeeType.equals(PayeeType.LINKED)) {
            if (null != payeeList && !payeeList.isEmpty() && null != payeeDetails.getPayanyonePayeeList()
                    && validateAccounts(payeeDetails.getPayanyonePayeeList(), keyedObject)) {
                this.logError(keyedObject, Constants.DUPLICATE_CRN);
                return keyedObject;
            }
        }
        if (payeeType.equals(PayeeType.LINKED) && !isAssociatedAccount(keyedObject, serviceErrors)) {
            throw new BadRequestException("Operation not allowed");
        }
        keyedObject.setValidBsb(validateBSB(keyedObject.getToPayteeDto().getCode()));
        return keyedObject;
    }

    private boolean isAssociatedAccount (PaymentDto keyedObject, ServiceErrors serviceErrors) {
        List<LinkedAccountStatus> associatedAccountsList = DepositUtils.populateAssociatedAccounts(keyedObject.getKey().getAccountId(), avaloqAccountIntegrationServiceImpl, profileService, customerDataManagementIntegrationService, serviceErrors);
        for (LinkedAccountStatus associatedAccount: associatedAccountsList) {
            if (associatedAccount.getAccountNumber().equalsIgnoreCase(keyedObject.getToPayteeDto().getAccountId()) &&
                    associatedAccount.getBsb().equalsIgnoreCase(keyedObject.getToPayteeDto().getCode())) {
                return true;
            }
        }
        return false;
    }
    private void clearPayeeDetailsCache(String accountId) {
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(accountId);
        payeeDetailsIntegrationService.clearCache(wrapAccountIdentifier);
    }

    private PayeeDetails loadPayeeDetails(String accountId, ServiceErrors serviceErrors) {
        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(accountId);
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifier, serviceErrors);

        return payeeDetails;
    }

    private List<Biller> fetchBpayBillers(PayeeDetails payeeDetails) {
        List<Biller> payeeList = payeeDetails.getBpayBillerPayeeList();

        if (null != payeeList && !payeeList.isEmpty()) {
            for (Biller payee : payeeList) {
                BpayBiller bpayBiller = bpayBillerCodeRepository.load(payee.getBillerCode());
                if (null != bpayBiller && null != bpayBiller.getCrnType())
                    payee.setCRNType(bpayBiller.getCrnType().name());
            }
        }
        return payeeList;
    }

    private PayeeType getPayeeType(PaymentDto paymentDtoKeyedObj) {
        if (null != paymentDtoKeyedObj.getToPayteeDto().getPayeeType()) {
            return PayeeType.valueOf(paymentDtoKeyedObj.getToPayteeDto().getPayeeType());
        }
        return null;
    }

    @Override
    public PaymentDto addPayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        // Container for errors to display on screen
        ServiceErrors errors = new ServiceErrorsImpl();

        PayeeDetails payeeDetails = loadPayeeDetails(paymentDtoKeyedObj.getKey().getAccountId(), serviceErrors);
        PaymentDto paymentDto = toPayeeModel(paymentDtoKeyedObj, payeeDetails);

        // user has selected to not save the account to the address book, add the verification to the session instead
        if (StringUtils.isEmpty(paymentDtoKeyedObj.getToPayteeDto().getSaveToList())) {
            if (httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER) != null) {
                logger.info("Already found var {} in session, remove it", Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
                httpSession.removeAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
            }

            TwoFactorRuleModel ruleModel = new TwoFactorRuleModel();
            TwoFactorAccountVerificationKey accountVerificationKey = new TwoFactorAccountVerificationKey(
                    paymentDtoKeyedObj.getToPayteeDto().getAccountId(), paymentDtoKeyedObj.getToPayteeDto().getCode());
            ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(null, true));
            logger.info("Adding payee without saving to address book for linked account: {}",
                    ruleModel.getAccountStatusMap().get(accountVerificationKey));
            httpSession.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, ruleModel);

            return paymentDto;
        }

        PayeeType payeeType = getPayeeType(paymentDtoKeyedObj);
        if (null != payeeType && payeeType.equals(PayeeType.BPAY)) {
            BillerRequest biller = makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
            UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.addNewBillerDetail(biller, errors);
        } else if (null != payeeType && payeeType.equals(PayeeType.PAY_ANYONE)) {
            PayeeRequest payeeRequest = makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
            UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.addNewRegPayeeDetail(payeeRequest, errors);
        } else if (null != payeeType && payeeType.equals(PayeeType.LINKED)) {
            LinkedAccRequest linkedAccRequest = makeLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
            UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.addLinkedAccount(linkedAccRequest, errors);
        }

        this.clearPayeeDetailsCache(paymentDtoKeyedObj.getKey().getAccountId());
        triggerpayeeEvents(errors, paymentDto);
        paymentDto = handleErrors(errors, paymentDto);
        return paymentDto;
    }

    public PaymentDto deletePayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        // Container for errors to display on screen
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDto paymentDto = null;
        PayeeDetails payeeDetails = loadPayeeDetails(paymentDtoKeyedObj.getKey().getAccountId(), serviceErrors);
        PayeeType payeeType = getPayeeType(paymentDtoKeyedObj);
        if (null != payeeType && payeeType.equals(PayeeType.BPAY)) {
            BillerRequest biller = makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
            UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.deleteExistingBillerDetail(biller, errors);

        } else if (null != payeeType && payeeType.equals(PayeeType.PAY_ANYONE)) {
            PayeeRequest payeeRequest = makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.deleteExistingPayeeDetail(
                        payeeRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }

        } else if (null != payeeType && payeeType.equals(PayeeType.LINKED)) {
            DeleteLinkedAccRequest deleteLinkedAccRequest = makeDeleteLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.deleteLinkedAccount(
                        deleteLinkedAccRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        }
        this.clearPayeeDetailsCache(paymentDtoKeyedObj.getKey().getAccountId());
        paymentDto = toPayeeModel(paymentDtoKeyedObj, payeeDetails);
        triggerpayeeEvents(errors, paymentDto);
        paymentDto = handleErrors(errors, paymentDto);
        return paymentDto;
    }

    public PaymentDto updatePayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        // Container for errors to display on screen
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDto paymentDto = null;
        PayeeDetails payeeDetails = this.loadPayeeDetails(paymentDtoKeyedObj.getKey().getAccountId(), serviceErrors);
        PayeeType payeeType = getPayeeType(paymentDtoKeyedObj);
        if (null != payeeType && payeeType.equals(PayeeType.BPAY)) {
            BillerRequest biller = makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
            UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.updateExistingBillerDetail(biller, errors);
        } else if (null != payeeType && payeeType.equals(PayeeType.PAY_ANYONE)) {
            PayeeRequest payeeRequest = makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.updateExistingPayeeDetail(
                        payeeRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        } else if (null != payeeType && payeeType.equals(PayeeType.LINKED)) {
            LinkedAccRequest linkedAccRequest = makeLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl.updateLinkedAccount(linkedAccRequest,
                        errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        }
        this.clearPayeeDetailsCache(paymentDtoKeyedObj.getKey().getAccountId());
        paymentDto = toPayeeModel(paymentDtoKeyedObj, payeeDetails);
        triggerpayeeEvents(errors, paymentDto);
        paymentDto = handleErrors(errors, paymentDto);
        return paymentDto;
    }

    public LinkedAccRequest makeLinkedAccountRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        LinkedAccRequest linkedAccRequest = new LinkedAccRequestImpl();

        LinkedAccountImpl linkedAccountImpl = new LinkedAccountImpl();
        linkedAccountImpl.setAccountNumber(paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        linkedAccountImpl.setBsb(paymentDtoKeyedObj.getToPayteeDto().getCode());
        linkedAccountImpl.setName(paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        linkedAccountImpl.setNickName(paymentDtoKeyedObj.getToPayteeDto().getNickname());
        linkedAccountImpl.setPrimary(paymentDtoKeyedObj.getToPayteeDto().isPrimary());
        linkedAccountImpl.setCurrency(CurrencyType.AustralianDollar);
        linkedAccountImpl.setLimit(paymentDtoKeyedObj.getLimit());
        linkedAccountImpl.setLinkedAccountStatus(paymentDtoKeyedObj.getToPayteeDto().getManuallyVerifiedFlag());

        AccountKey accountKey = AccountKey.valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        linkedAccRequest.setAccountKey(accountKey);
        linkedAccRequest.setLinkedAccount(linkedAccountImpl);
        linkedAccRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return linkedAccRequest;
    }

    public PayeeRequest makePayeeRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        PayeeRequest payeeRequest = new PayeeReqImpl();

        BankAccountImpl bankAccountImpl = new BankAccountImpl();
        bankAccountImpl.setName(paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        bankAccountImpl.setNickName(paymentDtoKeyedObj.getToPayteeDto().getNickname());
        bankAccountImpl.setAccountNumber(paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        bankAccountImpl.setBsb(paymentDtoKeyedObj.getToPayteeDto().getCode());

        AccountKey accountKey = AccountKey.valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        payeeRequest.setAccountKey(accountKey);
        payeeRequest.setBankAccount(bankAccountImpl);
        payeeRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return payeeRequest;
    }

    public BillerRequest makeBillerRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        BillerImpl billerImpl = new BillerImpl();
        if (!"DELETE".equals(paymentDtoKeyedObj.getOpType())) {
            BpayBiller bpayBiller = bpayBillerCodeRepository.load(paymentDtoKeyedObj.getToPayteeDto().getCode());
            // payeeModel.setCrnType(bpayBiller.getCrnType().name());
            paymentDtoKeyedObj.getToPayteeDto().setAccountName(bpayBiller.getBillerName());
            billerImpl.setName(paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        }
        billerImpl.setNickName(paymentDtoKeyedObj.getToPayteeDto().getNickname());
        billerImpl.setBillerCode(paymentDtoKeyedObj.getToPayteeDto().getCode());
        billerImpl.setCRN(paymentDtoKeyedObj.getToPayteeDto().getCrn());

        BillerRequest biller = new BilllerReqImpl();
        AccountKey accountKey = AccountKey.valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        biller.setAccountKey(accountKey);
        biller.setBillerDetail(billerImpl);
        biller.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return biller;
    }

    public DeleteLinkedAccRequest makeDeleteLinkedAccountRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        DeleteLinkedAccRequest deleteLinkedAccRequest = new LinkedAccRequestImpl();

        BankAccountImpl bankAccountImpl = new BankAccountImpl();
        bankAccountImpl.setName(paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        bankAccountImpl.setNickName(paymentDtoKeyedObj.getToPayteeDto().getNickname());
        bankAccountImpl.setAccountNumber(paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        bankAccountImpl.setBsb(paymentDtoKeyedObj.getToPayteeDto().getCode());

        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        deleteLinkedAccRequest.setAccountKey(accountKey);
        deleteLinkedAccRequest.setBankAccount(bankAccountImpl);
        deleteLinkedAccRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return deleteLinkedAccRequest;
    }

    private boolean validateNickName(List<Biller> payeeList, PaymentDto paymentDtoKeyedObj) {
        boolean isCombinationInValid = true;
        if (null != payeeList) {
            for (Biller payee : payeeList) {
                if (null != payee) {

                    if (null != payee.getNickName() && null != paymentDtoKeyedObj.getToPayteeDto().getNickname()) {
                        if (paymentDtoKeyedObj.getToPayteeDto().getNickname().equalsIgnoreCase(payee.getNickName())) {
                            isCombinationInValid = false;
                            break;
                        }
                    }
                }
            }
        }
        return isCombinationInValid;
    }

    private boolean validateBSB(String bsb) {
        Bsb resultBsb = bsbCodeRepository.load(bsb);
        if (null != resultBsb && null != resultBsb.getBsbCode())
            return true;
        else
            return false;
    }

    private boolean validateDuplicateCRN(List<Biller> payeeList, PaymentDto paymentDtoKeyedObj) {
        boolean isCombinationInValid = false;
        if (null != payeeList) {
            for (Biller payee : payeeList) {
                if (null != payee) {
                    if (null != payee.getBillerCode() && null != payee.getCRN()
                            && null != paymentDtoKeyedObj.getToPayteeDto().getCode()
                            && null != paymentDtoKeyedObj.getToPayteeDto().getCrn()) {
                        if (paymentDtoKeyedObj.getToPayteeDto().getCode().equalsIgnoreCase(payee.getBillerCode())) {

                            if (paymentDtoKeyedObj.getToPayteeDto().getCrn().equalsIgnoreCase(payee.getCRN())) {
                                isCombinationInValid = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return isCombinationInValid;
    }

    private PaymentDto toPayeeModel(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        PaymentDto payee = new PaymentDto(paymentDtoKeyedObj);
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType(paymentDtoKeyedObj.getToPayteeDto().getPayeeType());
        payeeDto.setAccountName(paymentDtoKeyedObj.getToPayteeDto().getAccountName());
        payeeDto.setAccountId(paymentDtoKeyedObj.getToPayteeDto().getAccountId());
        payeeDto.setNickname(paymentDtoKeyedObj.getToPayteeDto().getNickname());
        payee.setPrimary(paymentDtoKeyedObj.getToPayteeDto().isPrimary());
        payeeDto.setCode(paymentDtoKeyedObj.getToPayteeDto().getCode());
        payeeDto.setCrn(paymentDtoKeyedObj.getToPayteeDto().getCrn());
        payeeDto.setManuallyVerifiedFlag(paymentDtoKeyedObj.getToPayteeDto().getManuallyVerifiedFlag());
        if (paymentDtoKeyedObj.getToPayteeDto().getPayeeType().equals(PayeeType.BPAY.toString()))
            payeeDto.setAccountKey(EncodedString.fromPlainText(paymentDtoKeyedObj.getToPayteeDto().getCrn()).toString());
        else
            payeeDto.setAccountKey(EncodedString.fromPlainText(paymentDtoKeyedObj.getToPayteeDto().getAccountId()).toString());
        payee.setToPayteeDto(payeeDto);

        PayeeDto fromPayeeDto = new PayeeDto();
        if (null != payeeDetails && null != payeeDetails.getCashAccount()) {
            fromPayeeDto.setAccountId(payeeDetails.getCashAccount().getAccountNumber());
            fromPayeeDto.setAccountName(payeeDetails.getCashAccount().getAccountName());
            fromPayeeDto.setCode(payeeDetails.getCashAccount().getBsb());
            fromPayeeDto.setAccountKey(EncodedString.fromPlainText(payeeDetails.getCashAccount().getAccountNumber()).toString());
        }
        payee.setFromPayDto(fromPayeeDto);
        return payee;

    }

    public PaymentDto handleErrors(ServiceErrors errors, PaymentDto confirmPaymentDto) {

        Iterator<ServiceError> serviceError = errors.getErrorList().iterator();
        List<ServiceError> errorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror;
            serror = serviceError.next();
            errorList.add(serror);
            confirmPaymentDto.setErrors(errorList);
        }
        return confirmPaymentDto;
    }

    private boolean validateAccounts(List<? extends BankAccount> payeeList, PaymentDto paymentDtoKeyedObj) {
        boolean isCombinationInValid = false;

        for (BankAccount payee : payeeList) {
            if (null != payee) {
                if (null != payee.getAccountNumber() && null != payee.getBsb()
                        && null != paymentDtoKeyedObj.getToPayteeDto().getCode()
                        && null != paymentDtoKeyedObj.getToPayteeDto().getAccountId()) {
                    String bsb = paymentDtoKeyedObj.getToPayteeDto().getCode();
                    // string teststring = bsb.replacefirst("^0+(?!$)", "");
                    if (null != bsb) {
                        paymentDtoKeyedObj.getToPayteeDto().setCode(bsb.trim());
                    }
                    if (paymentDtoKeyedObj.getToPayteeDto().getCode().equalsIgnoreCase(payee.getBsb())) {
                        if (paymentDtoKeyedObj.getToPayteeDto().getAccountId().equalsIgnoreCase(payee.getAccountNumber())) {
                            isCombinationInValid = true;
                            break;
                        }
                    }
                }
            }
        }

        return isCombinationInValid;
    }

    private void logError(PaymentDto keyedObject, String code) {
        List<ServiceError> validationErrors = new ArrayList<ServiceError>();
        ServiceError validationError = new ServiceErrorImpl();
        validationError.setId(code);
        validationError.setType("Validation");
        validationErrors.add(validationError);
        keyedObject.setErrors(validationErrors);

    }

    private void logWarning(PaymentDto keyedObject, String code) {
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        ValidationError validationError = new ValidationError("", code);
        validationErrors.add(validationError);
        keyedObject.setWarnings(paymentsErrorMapper.map(validationErrors));
    }

    private String getServiceErrorMessage(AvaloqException ae) {
        String errorMessage = "";
        ServiceErrors error = ae.getServiceErrors();
        Iterator<ServiceError> serviceError = error.getErrors().values().iterator();
        while (serviceError.hasNext()) {
            ServiceError serror = serviceError.next();
            errorMessage = serror.getMessage();
        }
        return errorMessage;
    }

    private void triggerpayeeEvents(ServiceErrors errors, PaymentDto paymentDto) {
        if (!errors.hasErrors() ) {
            prmService.triggerPayeeEvents(errors,  paymentDto );
        }
    }
}
