package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountKey;
import com.bt.nextgen.api.account.v1.model.DailyLimitDto;
import com.bt.nextgen.api.account.v1.model.PayeeDto;
import com.bt.nextgen.api.account.v1.model.PaymentDto;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.draftaccount.LoggingConstants;
import com.bt.nextgen.api.movemoney.v3.util.DepositUtils;
import com.bt.nextgen.api.util.IntegrationUtil;
import com.bt.nextgen.api.util.PaymentUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PortfolioRequest;
import com.bt.nextgen.service.avaloq.PortfolioRequestModel;
import com.bt.nextgen.service.avaloq.account.AvailableCashImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AvailableCash;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.bt.nextgen.service.integration.payments.PaymentDetails;
import com.bt.nextgen.service.integration.payments.PaymentIntegrationService;
import com.bt.nextgen.service.integration.payments.RecurringPaymentDetails;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.slf4j.LoggerFactory.*;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service("PaymentDtoServiceV1")
@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1200" })
public class PaymentDtoServiceImpl implements PaymentDtoService {

    private static final Logger LOGGER = getLogger(PaymentDtoServiceImpl.class);

    @Autowired
    @Qualifier("DeprecatedPaymentServiceIntegrationImpl")
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    protected StaticIntegrationService staticIntegrationService;

    private Map<String, PaymentDto> receiptObj = new ConcurrentHashMap<>();

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    private InvestorProfileService profileService;

    @Autowired
    @Qualifier("customerDataManagementService")
    private CustomerDataManagementIntegrationService customerDataManagementIntegrationService;


