package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.movemoney.BpayBiller;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PaymentActionType;
import com.bt.nextgen.service.integration.movemoney.PaymentDetails;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.movemoney.WithdrawalType;
import com.btfin.panorama.service.integration.RecurringFrequency;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class PaymentDetailsImpl implements PaymentDetails {

    private MoneyAccountIdentifier moneyAccount;
    private PayAnyoneAccountDetails payAnyoneBeneficiary;
    private BpayBiller bpayBiller;
    private BigDecimal amount;
    private CurrencyType currencyType;
    private String receiptNumber;
    private String benefeciaryInfo;
    private Date transactionDate;
    private String payeeName;
    private Date paymentDate;
    private String positionId;
    private String docId;
    private RecurringFrequency recurringFrequency;
    private Date startDate;
    private Date endDate;
    private BigInteger maxCount;
    private DateTime nextTransactionDate;
    private IndexationType indexationType;
    private BigDecimal indexationAmount;
    private WithdrawalType withdrawalType;
    private PensionPaymentType pensionPaymentType;
    private AccountKey accountKey;
    private String transactionSeqNo;
    private PaymentActionType paymentAction;
    private List<ValidationError> errors;
    private List<ValidationError> warnings;
    private String businessChannel;
    private String clientIp;
    private String modificationSeq;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * @return the moneyAccountId
     */
    @Override
    public MoneyAccountIdentifier getMoneyAccount() {
        return moneyAccount;
    }

    public void setMoneyAccount(MoneyAccountIdentifier moneyAccount) {
        this.moneyAccount = moneyAccount;
    }

    /**
     * @return the payAnyoneBeneficiary
     */
    @Override
    public PayAnyoneAccountDetails getPayAnyoneBeneficiary() {
        return payAnyoneBeneficiary;
    }

    public void setPayAnyoneBeneficiary(PayAnyoneAccountDetails payAnyoneBeneficiary) {
        this.payAnyoneBeneficiary = payAnyoneBeneficiary;
    }

    /**
     * @return the bpayBiller
     */
    @Override
    public BpayBiller getBpayBiller() {
        return bpayBiller;
    }

    public void setBpayBiller(BpayBiller bpayBiller) {
        this.bpayBiller = bpayBiller;
    }

    /**
     * @return the amount
     */
    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the currencyType
     */
    @Override
    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * @return the receiptNumber
     */
    @Override
    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    /**
     * @return the benefeciaryInfo
     */
    @Override
    public String getBenefeciaryInfo() {
        return benefeciaryInfo;
    }

    public void setBenefeciaryInfo(String benefeciaryInfo) {
        this.benefeciaryInfo = benefeciaryInfo;
    }

    /**
     * @return the transactionDate
     */
    @Override
    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * @return the payeeName
     */
    @Override
    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    /**
     * @return the paymentDate
     */
    @Override
    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * @return the positionId
     */
    @Override
    public String getPositionId() {
        return positionId;
    }


    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    @Override
    public String getDocId() {
        return docId;
    }
    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public RecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }

    public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public BigInteger getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(BigInteger maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public DateTime getNextTransactionDate() {
        return nextTransactionDate;
    }

    public void setNextTransactionDate(DateTime nextTransactionDate) {
        this.nextTransactionDate = nextTransactionDate;
    }

    @Override
    public IndexationType getIndexationType() {
        return indexationType;
    }

    public void setIndexationType(IndexationType indexationType) {
        this.indexationType = indexationType;
    }

    @Override
    public BigDecimal getIndexationAmount() {
        return indexationAmount;
    }

    public void setIndexationAmount(BigDecimal indexationAmount) {
        this.indexationAmount = indexationAmount;
    }

    @Override
    public WithdrawalType getWithdrawalType() {
        return withdrawalType;
    }

    public void setWithdrawalType(WithdrawalType withdrawalType) {
        this.withdrawalType = withdrawalType;
    }

    @Override
    public PensionPaymentType getPensionPaymentType() {
        return pensionPaymentType;
    }

    public void setPensionPaymentType(PensionPaymentType pensionPaymentType) {
        this.pensionPaymentType = pensionPaymentType;
    }

    @Override
    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
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

    @Override
    public PaymentActionType getPaymentAction() {
        return paymentAction;
    }

    public void setPaymentAction(PaymentActionType paymentAction) {
        this.paymentAction = paymentAction;
    }

    public String getTransactionSeqNo() {
        return transactionSeqNo;
    }

    public void setTransactionSeqNo(String transactionSeqNo) {
        this.transactionSeqNo = transactionSeqNo;
    }

    @Override
    public String getBusinessChannel() { return businessChannel; }

    public void setBusinessChannel(String businessChannel) { this.businessChannel = businessChannel; }

    @Override
    public String getClientIp() { return clientIp; }

    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    /**
     * Return the modification sequence - required for BP pay request
     * @return
     */
    @Override
    public String getModificationSeq() {
        return modificationSeq;
    }

    public void setModificationSeq(String modificationSeq) {
        this.modificationSeq = modificationSeq;
    }
}
