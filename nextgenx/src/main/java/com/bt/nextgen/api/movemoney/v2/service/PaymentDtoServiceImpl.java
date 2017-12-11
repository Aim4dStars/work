package com.bt.nextgen.api.movemoney.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.LinkedAccountStatusDto;
import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.api.movemoney.v2.model.EndPaymentDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.api.movemoney.v2.util.PaymentUtil;
import com.bt.nextgen.api.movemoney.v2.util.TransactionReceiptHelper;
import com.bt.nextgen.api.movemoney.v2.validation.MovemoneyDtoErrorMapper;
import com.bt.nextgen.api.movemoney.v3.util.DepositUtils;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.web.model.AccountVerificationStatus;
import com.bt.nextgen.payments.web.model.TwoFactorAccountVerificationKey;
import com.bt.nextgen.payments.web.model.TwoFactorRuleModel;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.account.AccountSubType;
import com.bt.nextgen.service.avaloq.account.AvailableCashImpl;
import com.bt.nextgen.service.avaloq.movemoney.BpayBillerImpl;
import com.bt.nextgen.service.avaloq.movemoney.PaymentDetailsImpl;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.rules.AvaloqRulesIntegrationService;
import com.bt.nextgen.service.avaloq.rules.RuleAction;
import com.bt.nextgen.service.avaloq.rules.RuleCond;
import com.bt.nextgen.service.avaloq.rules.RuleImpl;
import com.bt.nextgen.service.avaloq.rules.RuleType;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AvailableCash;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.movemoney.BpayBiller;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentActionType;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PaymentIntegrationService;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.btfin.abs.err.v1_0.ErrType.FA;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Service("PaymentDtoServiceV2")
@SuppressWarnings({"squid:MethodCyclomaticComplexity", "squid:S1200"})
public class PaymentDtoServiceImpl implements PaymentDtoService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentDtoServiceImpl.class);

    @Autowired
    private PaymentIntegrationService paymentIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private MovemoneyDtoErrorMapper movemoneyDtoErrorMapper;

    @Autowired
    private AvaloqRulesIntegrationService avaloqRulesIntegrationService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private TransactionReceiptHelper transactionReceiptHelper;

    @Autowired
    protected StaticIntegrationService staticIntegrationService;

    @Autowired
    @Qualifier("acctPermissionService")
    private PermissionAccountDtoService acctPermissionService;

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
        toPayeeDto.setSaveToList("save");
        LinkedAccountStatusDto linkedAccountStatus = DepositUtils.linkedAccountStatus(bankAccount,staticIntegrationService);
        toPayeeDto.setLinkedAccountStatus(linkedAccountStatus);
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
                paymentDto.setToPayeeDto(toPayeeDto);

                lstPaymentDto.add(paymentDto);
            }
        } else {
            PaymentDto paymentDtoPayAnyOne = new PaymentDto();
            PayeeDto payeeDto = new PayeeDto();
            payeeDto.setPayeeType(PayeeType.PAY_ANYONE.toString());
            paymentDtoPayAnyOne.setToPayeeDto(payeeDto);
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
                paymentDto.setToPayeeDto(toPayeeDto);
                paymentDto.setFromPayDto(fromPayeeDto);

                lstPaymentDto.add(paymentDto);
            }
        } else {
            PaymentDto paymentDtobPAY = new PaymentDto();
            PayeeDto payeeDto = new PayeeDto();
            payeeDto.setPayeeType(PayeeType.BPAY.toString());
            paymentDtobPAY.setToPayeeDto(payeeDto);
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
    public List<PaymentDto> search(AccountKey accountKey, List<ApiSearchCriteria> criteria, ServiceErrors serviceErrors) {
        String model = null;

        for (int i = 0; i < criteria.size(); i++) {
            switch (criteria.get(i).getProperty()) {
                case Attribute.MOVE_MONEY_MODEL:
                    model = criteria.get(i).getValue();
                    break;
                default:
                    break;
            }
        }
        return loadPayees(accountKey.getAccountId(), model, serviceErrors);
    }

    private List<PaymentDto> loadPayees(String encodedAccountId, String model, ServiceErrors serviceErrors) {
        final boolean payAnyoneAllowed = acctPermissionService.canTransact(encodedAccountId, "account.payment.anyone.create");
        final boolean bPayAllowed = acctPermissionService.canTransact(encodedAccountId, "account.payment.anyone.create");
        final String accountId = EncodedString.toPlainText(encodedAccountId);

        AvailableCash accountBalance = new AvailableCashImpl();
        if (null == model) {
            accountBalance = accountIntegrationService
                    .loadAvailableCash(com.bt.nextgen.service.integration.account.AccountKey.valueOf(accountId), serviceErrors);
        }
        PayeeDetails payeeDetails = getPayeeDetails(accountId, serviceErrors);

        // Get Linked Account. Sort and Move Primary on Top
        List<LinkedAccount> lstLinkedAccounts = payeeDetails.getLinkedAccountList();
        if (isNotEmpty(lstLinkedAccounts)) {
            lstLinkedAccounts = PaymentUtil.sortLinkedAccount(lstLinkedAccounts, model);
            lstLinkedAccounts = PaymentUtil.movePrimaryOnTop(lstLinkedAccounts);
        }
        List<PaymentDto> lstPaymentDto = toPaymentDto(accountId, lstLinkedAccounts, payeeDetails,
                accountBalance.getAvailableCash());

        if (payAnyoneAllowed) {
            // Get Payanyone Account. Sort on Nick name / Account name
            List<PayAnyOne> lstPayAnyOneAccounts = payeeDetails.getPayanyonePayeeList();
            if (isNotEmpty(lstPayAnyOneAccounts)) {
                lstPayAnyOneAccounts = PaymentUtil.sortPayAnyoneAccount(lstPayAnyOneAccounts, model);
                lstPaymentDto.addAll(toPaymentDtoPayAnyOne(accountId, lstPayAnyOneAccounts, payeeDetails, accountBalance.getAvailableCash()));
            }
        }

        if (bPayAllowed) {
            // Get BPay Acounts. Sort on Nick name / Account name
            List<Biller> lstbPAYAccounts = payeeDetails.getBpayBillerPayeeList();
            if (bPayAllowed && isNotEmpty(lstbPAYAccounts)) {
                // TODO Uncomment when CRN Type is required
            /*
             * for (Biller payee : lstBPAYAccounts) { BpayBiller bpayBiller =
             * bpayBillerCodeRepository.load(payee.getBillerCode()); if(null!=bpayBiller && null!=bpayBiller.getCrnType())
             * payee.setCRNType(bpayBiller.getCrnType().name()); }
             */
                lstbPAYAccounts = PaymentUtil.sortBPayAccount(lstbPAYAccounts, model);
                lstPaymentDto.addAll(toPaymentDtoForBillers(accountId, lstbPAYAccounts, payeeDetails, accountBalance.getAvailableCash()));
            }
        }

        return lstPaymentDto;
    }

    @Override
    public PaymentDto submit(PaymentDto paymentDto, ServiceErrors serviceErrors) {
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDetails paymentDetails;
        boolean saveReceipt = true;

        if (paymentDto instanceof EndPaymentDto) {
            PaymentDetailsImpl paymentDetailsImpl = new PaymentDetailsImpl();
            paymentDetailsImpl.setPositionId(EncodedString.toPlainText(paymentDto.getTransactionId()));
            if (((EndPaymentDto) paymentDto).getHasDrawdownInprogress() != null
                    && ((EndPaymentDto) paymentDto).getHasDrawdownInprogress()) {
                List<ValidationError> warnings = new ArrayList<>();
                warnings.add(new ValidationError("btfg$dd_occurred_close", null, null, ValidationError.ErrorType.WARNING));
                paymentDetailsImpl.setWarnings(warnings);
            }
            paymentDetails = paymentIntegrationService.endPayment(paymentDetailsImpl, errors);
            saveReceipt = false;
        } else {
            PayeeDetails payeeDetails = getPayeeDetails(EncodedString.toPlainText(paymentDto.getKey().getAccountId()),
                    serviceErrors);
            MoneyAccountIdentifier moneyAccountIdentifier = payeeDetails.getMoneyAccountIdentifier();

            if (isValidTransaction(paymentDto.getKey().getAccountId(), payeeDetails, paymentDto.getToPayeeDto())) {
                paymentDetails = paymentIntegrationService.submitPayment(toPaymentDetails(moneyAccountIdentifier, paymentDto, errors),
                        errors);
            } else {
                PaymentDto paymentErrorDto = new PaymentDto();
                List<DomainApiErrorDto> errorList = new ArrayList<>();
                DomainApiErrorDto error = new DomainApiErrorDto("Err.IP-0315", null, "Payee is not a linked account",
                        cmsService.getContent("Err.IP-0315"), ErrorType.ERROR);
                //TODO: Add SAFI Authentication specific Error
                errorList.add(error);
                paymentErrorDto.setErrors(errorList);
                return paymentErrorDto;
            }
        }

        filterFatalErrors(errors, serviceErrors);

        final PaymentDto paymentDtoData = toPaymentDto(paymentDetails, paymentDto);
        if(saveReceipt) {
            transactionReceiptHelper.storeReceiptData(paymentDtoData);
        }
        return paymentDtoData;
    }

    protected boolean isValidTransaction(String accountId, PayeeDetails payeeDetails, PayeeDto payee) {
        boolean isValid = false;
        final boolean payAnyoneAllowed = acctPermissionService.canTransact(accountId, "account.payment.anyone.create");
        final boolean bPayAllowed = acctPermissionService.canTransact(accountId, "account.payment.anyone.create");

        if (payee != null && payeeDetails != null) {
            final boolean safiAuthenticated = getSafiAuthResult(payee);

            if (payee.getPayeeType().equals(PayeeType.LINKED.toString())) {
                if (safiAuthenticated) {
                    isValid = true;
                } else {
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
                }
            } else if (payee.getPayeeType().equals(PayeeType.PAY_ANYONE.toString()) && payAnyoneAllowed) {
                if (safiAuthenticated) {
                    isValid = true;
                } else {
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
                }
            } else if (payee.getPayeeType().equals(PayeeType.BPAY.toString()) && bPayAllowed) {
                if (safiAuthenticated) {
                    isValid = true;
                } else {
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

    protected boolean getSafiAuthResult(PayeeDto payee) {
        if (payee.getPayeeType().equalsIgnoreCase(PayeeType.LINKED.toString()) || StringUtils.isEmpty(payee.getSaveToList())) {
            TwoFactorRuleModel ruleModel = (TwoFactorRuleModel) httpSession
                    .getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
            if (ruleModel != null) {
                TwoFactorAccountVerificationKey accountVerificationKey = new TwoFactorAccountVerificationKey(payee.getAccountId(),
                        payee.getCode());
                logger.info("Found AccountStatusMap: {}", ruleModel.getAccountStatusMap());
                logger.info("Found AccountStatusMap for linked account: {}",
                        ruleModel.getAccountStatusMap().get(accountVerificationKey));
                logger.info("Found AuthStatus: {}",
                        ruleModel.getAccountStatusMap().get(accountVerificationKey).isAuthenticationDone());
                return ruleModel.getAccountStatusMap().get(accountVerificationKey).isAuthenticationDone();
            }
        }

        return false;
    }

    @Override
    public PaymentDto validate(PaymentDto paymentDto, ServiceErrors serviceErrors) {
        PayeeDetails payeeDetails = getPayeeDetails(EncodedString.toPlainText(paymentDto.getKey().getAccountId()), serviceErrors);
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDetails paymentDetails = paymentIntegrationService
                .validatePayment(toPaymentDetails(payeeDetails.getMoneyAccountIdentifier(), paymentDto, errors), errors);

        filterFatalErrors(errors, serviceErrors);
        PaymentDto validatePaymentDto = toPaymentDto(paymentDetails, paymentDto);
        setTwoFactorAuthDetails(paymentDto, validatePaymentDto);
        return validatePaymentDto;
    }

    private PaymentDto toPaymentDto(PaymentDetails paymentDetails, PaymentDto requestingPaymentDto) {
        PaymentDto paymentDto = new PaymentDto();
        if (paymentDetails.getPositionId() != null) {
            paymentDto.setTransactionId(EncodedString.fromPlainText(paymentDetails.getPositionId()).toString());
        }
        paymentDto.setAmount(paymentDetails.getAmount());
        paymentDto.setReceiptNumber(paymentDetails.getReceiptNumber());
        paymentDto.setDescription(paymentDetails.getBenefeciaryInfo());
        paymentDto.setTransactionDate(new DateTime(paymentDetails.getTransactionDate()));
        paymentDto.setFrequency(
                paymentDetails.getRecurringFrequency() == null ? null : paymentDetails.getRecurringFrequency().getDescription());
        paymentDto.setRecurring(paymentDetails.getRecurringFrequency() != null
                && paymentDetails.getRecurringFrequency() != RecurringFrequency.Once);
        paymentDto.setIndexationType(
                paymentDetails.getIndexationType() == null ? null : paymentDetails.getIndexationType().getLabel());
        paymentDto.setIndexationAmount(paymentDetails.getIndexationAmount());
        paymentDto.setWithdrawalType(
                paymentDetails.getWithdrawalType() == null ? null : paymentDetails.getWithdrawalType().getLabel());
        paymentDto.setPensionPaymentType(
                paymentDetails.getPensionPaymentType() == null ? null : paymentDetails.getPensionPaymentType().getLabel());
        paymentDto.setReceiptId(EncodedString.fromPlainText(paymentDetails.getReceiptNumber()).toString());
        if(paymentDto.getIsRecurring()){
            paymentDto.setRepeatEndDate(paymentDetails.getEndDate() != null ? new DateTime(paymentDetails.getEndDate()) : null);
            paymentDto.setEndRepeatNumber(paymentDetails.getMaxCount());
        }
        addPayeeDtos(paymentDto, requestingPaymentDto);

        addValidations(paymentDto, paymentDetails);

        return paymentDto;
    }

    private void setTwoFactorAuthDetails(PaymentDto requestingPaymentDto, PaymentDto paymentDto) {
        TwoFactorRuleModel ruleModel = new TwoFactorRuleModel();

        if (null != httpSession.getAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER)) {
            logger.info("Already found var {} in session, remove it", Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
            httpSession.removeAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER);
        }

        final TwoFactorAccountVerificationKey accountVerificationKey =
                new TwoFactorAccountVerificationKey(requestingPaymentDto.getToPayeeDto().getAccountId(), requestingPaymentDto.getToPayeeDto().getCode());

        if (requestingPaymentDto.getToPayeeDto().getPayeeType().equalsIgnoreCase(PayeeType.LINKED.toString())) {
            final RuleImpl rule = isTwoFAReq(requestingPaymentDto);
            if (rule != null && (rule.getAction() == RuleAction.CHK || rule.getAction() == RuleAction.CHK_UPD)) {
                logger.info("Found avaloq rule with id:{}, type:{}, action:{}", rule.getRuleId(), rule.getType(), rule.getAction());
                paymentDto.setTwoFaRequired(true);
                ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(rule.getRuleId(), false));
            } else {
                paymentDto.setTwoFaRequired(false);
                ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(rule != null ? rule.getRuleId() : null, true));
            }
        } else {
            paymentDto.setTwoFaRequired(false);
            ruleModel.addVerificationStatus(accountVerificationKey, new AccountVerificationStatus(null, true));
        }

        httpSession.setAttribute(Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, ruleModel);
        logger.info("Session variable: {}, value: {}", Constants.SAFI_PAYMENT_SESSION_IDENTIFIER, ruleModel.getAccountStatusMap().get(accountVerificationKey));
    }

    private RuleImpl isTwoFAReq(PaymentDto requestingPaymentDto) {
        Map<RuleCond, String> ruleCondStringMap = new HashMap<>();
        ruleCondStringMap.put(RuleCond.LINK_ACC_NR, requestingPaymentDto.getToPayeeDto().getAccountId());
        ruleCondStringMap.put(RuleCond.LINK_BSB, requestingPaymentDto.getToPayeeDto().getCode());
        ruleCondStringMap.put(RuleCond.BP_ID, EncodedString.toPlainText(requestingPaymentDto.getKey().getAccountId()));
        return avaloqRulesIntegrationService.retrieveTwoFaRule(RuleType.LINK_ACC, ruleCondStringMap, new FailFastErrorsImpl());
    }

    private List<PaymentDto> toPaymentDto(String accountId, List<LinkedAccount> lstPayeeAccounts, PayeeDetails payeeDetails,
                                          BigDecimal availableCash) {
        List<PaymentDto> lstPaymentDto = new ArrayList<PaymentDto>();

        AccountKey accountKey = new AccountKey(accountId);

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
                paymentDto.setToPayeeDto(toPayeeDto);

                lstPaymentDto.add(paymentDto);
            }
        } else {
            PaymentDto paymentDtoLinked = new PaymentDto();
            PayeeDto payeeDto = new PayeeDto();
            payeeDto.setPayeeType(PayeeType.LINKED.toString());
            paymentDtoLinked.setToPayeeDto(payeeDto);
            paymentDtoLinked.setDailyLimitDto(populateLimitsForAccount(payeeDetails, PayeeType.LINKED.toString(), availableCash));
            paymentDtoLinked.setMaccId(payeeDetails.getMoneyAccountIdentifier().getMoneyAccountId());

            lstPaymentDto.add(paymentDtoLinked);
        }
        return lstPaymentDto;
    }

    private void addPayeeDtos(PaymentDto paymentDto, PaymentDto requestingPaymentDto) {
        paymentDto.setToPayeeDto(requestingPaymentDto.getToPayeeDto());
        paymentDto.setFromPayDto(requestingPaymentDto.getFromPayDto());
    }

    private PayeeDetails getPayeeDetails(String accountId, ServiceErrors serviceErrors) {
        WrapAccountIdentifierImpl wrapAccountIdentifierImpl = new WrapAccountIdentifierImpl();
        wrapAccountIdentifierImpl.setBpId(accountId);
        PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifierImpl, serviceErrors);
        return payeeDetails;
    }

    PaymentDetails toPaymentDetails(MoneyAccountIdentifier moneyAccountIdentifier, PaymentDto paymentDto, ServiceErrors serviceErrors) {
        final com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(paymentDto.getKey().getAccountId()));
        final WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        final PaymentDetailsImpl paymentDetails = new PaymentDetailsImpl();

        paymentDetails.setAccountKey(accountKey);
        paymentDetails.setModificationSeq(account.getModificationSeq());// Modification sequence is required for BP update

        if (paymentDto.getTransactionId() != null) {
            paymentDetails.setPositionId(EncodedString.toPlainText(paymentDto.getTransactionId()));
        }
        paymentDetails.setAmount(paymentDto.getAmount());
        paymentDetails.setBenefeciaryInfo(paymentDto.getDescription());
        paymentDetails.setCurrencyType(CurrencyType.AustralianDollar);
        paymentDetails.setMoneyAccount(moneyAccountIdentifier);
        paymentDetails.setPayeeName(StringUtils.isNotBlank(paymentDto.getToPayeeDto().getNickname())
                ? paymentDto.getToPayeeDto().getNickname() : paymentDto.getToPayeeDto().getAccountName());
        WithdrawalType withdrawalType = WithdrawalType.fromLabel(paymentDto.getWithdrawalType());
        if (WithdrawalType.PENSION_ONE_OFF_PAYMENT.equals(withdrawalType) || WithdrawalType.LUMP_SUM_WITHDRAWAL.equals(withdrawalType)) {
            paymentDetails.setTransactionDate(null);
        } else {
            paymentDetails.setTransactionDate(paymentDto.getTransactionDate().toDate());
        }
        paymentDetails.setPaymentDate(paymentDto.getTransactionDate().toDate());
        if (paymentDto.isRecurring()) {
            paymentDetails.setRecurringFrequency(RecurringFrequency.getRecurringFrequencyByDescription(paymentDto.getFrequency()));
            // Set max count and end date only for non-(regular pension payments)
            if (!AccountSubType.PENSION.equals(account.getSuperAccountSubType())) {
                paymentDetails.setEndDate(paymentDto.getRepeatEndDate() == null ? null : paymentDto.getRepeatEndDate().toDate());
                paymentDetails.setMaxCount(paymentDto.getEndRepeatNumber());
            }
        }
        paymentDetails.setIndexationType(IndexationType.fromLabel(paymentDto.getIndexationType()));
        paymentDetails.setIndexationAmount(paymentDto.getIndexationAmount());
        paymentDetails.setWithdrawalType(WithdrawalType.fromLabel(paymentDto.getWithdrawalType()));
        paymentDetails.setPensionPaymentType(PensionPaymentType.fromLabel(paymentDto.getPensionPaymentType()));
        paymentDetails.setPaymentAction(PaymentActionType.fromLabel(paymentDto.getPaymentAction())); //saved payments action
        paymentDetails.setTransactionSeqNo(paymentDto.getTransSeqNo()); //saved payments sequence number
        if (StringUtils.isNotBlank(paymentDto.getReceiptNumber())) { //saved payments doc Id
            paymentDetails.setDocId(paymentDto.getReceiptNumber());
        }
        addPayeeDetails(paymentDto, paymentDetails);
        if (paymentDto.getWarnings() != null) {
            paymentDetails.setWarnings(movemoneyDtoErrorMapper.mapWarnings(paymentDto.getWarnings()));
        }
        paymentDetails.setBusinessChannel(paymentDto.getBusinessChannel());
        paymentDetails.setClientIp(paymentDto.getClientIp());

        return paymentDetails;
    }

    private void addPayeeDetails(PaymentDto paymentDto, PaymentDetailsImpl paymentDetails) {
        if (StringUtils.equalsIgnoreCase(paymentDto.getToPayeeDto().getPayeeType(), "BPAY")) {
            BpayBiller bpayBiller = new BpayBillerImpl();
            bpayBiller.setPayeeName(paymentDto.getToPayeeDto().getAccountName());
            bpayBiller.setBillerCode(paymentDto.getToPayeeDto().getCode());
            bpayBiller.setCustomerReferenceNo(paymentDto.getToPayeeDto().getCrn());
            paymentDetails.setBpayBiller(bpayBiller);
        } else {
            PayAnyoneAccountDetails payAnyOneAccountDetails = new PayAnyoneAccountDetailsImpl();
            payAnyOneAccountDetails.setAccount(EncodedString.toPlainText(paymentDto.getToPayeeDto().getAccountKey()));
            payAnyOneAccountDetails.setBsb(paymentDto.getToPayeeDto().getCode());
            paymentDetails.setPayAnyoneBeneficiary(payAnyOneAccountDetails);
        }
    }

    private void addValidations(PaymentDto paymentDto, PaymentDetails paymentDetails) {
        if (paymentDetails.getErrors() != null) {
            paymentDto.setErrors(movemoneyDtoErrorMapper.map(paymentDetails.getErrors()));
        }

        if (paymentDetails.getWarnings() != null) {
            paymentDto.setWarnings(movemoneyDtoErrorMapper.map(paymentDetails.getWarnings()));
        }
    }

    @Override
    public PaymentDto update(PaymentDto requestPaymentDto, ServiceErrors serviceErrors) {
        ServiceErrors errors = new ServiceErrorsImpl();
        PaymentDetails paymentDetails = null;
        if (("cancelregular").equalsIgnoreCase(requestPaymentDto.getPaymentAction()) ||
                ("canceloneoff").equalsIgnoreCase(requestPaymentDto.getPaymentAction())) {
            paymentDetails = paymentIntegrationService.endPayment(getCancelPaymentDetails(requestPaymentDto), errors);
        } else {
            PayeeDetails payeeDetails = getPayeeDetails(EncodedString.toPlainText(requestPaymentDto.getKey().getAccountId()), serviceErrors);
            paymentDetails = paymentIntegrationService
                    .savePayment(toPaymentDetails(payeeDetails.getMoneyAccountIdentifier(), requestPaymentDto, errors), errors);
        }
        filterFatalErrors(errors, serviceErrors);
        PaymentDto paymentDto = toPaymentDto(paymentDetails, requestPaymentDto);
        return paymentDto;
    }

    /**
     * Copy fatal errors from {@code source} to {@code target}.
     * Note that if {@link ServiceErrors} is a {@link FailFastErrorsImpl}, the error copying will trigger an exception.
     *
     * @param source    Source for errors.
     * @param target    Object to copy fatal errors to.
     */
    private void filterFatalErrors(ServiceErrors source, ServiceErrors target) {
        for (ServiceError error : source.getErrorList()) {
            if (FA.value().equals(error.getType())) {
                logger.error("Avaloq response contains fatal error: " + error.getMessage());
                target.addError(error);
            }
        }
    }

    private PaymentDetailsImpl getCancelPaymentDetails(PaymentDto requestPaymentDto) {
        PaymentDetailsImpl paymentDetailsImpl = new PaymentDetailsImpl();
        paymentDetailsImpl.setDocId(requestPaymentDto.getReceiptNumber());
        paymentDetailsImpl.setTransactionSeqNo(requestPaymentDto.getTransSeqNo());
        paymentDetailsImpl.setPaymentAction(PaymentActionType.fromLabel(requestPaymentDto.getPaymentAction()));
        paymentDetailsImpl.setWithdrawalType(WithdrawalType.fromLabel(requestPaymentDto.getWithdrawalType()));
        return paymentDetailsImpl;
    }
}
