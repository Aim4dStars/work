package com.bt.nextgen.api.account.v1.service;


import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.movemoney.v3.util.DepositUtils;
import com.bt.nextgen.api.util.DepositSort;
import com.bt.nextgen.api.util.DepositUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequestModel;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.deposit.DepositDetails;
import com.bt.nextgen.service.integration.deposit.DepositIntegrationService;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("DepositDtoServiceV1")
// Suppressed warnings in V1. To be fixed for V2
@SuppressWarnings("all")
public class DepositDtoServiceImpl implements DepositDtoService {
    private static final Logger LOGGER = getLogger(DepositDtoServiceImpl.class);


    @Autowired
    private DepositIntegrationService depositIntegrationService;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    protected StaticIntegrationService staticIntegrationService;




    // TODO: Threading bug here. Should not have a data stored in an singleton's
    // instance variable
    private Map<String, DepositDto> receiptObj = new HashMap<String, DepositDto>();

    private List<DepositDto> toDepositDto(String accountId, List<LinkedAccount> lstLinkedAccounts, PayeeDetails payeeDetails) {
        List<DepositDto> lstDepositDto = new ArrayList<>();
        if (lstLinkedAccounts != null && !lstLinkedAccounts.isEmpty()) {
            for (LinkedAccount linkedAccountModel : lstLinkedAccounts) {
                DepositDto depositDto = new DepositDto();

                if (null != linkedAccountModel) {
                    PayeeDto payeeDto = new PayeeDto();
                    payeeDto.setAccountId(linkedAccountModel.getAccountNumber());
                    payeeDto.setAccountName(linkedAccountModel.getName());
                    payeeDto.setCode(linkedAccountModel.getBsb());
                    // payeeDto.setPayeeType((linkedAccountModel.getPayeeType()).toString());
                    if (null != linkedAccountModel.getNickName())
                        payeeDto.setNickname(linkedAccountModel.getNickName());
                    payeeDto.setPrimary(linkedAccountModel.isPrimary());
                    // payeeDto.setCrn(linkedAccountModel.);
                    payeeDto.setType("Deposit");
                    LinkedAccountStatusDto linkedaccountStatus = DepositUtils.linkedAccountStatus(linkedAccountModel,staticIntegrationService);
                    payeeDto.setLinkedAccountStatus(linkedaccountStatus);
                    payeeDto.setLinkedAccountStatus(linkedaccountStatus);

                    // depositDto.setPaymentId(linkedAccountModel.);
                    depositDto.setFromPayDto(payeeDto);

                    PayeeDto toPayeeDto = new PayeeDto();
                    toPayeeDto.setAccountId(payeeDetails.getCashAccount().getAccountNumber());
                    toPayeeDto.setAccountName(payeeDetails.getCashAccount().getAccountName());
                    toPayeeDto.setCode(payeeDetails.getCashAccount().getBsb());
                    depositDto.setToPayteeDto(toPayeeDto);

                    lstDepositDto.add(depositDto);
                }
            }
            Collections.sort(lstDepositDto, new DepositSort());
            lstDepositDto = DepositUtil.movePrimaryOnTop(lstDepositDto);
        }
        return lstDepositDto;
    }

    @Override
    public List<DepositDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = criteriaList.get(0).getValue();
        PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(accountId);