    private List<PaymentDto> toPaymentDto(String accountId, List<LinkedAccount> lstPayeeAccounts, PayeeDetails payeeDetails,
            BigDecimal availableCash, String accountsAndBillers, ServiceErrors serviceErrors) {
        List<PaymentDto> lstPaymentDto = new ArrayList<PaymentDto>();
        List<LinkedAccountStatus> linkedAccountStatusList = null;
        AccountKey accountKey = new AccountKey(accountId);
        if(profileService.isInvestor() && !profileService.isEmulating() && "true".equalsIgnoreCase(accountsAndBillers)) {
             linkedAccountStatusList = DepositUtils.populateAssociatedAccounts(accountId, accountIntegrationService, profileService, customerDataManagementIntegrationService, serviceErrors);
        }
        if (lstPayeeAccounts != null && !lstPayeeAccounts.isEmpty()) {
            for (LinkedAccount bankAccount : lstPayeeAccounts) {
                PaymentDto paymentDto = new PaymentDto(accountKey);
                PayeeDto toPayeeDto = new PayeeDto();
                PayeeDto fromPayeeDto = new PayeeDto();

                setLinkedToPayeeDto(bankAccount, toPayeeDto);
                setLinkedFromPayeeDto(payeeDetails, fromPayeeDto);

                paymentDto.setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.LINKED.toString(), availableCash));
                paymentDto.setPrimary(bankAccount.isPrimary());
                paymentDto.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
                paymentDto.setFromPayDto(fromPayeeDto);
                paymentDto.setToPayteeDto(toPayeeDto);
                paymentDto.setAssociatedAccounts(linkedAccountStatusList);

                lstPaymentDto.add(paymentDto);
            }
        } else {
            PaymentDto paymentDtoLinked = new PaymentDto();
            PayeeDto payeeDto = new PayeeDto();
            payeeDto.setPayeeType(PayeeType.LINKED.toString());
            paymentDtoLinked.setToPayteeDto(payeeDto);
            paymentDtoLinked.setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.LINKED.toString(), availableCash));
            paymentDtoLinked.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());

            lstPaymentDto.add(paymentDtoLinked);
        }
        return lstPaymentDto;
    }

    private void setLinkedFromPayeeDto(PayeeDetails payeeDetails, PayeeDto fromPayeeDto) {
        fromPayeeDto.setAccountId(payeeDetails.getCashAccount().getAccountNumber());
        fromPayeeDto.setAccountName(payeeDetails.getCashAccount().getAccountName());
        fromPayeeDto.setCode(payeeDetails.getCashAccount().getBsb());
        fromPayeeDto.setAccountKey(EncodedString.fromPlainText(payeeDetails.getCashAccount().getAccountNumber()).toString());
    }

    private void setLinkedToPayeeDto(LinkedAccount bankAccount, PayeeDto toPayeeDto) {
        if (null != bankAccount.getName())
            toPayeeDto.setAccountName(bankAccount.getName().trim());
        if (null != bankAccount.getNickName())
            toPayeeDto.setNickname(bankAccount.getNickName().trim());
        toPayeeDto.setAccountId(bankAccount.getAccountNumber());
        toPayeeDto.setCode(bankAccount.getBsb());
        toPayeeDto.setPrimary(bankAccount.isPrimary());
        toPayeeDto.setPayeeType((PayeeType.LINKED).toString());
        toPayeeDto.setType(Attribute.PAYMENT);
        LinkedAccountStatusDto linkedAccountStatus = DepositUtils.linkedAccountStatus(bankAccount,staticIntegrationService);
        toPayeeDto.setLinkedAccountStatus(linkedAccountStatus);
        toPayeeDto.setSaveToList("save");
        toPayeeDto.setAccountKey(EncodedString.fromPlainText(bankAccount.getAccountNumber()).toString());
    }

    private List<PaymentDto> toPaymentDtoPayAnyOne(String accountId, List<PayAnyOne> lstPayeeAccounts, PayeeDetails payeeDetails,
            BigDecimal availableCash) {
        List<PaymentDto> lstPaymentDto = new ArrayList<PaymentDto>();

        AccountKey accountKey = new AccountKey(accountId);
        if (lstPayeeAccounts != null && !lstPayeeAccounts.isEmpty()) {
            for (BankAccount bankAccount : lstPayeeAccounts) {

                PayeeDto toPayeeDto = new PayeeDto();

                if (null != bankAccount.getName())
                    toPayeeDto.setAccountName(bankAccount.getName().trim());
                if (null != bankAccount.getNickName())
                    toPayeeDto.setNickname(bankAccount.getNickName().trim());
                toPayeeDto.setAccountId(bankAccount.getAccountNumber());
                toPayeeDto.setCode(bankAccount.getBsb());
                toPayeeDto.setType(Attribute.PAYMENT);
                toPayeeDto.setPayeeType((PayeeType.PAY_ANYONE).toString());
                toPayeeDto.setSaveToList("save");
                toPayeeDto.setAccountKey(EncodedString.fromPlainText(bankAccount.getAccountNumber()).toString());
                PayeeDto fromPayeeDto = new PayeeDto();
                fromPayeeDto.setAccountId(payeeDetails.getCashAccount().getAccountNumber());
                fromPayeeDto.setAccountName(payeeDetails.getCashAccount().getAccountName());
                fromPayeeDto.setCode(payeeDetails.getCashAccount().getBsb());
                fromPayeeDto
                        .setAccountKey(EncodedString.fromPlainText(payeeDetails.getCashAccount().getAccountNumber()).toString());
                PaymentDto paymentDto = new PaymentDto(accountKey);
                paymentDto
                        .setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.PAY_ANYONE.toString(), availableCash));
                paymentDto.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
                paymentDto.setFromPayDto(fromPayeeDto);
                paymentDto.setToPayteeDto(toPayeeDto);

                lstPaymentDto.add(paymentDto);
            }
        } else {
            PaymentDto paymentDtoPayAnyOne = new PaymentDto();
            PayeeDto payeeDto = new PayeeDto();
            payeeDto.setPayeeType(PayeeType.PAY_ANYONE.toString());
            paymentDtoPayAnyOne.setToPayteeDto(payeeDto);
            paymentDtoPayAnyOne
                    .setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.PAY_ANYONE.toString(), availableCash));
            paymentDtoPayAnyOne.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());

            lstPaymentDto.add(paymentDtoPayAnyOne);
        }

        return lstPaymentDto;
    }

    private List<PaymentDto> toPaymentDtoForBillers(String accountId, List<Biller> lstbPAYAccounts, PayeeDetails payeeDetails,
            BigDecimal availableCash) {
        List<PaymentDto> lstPaymentDto = new ArrayList<PaymentDto>();
        AccountKey accountKey = new AccountKey(accountId);

        if (lstbPAYAccounts != null && !lstbPAYAccounts.isEmpty()) {
            for (Biller biller : lstbPAYAccounts) {

                PayeeDto toPayeeDto = new PayeeDto();

                if (null != biller.getName())
                    toPayeeDto.setAccountName(biller.getName().trim());
                if (null != biller.getNickName())
                    toPayeeDto.setNickname(biller.getNickName().trim());
                toPayeeDto.setCode(biller.getBillerCode());
                toPayeeDto.setPayeeType((PayeeType.BPAY).toString());
                toPayeeDto.setCrn(biller.getCRN());
                toPayeeDto.setType(Attribute.PAYMENT);
                toPayeeDto.setSaveToList("save");
                toPayeeDto.setAccountKey(EncodedString.fromPlainText(biller.getCRN()).toString());
                PayeeDto fromPayeeDto = new PayeeDto();
                fromPayeeDto.setAccountId(payeeDetails.getCashAccount().getAccountNumber());
                fromPayeeDto.setAccountName(payeeDetails.getCashAccount().getAccountName());
                fromPayeeDto.setCode(payeeDetails.getCashAccount().getBsb());
                fromPayeeDto
                        .setAccountKey(EncodedString.fromPlainText(payeeDetails.getCashAccount().getAccountNumber()).toString());
                PaymentDto paymentDto = new PaymentDto(accountKey);
                paymentDto.setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.BPAY.toString(), availableCash));
                paymentDto.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());
                paymentDto.setToPayteeDto(toPayeeDto);
                paymentDto.setFromPayDto(fromPayeeDto);

                lstPaymentDto.add(paymentDto);
            }
        } else {
            PaymentDto paymentDtobPAY = new PaymentDto();
            PayeeDto payeeDto = new PayeeDto();
            payeeDto.setPayeeType(PayeeType.BPAY.toString());
            paymentDtobPAY.setToPayteeDto(payeeDto);
            paymentDtobPAY.setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.BPAY.toString(), availableCash));
            paymentDtobPAY.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());

            lstPaymentDto.add(paymentDtobPAY);
        }
        return lstPaymentDto;
    }

    private DailyLimitDto populateLimitsForAccount(PayeeDetails payeeDetails, String payeeType, BigDecimal availableCash) {
        DailyLimitDto dailyLimitDto = new DailyLimitDto();
        if (null != payeeDetails && null != payeeDetails.getPayeeLimits()) {
            for (PayeeLimit payeeLimit : payeeDetails.getPayeeLimits()) {
                if (null != payeeLimit.getRemainingLimit()) {
                    if (payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.BPAY.toString()))
                        dailyLimitDto.setRemainingBpayLimit(new BigDecimal(deformatCurrency(payeeLimit.getRemainingLimit())));
                    else if (payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.PAY_ANYONE.toString()))
                        dailyLimitDto
                                .setRemainingPayAnyoneLimit(new BigDecimal(deformatCurrency(payeeLimit.getRemainingLimit())));
                }
                if (payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.BPAY.toString()))
                    dailyLimitDto.setBpayLimit(new BigDecimal(deformatCurrency(payeeLimit.getLimitAmount())));
                else if (payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.PAY_ANYONE.toString()))
                    dailyLimitDto.setPayAnyoneLimit(new BigDecimal(deformatCurrency(payeeLimit.getLimitAmount())));
            }
        }

        /*
         * if (lstAccountBalance != null && !lstAccountBalance.isEmpty()) { for (AccountBalance accountBalance :
         * lstAccountBalance) { if (payeeType.equalsIgnoreCase(PayeeType.LINKED.toString())) {
         * dailyLimitDto.setLinkedLimit(accountBalance.getAvailableCash());
         * dailyLimitDto.setRemainingLinkedLimit(accountBalance.getAvailableCash ()); }
         * dailyLimitDto.setAvailableCash(accountBalance.getAvailableCash()); } }
         */

        dailyLimitDto.setAvailableCash(availableCash);
        if (payeeType.equalsIgnoreCase(PayeeType.LINKED.toString())) {
            dailyLimitDto.setLinkedLimit(availableCash);
            dailyLimitDto.setRemainingLinkedLimit(availableCash);
        }

        if (null != payeeDetails && null != payeeDetails.getMaxDailyLimit()) {
            dailyLimitDto.setMaxLimit(new BigDecimal(payeeDetails.getMaxDailyLimit()));
        }

        return dailyLimitDto;
    }


    public static String deformatCurrency(String amount) {
        if (StringUtils.isBlank(amount))
            return "0";
        return amount.replaceAll("[\\$\\,\\,]", "").trim();
    }

    @Override
    public List<PaymentDto> search(List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        String accountId = null;
        String model = null;

        for (int i = 0; i < criteria.size(); i++) {
            switch (criteria.get(i).getProperty()) {
                case Attribute.PORTFOLIO_ID:
                    accountId = criteria.get(i).getValue();
                    break;
                case Attribute.MOVE_MONEY_MODEL:
                    model = criteria.get(i).getValue();
                    break;
                default:
                    break;
            }
        }
        List<PaymentDto> paymentDtoList = loadPayees(accountId, model, serviceErrors);
        return paymentDtoList;
    }

    @Override
    public List<PaymentDto> loadPayees(String accountId, String model, ServiceErrors serviceErrors) {

        WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
        wrapAccountIdentifier.setBpId(accountId);
        // boolean isPrimary = false;

        AvailableCash accountBalance = new AvailableCashImpl();
        if (null == model) {
            accountBalance = accountIntegrationService
                    .loadAvailableCash(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        }
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifier, serviceErrors);

        // Get Linked Account. Sort and Move Primary on Top
        List<LinkedAccount> lstLinkedAccounts = payeeDetails.getLinkedAccountList();

        if (null != lstLinkedAccounts) {
            lstLinkedAccounts = PaymentUtil.sortLinkedAccount(lstLinkedAccounts, model);
            lstLinkedAccounts = PaymentUtil.movePrimaryOnTop(lstLinkedAccounts);

            // LinkedAccount linkedAccnt = lstLinkedAccounts.get(0);

        }
        List<PaymentDto> lstPaymentDto = toPaymentDto(accountId, lstLinkedAccounts, payeeDetails,
                accountBalance.getAvailableCash(), model, serviceErrors);

        // Get Payanyone Account. Sort on Nick name / Account name
        List<PayAnyOne> lstPayAnyOneAccounts = payeeDetails.getPayanyonePayeeList();
        if (null != lstPayAnyOneAccounts) {
            lstPayAnyOneAccounts = PaymentUtil.sortPayAnyoneAccount(lstPayAnyOneAccounts, model);
            lstPaymentDto.addAll(
                    toPaymentDtoPayAnyOne(accountId, lstPayAnyOneAccounts, payeeDetails, accountBalance.getAvailableCash()));
        }

        // Get BPay Acounts. Sort on Nick name / Account name
        List<Biller> lstbPAYAccounts = payeeDetails.getBpayBillerPayeeList();
        if (null != lstbPAYAccounts) {
            // TODO Uncomment when CRN Type is required
            /*
             * for (Biller payee : lstBPAYAccounts) { BpayBiller bpayBiller =
             * bpayBillerCodeRepository.load(payee.getBillerCode()); if(null!=bpayBiller && null!=bpayBiller.getCrnType())
             * payee.setCRNType(bpayBiller.getCrnType().name()); }
             */
            lstbPAYAccounts = PaymentUtil.sortBPayAccount(lstbPAYAccounts, model);
            lstPaymentDto
                    .addAll(toPaymentDtoForBillers(accountId, lstbPAYAccounts, payeeDetails, accountBalance.getAvailableCash()));
        }

        return lstPaymentDto;

    }

    @Override
    public PaymentDto validatePayment(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        PaymentDto confirmPaymentDto = new PaymentDto(paymentDtoKeyedObj.getKey());
        PaymentDetails paymentDetails = null;
        RecurringPaymentDetails recurringPaymentDetails = null;
        ServiceErrors errors = new ServiceErrorsImpl();

        // Call PAY_DET service to get Money Account Identifier
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(paymentDtoKeyedObj.getKey().getAccountId());
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());

        if (paymentDtoKeyedObj.getIsRecurring() == true) {
            recurringPaymentDetails = PaymentUtil.populateRecurringPaymentDetails(moneyAccountIdentifier, paymentDtoKeyedObj);

            if (recurringPaymentDetails != null) {
                recurringPaymentDetails = paymentIntegrationService.validatePayment(recurringPaymentDetails, errors);
                confirmPaymentDto = PaymentUtil.toConfirmPaymentDto(recurringPaymentDetails, paymentDtoKeyedObj);
            }
        } else {
            paymentDetails = PaymentUtil.populatePaymentDetails(moneyAccountIdentifier, paymentDtoKeyedObj);
            if (paymentDetails != null) {
                paymentDetails = paymentIntegrationService.validatePayment(paymentDetails, errors);
                confirmPaymentDto = PaymentUtil.toConfirmPaymentDto(paymentDetails, paymentDtoKeyedObj);
            }
        }

        confirmPaymentDto = handleErrors(errors, confirmPaymentDto);
        return confirmPaymentDto;
    }

    @Override
    public PaymentDto validate(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        return validatePayment(paymentDtoKeyedObj, serviceErrors);
    }

    public PaymentDto makePayment(PaymentDto paymentDtoKeyedObj) {
        ServiceErrors serviceErrors = null;
        PortfolioRequest portfolioRequest = new PortfolioRequestModel();
        portfolioRequest.setPortfolioId(paymentDtoKeyedObj.getKey().getAccountId());

        PaymentDto confirmPaymentDto = new PaymentDto(paymentDtoKeyedObj.getKey());

        PaymentDetails paymentDetails = null;
        RecurringPaymentDetails recurringPaymentDetails = null;
        ServiceErrors errors = new ServiceErrorsImpl();

        // Call PAY_DET service to get Money Account Identifier
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(paymentDtoKeyedObj.getKey().getAccountId());
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
        // validating account before submitting payment
        if (isValidTransaction(payeeDetails, paymentDtoKeyedObj.getToPayteeDto())) {
            MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
            moneyAccountIdentifier.setMoneyAccountId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());

            if (paymentDtoKeyedObj.getIsRecurring() == true) {
                recurringPaymentDetails = PaymentUtil.populateRecurringPaymentDetails(moneyAccountIdentifier, paymentDtoKeyedObj);

                if (recurringPaymentDetails != null) {
                    recurringPaymentDetails = paymentIntegrationService.submitPayment(recurringPaymentDetails, errors);
                    confirmPaymentDto = PaymentUtil.toConfirmPaymentDto(recurringPaymentDetails, paymentDtoKeyedObj);
                }
            } else {
                paymentDetails = PaymentUtil.populatePaymentDetails(moneyAccountIdentifier, paymentDtoKeyedObj);
                if (paymentDetails != null) {
                    paymentDetails = paymentIntegrationService.submitPayment(paymentDetails, errors);
                    confirmPaymentDto = PaymentUtil.toConfirmPaymentDto(paymentDetails, paymentDtoKeyedObj);
                }
            }
            logPaymentDetails(paymentDtoKeyedObj, confirmPaymentDto);
        } else {
            ServiceError error = new ServiceErrorImpl();
            error.setId(Constants.ACCT_NOT_IN_PAYEE_LIST);
            errors.addError(error);
        }

        confirmPaymentDto = handleErrors(errors, confirmPaymentDto);
        return confirmPaymentDto;
    }

    private boolean isValidTransaction(PayeeDetails payeeDetails, PayeeDto payee) {
        boolean isValid = false;
        if (null != payee && null != payeeDetails) {
            if (null == payee.getSaveToList() || payee.getSaveToList().isEmpty()) {
                return true;
            } else {
                if (payee.getPayeeType().equals(PayeeType.LINKED.toString())) {
                    List<LinkedAccount> linkedAccounts = payeeDetails.getLinkedAccountList();
                    if (linkedAccounts != null) {
                        for (LinkedAccount account : linkedAccounts) {
                            if (account.getBsb().equals(payee.getCode())
                                    && account.getAccountNumber().equals(payee.getAccountId())) {
                                isValid = true;
                                break;
                            }
                        }
                    }
                } else if (payee.getPayeeType().equals(PayeeType.PAY_ANYONE.toString())) {
                    List<PayAnyOne> payAnyOnes = payeeDetails.getPayanyonePayeeList();
                    if (payAnyOnes != null) {
                        for (PayAnyOne account : payAnyOnes) {
                            if (account.getBsb().equals(payee.getCode())
                                    && account.getAccountNumber().equals(payee.getAccountId())) {
                                isValid = true;
                                break;
                            }
                        }
                    }
                } else if (payee.getPayeeType().equals(PayeeType.BPAY.toString())) {
                    List<Biller> billers = payeeDetails.getBpayBillerPayeeList();
                    if (billers != null) {
                        for (Biller account : billers) {
                            if (account.getBillerCode().equals(payee.getCode()) && account.getCRN().equals(payee.getCrn())) {
                                isValid = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return isValid;
    }

    @Override
    public PaymentDto submit(PaymentDto paymentDtoKeyedObj, ServiceErrors serviceErrors) {
        PaymentDto paymentDtoObj = makePayment(paymentDtoKeyedObj);
        if (paymentDtoObj.getTransactionDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
            Date transactionDate = new Date(paymentDtoObj.getTransactionDate());
            if (DateUtil.isToday(transactionDate)) {
                paymentDtoObj.setTransactionDate(DateUtil.formatCurrentTransactionDate());
            } else {
                paymentDtoObj.setTransactionDate(dateFormat.format(transactionDate).toString());
            }
        }

        if(CollectionUtils.isEmpty(paymentDtoObj.getErrors())) {
            receiptObj.put(paymentDtoObj.getRecieptNumber(), paymentDtoObj);
        }

        return paymentDtoObj;
    }

    @Override
    public Map<String, PaymentDto> loadPaymentReciepts() {
        // TODO Auto-generated method stub
        return receiptObj;
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

    private void logPaymentDetails(PaymentDto confirmPaymentDto, PaymentDto paymentDto) {

        LOGGER.info(LoggingConstants.PAYMENTS_SUBMIT + "BEGIN");
        if (confirmPaymentDto.getFromPayDto() != null && confirmPaymentDto.getToPayteeDto() != null) {
            LOGGER.info("FROM_ACCOUNT_ID" + LoggingConstants.PAYMENTS_DELIMITER + confirmPaymentDto.getFromPayDto().getAccountId()
                    + LoggingConstants.ONBOARDING_DELIMITER + "FROM_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER
                    + confirmPaymentDto.getFromPayDto().getCode() + LoggingConstants.ONBOARDING_DELIMITER + "TO_ACCOUNT_ID"
                    + LoggingConstants.PAYMENTS_DELIMITER + confirmPaymentDto.getToPayteeDto().getAccountId()
                    + LoggingConstants.ONBOARDING_DELIMITER + "TO_ACCOUNT_BSB" + LoggingConstants.PAYMENTS_DELIMITER
                    + confirmPaymentDto.getToPayteeDto().getCode() + LoggingConstants.ONBOARDING_DELIMITER + "PAYMENT_AMOUNT"
                    + LoggingConstants.PAYMENTS_DELIMITER + confirmPaymentDto.getAmount() + LoggingConstants.ONBOARDING_DELIMITER
                    + "PAYEE_TYPE" + LoggingConstants.PAYMENTS_DELIMITER + confirmPaymentDto.getToPayteeDto().getPayeeType()
                    + LoggingConstants.ONBOARDING_DELIMITER + "TRANSACTION_DATE" + LoggingConstants.PAYMENTS_DELIMITER
                    + paymentDto.getTransactionDate());
        }
        LOGGER.info(LoggingConstants.PAYMENTS_SUBMIT + "END");
    }

}
