package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
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
import com.bt.nextgen.service.avaloq.gateway.AvaloqException;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.bt.nextgen.service.integration.account.PayeeRequest;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.prm.service.PrmService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import org.apache.commons.collections.CollectionUtils;
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

@Service("PayeeDtoServiceV2")
@SuppressWarnings({ "squid:S1200" })
public class PayeeDtoServiceImpl implements PayeeDtoService {
    private static final Logger logger = LoggerFactory.getLogger(PayeeDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService avaloqAccountIntegrationServiceImpl;

    @Autowired
    private BpayBillerCodeRepository bpayBillerCodeRepository;

    @Autowired
    private BsbCodeRepository bsbCodeRepository;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneyErrorMapper;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private PrmService prmService;

    @Override
    public PaymentDto submit(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        PaymentDto paymentDto = null;
        if (StringUtils.isNotEmpty(paymentDtoKeyedObj.getOpType())) {
            switch (paymentDtoKeyedObj.getOpType()) {
                case Attribute.ADD:
                    paymentDto = addPayee(paymentDtoKeyedObj, serviceErrors);
                    break;
                case Attribute.DELETE:
                    paymentDto = deletePayee(paymentDtoKeyedObj, serviceErrors);
                    break;
                case Attribute.UPDATE:
                    paymentDto = updatePayee(paymentDtoKeyedObj, serviceErrors);
                    break;
                default:
                    logger.warn("submit called with no valid operation: {}", paymentDtoKeyedObj.getOpType());
            }
        }

        return paymentDto;
    }

    @Override
    public PaymentDto validate(PaymentDto keyedObject, ServiceErrors serviceErrors) {
        PayeeDetails payeeDetails = loadPayeeDetails(keyedObject.getKey().getAccountId(), serviceErrors);
        PayeeType payeeType = getPayeeType(keyedObject);
        if (PayeeType.BPAY.equals(payeeType)) {
            List<Biller> payeeList = fetchBpayBillers(payeeDetails);
            if (isDuplicateCRN(payeeList, keyedObject)) {
                addError(keyedObject, Constants.DUPLICATE_CRN);
                return keyedObject;
            }
            if (isDuplicateNickname(payeeList, keyedObject)) {
                addError(keyedObject, Constants.DUPLICATE_NICKNAME);
                return keyedObject;
            }
        } else if ((PayeeType.PAY_ANYONE.equals(payeeType) || PayeeType.LINKED.equals(payeeType))
                && isDuplicateAccount(payeeDetails.getPayanyonePayeeList(), keyedObject)) {
            addError(keyedObject, Constants.DUPLICATE_CRN);
            return keyedObject;
        }
        keyedObject.setValidBsb(validateBSB(keyedObject.getToPayeeDto().getCode()));
        return keyedObject;
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
        if (null != paymentDtoKeyedObj.getToPayeeDto().getPayeeType()) {
            return PayeeType.valueOf(paymentDtoKeyedObj.getToPayeeDto().getPayeeType());
        }
        return null;
    }

    protected PaymentDto addPayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        // Container for errors to display on screen
        ServiceErrors errors = new ServiceErrorsImpl();

        PayeeDetails payeeDetails = loadPayeeDetails(paymentDtoKeyedObj.getKey().getAccountId(), serviceErrors);
        PaymentDto paymentDto = toPayeeModel(paymentDtoKeyedObj, payeeDetails);

        // user has selected to not save the account to the address book, add the verification to the session instead
        if (StringUtils.isEmpty(paymentDtoKeyedObj.getToPayeeDto().getSaveToList())) {
            if (httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER) != null) {
                logger.info("Already found var {} in session, remove it", Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
                httpSession.removeAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
            }

            TwoFactorRuleModel ruleModel = new TwoFactorRuleModel();
            TwoFactorAccountVerificationKey accountVerificationKey = new TwoFactorAccountVerificationKey(
                    paymentDtoKeyedObj.getToPayeeDto().getAccountId(), paymentDtoKeyedObj.getToPayeeDto().getCode());
            ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(null, true));
            logger.info("Adding payee without saving to address book for linked account: {}",
                    ruleModel.getAccountStatusMap().get(accountVerificationKey));
            httpSession.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, ruleModel);

