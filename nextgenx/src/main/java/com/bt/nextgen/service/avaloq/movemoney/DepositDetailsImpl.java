package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.LazyServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.movemoney.ContributionType;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.DepositStatus;
import com.bt.nextgen.service.integration.movemoney.OrderType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@LazyServiceBean()
public class DepositDetailsImpl extends DepositDetailsBaseImpl implements DepositDetails {
    private static final String XPATH_CONSTANT = "doc_head_list/doc_head/";

    @ServiceElement(xpath = XPATH_CONSTANT + "trx/val")
    private String depositId;

    @ServiceElement(xpath = XPATH_CONSTANT + "trans_seq_nr/val")
    private String transactionSeq;

    @ServiceElement(xpath = XPATH_CONSTANT + "ui_trx_status_id/val", staticCodeCategory = "ORDER_STATUS")
    private DepositStatus status;

    @ServiceElement(xpath = XPATH_CONSTANT + "amount/val", converter = BigDecimalConverter.class)
    private BigDecimal depositAmount;

    @ServiceElement(xpath = XPATH_CONSTANT + "trx_descn/val")
    private String description;

    @ServiceElement(xpath = XPATH_CONSTANT + "first_date/val", converter = DateTimeTypeConverter.class)
    private DateTime transactionDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "contri_type_id/val", staticCodeCategory = "SUPER_CONTRIBUTIONS_TYPE")
    private ContributionType contributionType;

    @ServiceElement(xpath = XPATH_CONSTANT + "payer_bsb/val")
    private String payerBsb;

    @ServiceElement(xpath = XPATH_CONSTANT + "payer_acct_no/val")
    private String payerAccount;

    @ServiceElement(xpath = XPATH_CONSTANT + "payer/val")
    private String payerName;

    @ServiceElement(xpath = XPATH_CONSTANT + "payee/val")
    private String payeeMoneyAccount;

    @ServiceElement(xpath = XPATH_CONSTANT + "freq_id/val", staticCodeCategory = "CODES_PAYMENT_FREQUENCIES")
    private RecurringFrequency recurringFrequency;

    @ServiceElement(xpath = XPATH_CONSTANT + "order_type_id/val", staticCodeCategory = "ORDER_TYPE")
    private OrderType orderType;

    private CurrencyType currencyType;
    private String receiptNumber;
    private DateTime depositDate;
    private List<ValidationError> errors;
    private List<ValidationError> warnings;

    public DepositDetailsImpl() {
        // default constructor
    }

    @SuppressWarnings("squid:S00107")
    public DepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier, PayAnyoneAccountDetails payAnyoneAccountDetails,
            BigDecimal depositAmount, CurrencyType currencyType, String description, DateTime transactionDate,
            String receiptNumber, ContributionType contributiontype, RecurringFrequency recurringFrequency,
            List<ValidationError> warnings, String transactionSeq) {
        if (moneyAccountIdentifier != null) {
            this.payeeMoneyAccount = moneyAccountIdentifier.getMoneyAccountId();
        }

        if (payAnyoneAccountDetails != null) {
            this.payerAccount = payAnyoneAccountDetails.getAccount();
            this.payerBsb = payAnyoneAccountDetails.getBsb();
        }

        this.depositAmount = depositAmount;
        this.currencyType = currencyType;
        this.description = description;
        this.transactionDate = transactionDate;
        this.contributionType = contributiontype;
        this.receiptNumber = receiptNumber;
        this.transactionSeq = transactionSeq;
        this.recurringFrequency = recurringFrequency;

        if (warnings != null) {
            this.warnings = warnings;
        }
    }

    public DepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier, PayAnyoneAccountDetails payAnyoneAccountDetails,
            BigDecimal depositAmount, CurrencyType currencyType, String description, DateTime transactionDate,
            ContributionType contributiontype) {
        this(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate, null,
                contributiontype, null, null, null);
    }

    @SuppressWarnings("squid:S00107")
    public DepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier, PayAnyoneAccountDetails payAnyoneAccountDetails,
            BigDecimal depositAmount, CurrencyType currencyType, String description, DateTime transactionDate,
            String receiptNumber, DateTime depositDate, ContributionType contributiontype, RecurringFrequency recurringFrequency,
            List<ValidationError> warnings,
            String transactionSeq) {
        this(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                receiptNumber, contributiontype, recurringFrequency, warnings, transactionSeq);
        this.depositDate = depositDate;
    }

    @SuppressWarnings("squid:S00107")
    public DepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier, PayAnyoneAccountDetails payAnyoneAccountDetails,
            BigDecimal depositAmount, CurrencyType currencyType, String description, DateTime transactionDate,
            String receiptNumber, DateTime depositDate, ContributionType contributiontype, RecurringFrequency recurringFrequency,
            String transactionSeq) {
        this(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                receiptNumber, depositDate, contributiontype, recurringFrequency, null, transactionSeq);
    }

    @SuppressWarnings("squid:S00107")
    public DepositDetailsImpl(MoneyAccountIdentifier moneyAccountIdentifier, PayAnyoneAccountDetails payAnyoneAccountDetails,
            BigDecimal depositAmount, CurrencyType currencyType, String description, DateTime transactionDate,
            String receiptNumber, DateTime depositDate, ContributionType contributiontype) {
        this(moneyAccountIdentifier, payAnyoneAccountDetails, depositAmount, currencyType, description, transactionDate,
                receiptNumber, depositDate, contributiontype, null, null);
    }

    @Override
    // TODO: remove this once all references have been change to use payeeMoneyAccount
    public MoneyAccountIdentifier getMoneyAccountIdentifier() {
        MoneyAccountIdentifier moneyAccountIdentifier = new MoneyAccountIdentifierImpl();
        moneyAccountIdentifier.setMoneyAccountId(this.payeeMoneyAccount);
        return moneyAccountIdentifier;
    }

    // TODO: remove this once all references have been change to use payeeMoneyAccount
    public void setMoneyAccountIdentifier(MoneyAccountIdentifier moneyAccountIdentifier) {
        if (moneyAccountIdentifier != null) {
            this.payeeMoneyAccount = moneyAccountIdentifier.getMoneyAccountId();
        }
    }

    @Override
    // TODO: remove this once all references have been change to use payerBsb and payerAccount
    public PayAnyoneAccountDetails getPayAnyoneAccountDetails() {
        PayAnyoneAccountDetails payAnyoneAccountDetails = new PayAnyoneAccountDetailsImpl();
        payAnyoneAccountDetails.setAccount(this.payerAccount);
        payAnyoneAccountDetails.setBsb(this.payerBsb);
        return payAnyoneAccountDetails;
    }

    // TODO: remove this once all references have been change to use payerBsb and payerAccount
    public void setPayAnyoneAccountDetails(PayAnyoneAccountDetails payAnyoneAccountDetails) {
        if (payAnyoneAccountDetails != null) {
            this.payerAccount = payAnyoneAccountDetails.getAccount();
            this.payerBsb = payAnyoneAccountDetails.getBsb();
        }
    }

    @Override
    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    @Override
    public String getTransactionSeq() {
        return transactionSeq;
    }

    public void setTransactionSeq(String transactionSeq) {
        this.transactionSeq = transactionSeq;
    }

    @Override
    public DepositStatus getStatus() {
        return status;
    }

    public void setStatus(DepositStatus status) {
        this.status = status;
    }

    @Override
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(DateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public ContributionType getContributionType() {
        return contributionType;
    }

    public void setContributionType(ContributionType contributionType) {
        this.contributionType = contributionType;
    }

    @Override
    public String getPayerBsb() {
        return payerBsb;
    }

    public void setPayerBsb(String payerBsb) {
        this.payerBsb = payerBsb;
    }

    @Override
    public String getPayerAccount() {
        return payerAccount;
    }

    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    @Override
    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    @Override
    public String getPayeeMoneyAccount() {
        return payeeMoneyAccount;
    }

    public void setPayeeMoneyAccount(String payeeMoneyAccount) {
        this.payeeMoneyAccount = payeeMoneyAccount;
    }

    @Override
    public RecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }

    public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }

    @Override
    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    @Override
    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    @Override
    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    @Override
    public DateTime getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(DateTime depositDate) {
        this.depositDate = depositDate;
    }

    @Override
    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }

    @Override
    public List<ValidationError> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ValidationError> warnings) {
        this.warnings = warnings;
    }
}