        return loadPayeesForDeposits(portfolioRequest);
    }

    @Override
    public List<DepositDto> loadPayeesForDeposits(PortfolioRequest portfolioRequest) {
        String accountId = portfolioRequest.getPortfolioId();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();

        ArrayList<LinkedAccount> lstPayees = new ArrayList<LinkedAccount>();
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(accountId);

        final PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
        if (payeeDetails != null && CollectionUtils.isNotEmpty(payeeDetails.getLinkedAccountList())) {
            for (LinkedAccount linkedModel : payeeDetails.getLinkedAccountList()) {
                if (null != linkedModel) {
                    lstPayees.add(linkedModel);
                }
            }
        }
        return toDepositDto(accountId, lstPayees, payeeDetails);
    }

    @Override
    public DepositDto validate(DepositDto keyedObject, ServiceErrors serviceErrors) {
        return validateDeposit(keyedObject, serviceErrors);
    }

    public MoneyAccountIdentifier getMoneyAccountIdentifier(DepositDto keyedObject, ServiceErrors serviceErrors) {

        MoneyAccountIdentifier macId = new MoneyAccountIdentifierImpl();

        try {
            WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
            wrapAccountIdentifierImpl.setBpId(keyedObject.getKey().getAccountId());
            PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
            macId.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
        } catch (Exception e) {
            LOGGER.error("Error in getting money account Id for portfolioId {}");
        }
        return macId;
    }

    @Override
    public DepositDto validateDeposit(DepositDto keyedObject, ServiceErrors serviceErrors) {
        DepositDto confirmDepositDto;
        MoneyAccountIdentifier moneyAccountIdentifier = getMoneyAccountIdentifier(keyedObject, serviceErrors);

        // Set Payer Account Details
        PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
        payAnyOneAccounts.setAccount(keyedObject.getFromPayDto().getAccountId());
        payAnyOneAccounts.setBsb(keyedObject.getFromPayDto().getCode());
        ServiceErrors errors = new ServiceErrorsImpl();

        if (keyedObject.getIsRecurring()) {
            RecurringDepositDetails confirmDepositConversationRecur;
            confirmDepositConversationRecur = DepositUtil.populateRecurDepositDetailsReq(keyedObject, payAnyOneAccounts,
                    moneyAccountIdentifier);
            confirmDepositConversationRecur = depositIntegrationService.validateDeposit(confirmDepositConversationRecur, errors);
            confirmDepositDto = DepositUtil.toDepositDto(confirmDepositConversationRecur, keyedObject);
        } else {
            DepositDetails confirmDepositConversation;
            confirmDepositConversation = DepositUtil.populateDepositDetailsReq(keyedObject, payAnyOneAccounts,
                    moneyAccountIdentifier);
            confirmDepositConversation = depositIntegrationService.validateDeposit(confirmDepositConversation, errors);
            confirmDepositDto = DepositUtil.toDepositDto(confirmDepositConversation, keyedObject);
        }

        Iterator<ServiceError> serviceError = errors.getErrorList().iterator();
        List<ServiceError> errorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror;
            serror = (ServiceError) serviceError.next();
            errorList.add(serror);
            confirmDepositDto.setErrors(errorList);
        }

        return confirmDepositDto;
    }

    @Override
    public DepositDto submit(DepositDto keyedObject, ServiceErrors serviceErrors) {
        DepositDto depositDtoObj = submitDeposit(keyedObject, serviceErrors);
        if (depositDtoObj.getTransactionDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            depositDtoObj.setTransactionDate(dateFormat.format(new Date(depositDtoObj.getTransactionDate())));
        }

        receiptObj.put(depositDtoObj.getRecieptNumber(), depositDtoObj);
        return depositDtoObj;
        // return submitDeposit(keyedObject, serviceErrors);
    }

    @Override
    public DepositDto submitDeposit(DepositDto keyedObject, ServiceErrors serviceErrors) {

        DepositDto submitDepositDto = new DepositDto();
        ServiceErrors errors = new ServiceErrorsImpl();
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();

        wrapAccountIdentifierImpl.setBpId(keyedObject.getKey().getAccountId());
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
        if (isValidTransaction(payeeDetails, keyedObject.getFromPayDto())) {
            moneyAccountIdentifier.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
            // MoneyAccountIdentifier moneyAccountIdentifier =
            // getMoneyAccountIdentifier( keyedObject, serviceErrors);
            // Set Payer Account Details
            PayAnyoneAccountDetails payAnyOneAccounts = new PayAnyoneAccountDetailsImpl();
            payAnyOneAccounts.setAccount(keyedObject.getFromPayDto().getAccountId());
            payAnyOneAccounts.setBsb(keyedObject.getFromPayDto().getCode());
            if (keyedObject.getIsRecurring()) {
                RecurringDepositDetails submitDepositConversationRecur;
                submitDepositConversationRecur = DepositUtil.populateRecurDepositDetailsReq(keyedObject, payAnyOneAccounts,
                        moneyAccountIdentifier);
                submitDepositConversationRecur = depositIntegrationService.submitDeposit(submitDepositConversationRecur, errors);
                submitDepositDto = DepositUtil.toDepositDto(submitDepositConversationRecur, keyedObject);
            } else {
                DepositDetails submitDepositConversation;
                submitDepositConversation = DepositUtil.populateDepositDetailsReq(keyedObject, payAnyOneAccounts,
                        moneyAccountIdentifier);
                submitDepositConversation = depositIntegrationService.submitDeposit(submitDepositConversation, errors);
                submitDepositDto = DepositUtil.toDepositDto(submitDepositConversation, keyedObject);
            }
            logDepositDetails(keyedObject,submitDepositDto);
        } else {
            ServiceError error = new ServiceErrorImpl();
            error.setId(Constants.ACCT_NOT_IN_PAYEE_LIST);
            errors.addError(error);
        }

        return handleErrors(errors, submitDepositDto);
    }

    private boolean isValidTransaction(PayeeDetails payeeDetails, PayeeDto payee) {
        boolean isValid = false;

        List<LinkedAccount> linkedAccounts = payeeDetails.getLinkedAccountList();
        if (linkedAccounts != null) {
            for (LinkedAccount account : linkedAccounts) {
                if (account.getBsb().equals(payee.getCode()) && account.getAccountNumber().equals(payee.getAccountId())) {
                    isValid = true;
                    break;
                }
            }
        }
        return isValid;
    }

    private DepositDto handleErrors(ServiceErrors errors, DepositDto submitDepositDto) {

        Iterator<ServiceError> serviceError = errors.getErrorList().iterator();
        List<ServiceError> errorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror;
            serror = (ServiceError) serviceError.next();
            errorList.add(serror);
            submitDepositDto.setErrors(errorList);
        }
        return submitDepositDto;
    }

    @Override
    public Map<String, DepositDto> loadDepositReciepts() {
        return receiptObj;
    }

    private void logDepositDetails(DepositDto confirmDepositDto, DepositDto submitDepositDto) {

        LOGGER.info(LoggingConstants.DEPOSITS_SUBMIT + "BEGIN");
        if(confirmDepositDto.getFromPayDto() != null  &&confirmDepositDto.getToPayteeDto() != null ){
            LOGGER.info("FROM_ACCOUNT_ID" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getFromPayDto().getAccountId()+
                    LoggingConstants.ONBOARDING_DELIMITER +
                    "FROM_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getFromPayDto().getCode()+
                    LoggingConstants.ONBOARDING_DELIMITER +
                    "TO_ACCOUNT_ID" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getToPayteeDto().getAccountId()+
                    LoggingConstants.ONBOARDING_DELIMITER +
                    "TO_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER + confirmDepositDto.getToPayteeDto().getCode()+
                    LoggingConstants.ONBOARDING_DELIMITER +
                    "DEPOSIT_AMOUNT"+ LoggingConstants.PAYMENTS_DELIMITER +
                    confirmDepositDto.getAmount() + LoggingConstants.ONBOARDING_DELIMITER +
                    "PAYEE_TYPE" + LoggingConstants.PAYMENTS_DELIMITER +  confirmDepositDto.getToPayteeDto().getPayeeType()+
                    LoggingConstants.ONBOARDING_DELIMITER + "TRANSACTION_DATE" + LoggingConstants.PAYMENTS_DELIMITER +
                    submitDepositDto.getTransactionDate());
        }
        LOGGER.info(LoggingConstants.DEPOSITS_SUBMIT + "END");
    }
}