            return paymentDto;
        }

        PayeeType payeeType = getPayeeType(paymentDtoKeyedObj);
        if (PayeeType.BPAY.equals(payeeType)) {
            BillerRequest biller = makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
            avaloqAccountIntegrationServiceImpl.addNewBillerDetail(biller, errors);
        } else if (PayeeType.PAY_ANYONE.equals(payeeType)) {
            PayeeRequest payeeRequest = makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
            avaloqAccountIntegrationServiceImpl.addNewRegPayeeDetail(payeeRequest, errors);
        } else if (PayeeType.LINKED.equals(payeeType)) {
            LinkedAccRequest linkedAccRequest = makeLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
            avaloqAccountIntegrationServiceImpl.addLinkedAccount(linkedAccRequest, errors);
        }

        this.clearPayeeDetailsCache(paymentDtoKeyedObj.getKey().getAccountId());
        paymentDto = handleErrors(errors, paymentDto);
        triggerpayeeEvents(paymentDto);
        return paymentDto;
    }

    protected PaymentDto deletePayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        // Container for errors to display on screen
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDto paymentDto = null;
        PayeeDetails payeeDetails = loadPayeeDetails(paymentDtoKeyedObj.getKey().getAccountId(), serviceErrors);
        PayeeType payeeType = getPayeeType(paymentDtoKeyedObj);
        if (PayeeType.BPAY.equals(payeeType)) {
            BillerRequest biller = makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
            avaloqAccountIntegrationServiceImpl.deleteExistingBillerDetail(biller, errors);
        } else if (PayeeType.PAY_ANYONE.equals(payeeType)) {
            PayeeRequest payeeRequest = makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                avaloqAccountIntegrationServiceImpl.deleteExistingPayeeDetail(payeeRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        } else if (PayeeType.LINKED.equals(payeeType)) {
            DeleteLinkedAccRequest deleteLinkedAccRequest = makeDeleteLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                avaloqAccountIntegrationServiceImpl.deleteLinkedAccount(deleteLinkedAccRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        }
        this.clearPayeeDetailsCache(paymentDtoKeyedObj.getKey().getAccountId());
        paymentDto = toPayeeModel(paymentDtoKeyedObj, payeeDetails);
        paymentDto = handleErrors(errors, paymentDto);
        triggerpayeeEvents(paymentDto);
        return paymentDto;
    }

    protected PaymentDto updatePayee(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        // Container for errors to display on screen
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDto paymentDto = null;
        PayeeDetails payeeDetails = this.loadPayeeDetails(paymentDtoKeyedObj.getKey().getAccountId(), serviceErrors);
        PayeeType payeeType = getPayeeType(paymentDtoKeyedObj);
        if (PayeeType.BPAY.equals(payeeType)) {
            BillerRequest biller = makeBillerRequest(paymentDtoKeyedObj, payeeDetails);
            avaloqAccountIntegrationServiceImpl.updateExistingBillerDetail(biller, errors);
        } else if (PayeeType.PAY_ANYONE.equals(payeeType)) {
            PayeeRequest payeeRequest = makePayeeRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                avaloqAccountIntegrationServiceImpl.updateExistingPayeeDetail(payeeRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        } else if (PayeeType.LINKED.equals(payeeType)) {
            LinkedAccRequest linkedAccRequest = makeLinkedAccountRequest(paymentDtoKeyedObj, payeeDetails);
            try {
                avaloqAccountIntegrationServiceImpl.updateLinkedAccount(linkedAccRequest, errors);
            } catch (AvaloqException ae) {
                String errorMessage = getServiceErrorMessage(ae);
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, errorMessage);
            }
        }
        this.clearPayeeDetailsCache(paymentDtoKeyedObj.getKey().getAccountId());
        paymentDto = toPayeeModel(paymentDtoKeyedObj, payeeDetails);
        paymentDto = handleErrors(errors, paymentDto);
        triggerpayeeEvents(paymentDto);
        return paymentDto;
    }

    protected LinkedAccRequest makeLinkedAccountRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        LinkedAccRequest linkedAccRequest = new LinkedAccRequestImpl();

        LinkedAccountImpl linkedAccountImpl = new LinkedAccountImpl();
        linkedAccountImpl.setAccountNumber(paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        linkedAccountImpl.setBsb(paymentDtoKeyedObj.getToPayeeDto().getCode());
        linkedAccountImpl.setName(paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        linkedAccountImpl.setNickName(paymentDtoKeyedObj.getToPayeeDto().getNickname());
        linkedAccountImpl.setPrimary(paymentDtoKeyedObj.getToPayeeDto().isPrimary());
        linkedAccountImpl.setCurrency(CurrencyType.AustralianDollar);
        linkedAccountImpl.setLimit(paymentDtoKeyedObj.getLimit());
        linkedAccountImpl.setLinkedAccountStatus(paymentDtoKeyedObj.getToPayeeDto().getManuallyVerifiedFlag());

        AccountKey accountKey = AccountKey.valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        linkedAccRequest.setAccountKey(accountKey);
        linkedAccRequest.setLinkedAccount(linkedAccountImpl);
        linkedAccRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return linkedAccRequest;
    }

    protected PayeeRequest makePayeeRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        PayeeRequest payeeRequest = new PayeeReqImpl();

        BankAccountImpl bankAccountImpl = new BankAccountImpl();
        bankAccountImpl.setName(paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        bankAccountImpl.setNickName(paymentDtoKeyedObj.getToPayeeDto().getNickname());
        bankAccountImpl.setAccountNumber(paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        bankAccountImpl.setBsb(paymentDtoKeyedObj.getToPayeeDto().getCode());

        AccountKey accountKey = AccountKey.valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        payeeRequest.setAccountKey(accountKey);
        payeeRequest.setBankAccount(bankAccountImpl);
        payeeRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return payeeRequest;
    }

    protected BillerRequest makeBillerRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        BillerImpl billerImpl = new BillerImpl();
        if (!"DELETE".equals(paymentDtoKeyedObj.getOpType())) {
            BpayBiller bpayBiller = bpayBillerCodeRepository.load(paymentDtoKeyedObj.getToPayeeDto().getCode());
            // payeeModel.setCrnType(bpayBiller.getCrnType().name());
            paymentDtoKeyedObj.getToPayeeDto().setAccountName(bpayBiller.getBillerName());
            billerImpl.setName(paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        }
        billerImpl.setNickName(paymentDtoKeyedObj.getToPayeeDto().getNickname());
        billerImpl.setBillerCode(paymentDtoKeyedObj.getToPayeeDto().getCode());
        billerImpl.setCRN(paymentDtoKeyedObj.getToPayeeDto().getCrn());

        BillerRequest biller = new BillerReqImpl();
        AccountKey accountKey = AccountKey.valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        biller.setAccountKey(accountKey);
        biller.setBillerDetail(billerImpl);
        biller.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return biller;
    }

    protected DeleteLinkedAccRequest makeDeleteLinkedAccountRequest(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        DeleteLinkedAccRequest deleteLinkedAccRequest = new LinkedAccRequestImpl();

        BankAccountImpl bankAccountImpl = new BankAccountImpl();
        bankAccountImpl.setName(paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        bankAccountImpl.setNickName(paymentDtoKeyedObj.getToPayeeDto().getNickname());
        bankAccountImpl.setAccountNumber(paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        bankAccountImpl.setBsb(paymentDtoKeyedObj.getToPayeeDto().getCode());

        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(paymentDtoKeyedObj.getKey().getAccountId());
        deleteLinkedAccRequest.setAccountKey(accountKey);
        deleteLinkedAccRequest.setBankAccount(bankAccountImpl);
        deleteLinkedAccRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());

        return deleteLinkedAccRequest;
    }

    protected boolean isDuplicateNickname(List<Biller> payeeList, PaymentDto paymentDtoKeyedObj) {
        boolean existingNicknameFound = false;
        if (CollectionUtils.isNotEmpty(payeeList)) {
            for (Biller payee : payeeList) {
                if (payee.getNickName() != null
                        && payee.getNickName().equalsIgnoreCase(paymentDtoKeyedObj.getToPayeeDto().getNickname())) {
                    existingNicknameFound = true;
                    break;
                }
            }
        }
        return existingNicknameFound;
    }

    protected boolean validateBSB(String bsb) {
        Bsb resultBsb = bsbCodeRepository.load(bsb);
        if (resultBsb != null && resultBsb.getBsbCode() != null) {
            return true;
        }

        return false;
    }

    protected boolean isDuplicateCRN(List<Biller> billerList, PaymentDto paymentDtoKeyedObj) {
        boolean existingCRNFound = false;
        if (CollectionUtils.isNotEmpty(billerList)) {
            for (Biller biller : billerList) {
                if (biller.getBillerCode() != null && biller.getCRN() != null
                        && biller.getBillerCode().equalsIgnoreCase(paymentDtoKeyedObj.getToPayeeDto().getCode())
                        && biller.getCRN().equalsIgnoreCase(paymentDtoKeyedObj.getToPayeeDto().getCrn())) {
                    existingCRNFound = true;
                    break;
                }
            }
        }
        return existingCRNFound;
    }

    private PaymentDto toPayeeModel(PaymentDto paymentDtoKeyedObj, PayeeDetails payeeDetails) {
        PaymentDto payee = new PaymentDto(paymentDtoKeyedObj);
        PayeeDto payeeDto = new PayeeDto();
        payeeDto.setPayeeType(paymentDtoKeyedObj.getToPayeeDto().getPayeeType());
        payeeDto.setAccountName(paymentDtoKeyedObj.getToPayeeDto().getAccountName());
        payeeDto.setAccountId(paymentDtoKeyedObj.getToPayeeDto().getAccountId());
        payeeDto.setNickname(paymentDtoKeyedObj.getToPayeeDto().getNickname());
        payee.setPrimary(paymentDtoKeyedObj.getToPayeeDto().isPrimary());
        payeeDto.setCode(paymentDtoKeyedObj.getToPayeeDto().getCode());
        payeeDto.setManuallyVerifiedFlag(paymentDtoKeyedObj.getToPayeeDto().getManuallyVerifiedFlag());
        payeeDto.setCrn(paymentDtoKeyedObj.getToPayeeDto().getCrn());

        if (paymentDtoKeyedObj.getToPayeeDto().getPayeeType().equals(PayeeType.BPAY.toString()))
            payeeDto.setAccountKey(EncodedString.fromPlainText(paymentDtoKeyedObj.getToPayeeDto().getCrn()).toString());
        else
            payeeDto.setAccountKey(EncodedString.fromPlainText(paymentDtoKeyedObj.getToPayeeDto().getAccountId()).toString());
        payee.setToPayeeDto(payeeDto);

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

    protected PaymentDto handleErrors(ServiceErrors errors, PaymentDto confirmPaymentDto) {
        Iterator<ServiceError> serviceError = errors.getErrorList().iterator();
        List<DomainApiErrorDto> errorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror = serviceError.next();
            DomainApiErrorDto error = new DomainApiErrorDto(serror.getId(), serror.getErrorCode(), serror.getReason(),
                    serror.getErrorMessageForScreenDisplay(), DomainApiErrorDto.ErrorType.ERROR);
            errorList.add(error);
        }
        confirmPaymentDto.setErrors(errorList);
        return confirmPaymentDto;
    }

    protected boolean isDuplicateAccount(List<? extends BankAccount> payeeList, PaymentDto paymentDtoKeyedObj) {
        boolean existingAccountFound = false;
        paymentDtoKeyedObj.getToPayeeDto().setCode(paymentDtoKeyedObj.getToPayeeDto().getCode().trim());

        if (CollectionUtils.isNotEmpty(payeeList)) {
            for (BankAccount payee : payeeList) {
                if (payee.getBsb().equals(paymentDtoKeyedObj.getToPayeeDto().getCode())
                        && payee.getAccountNumber().equals(paymentDtoKeyedObj.getToPayeeDto().getAccountId())) {
                    existingAccountFound = true;
                    break;
                }
            }
        }

        return existingAccountFound;
    }

    private void addError(PaymentDto keyedObject, String code) {
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();
        ValidationError validationError = new ValidationError(code, "", "", ErrorType.ERROR);
        validationErrors.add(validationError);
        keyedObject.setErrors(movemoneyErrorMapper.map(validationErrors));
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

    private void triggerpayeeEvents(PaymentDto paymentDto) {
        prmService.triggerPayeeEvents(paymentDto);

    }
}
