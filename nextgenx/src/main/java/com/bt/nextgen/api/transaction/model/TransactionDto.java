package com.bt.nextgen.api.transaction.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TransactionDto extends BaseDto implements KeyedDto<TransactionKey> {
    private TransactionKey transactionKey;
    private InvestmentKey investmentKey;
    private String description;
    private DateTime effectiveDate;
    private BigDecimal netAmount;
    private BigDecimal balance;
    private TransactionTypeEnum transactionType;
    private String workFlowStatus;
    private String transactionStatus;
    private String orderType;
    private String orderTypeCode;
    private DateTime valDate;
    private String frequency;
    private String payer;
    private String payee;
    private String metaType;

    /* Start :: Added fields for CASH Refactor Scheduled Transactions Implementation */

    private DateTime firstPayment;
    private DateTime lastPayment;
    private BigDecimal recieptNumber;
    private String payeeBsb;
    private String payeeAccount;
    private String payerBsb;
    private String payerAccount;
    private int tranStatusHolder;
    private DateTime nextDueDate;
    private DateTime recentTrxDate;

    private String paymentId;
    private boolean isSuccessful;

    private String firstPaymentUpdated;
    private String lastPaymentUpdated;

    private String effectiveDateUpdated;
    private String metaTypeDisplay;

    private BigDecimal creditAmount;
    private BigDecimal debitAmount;

    private String message;
    private String errorMessage;

    private String custRefno;
    private String billerCode;
    private String repeatInstr;
    private int maxCount;
    private String repeatEndDate;
    private String stordPosId;
    private String docDescription;

    private String category;

    /* End :: Added fields for CASH Refactor Scheduled Transactions Implementation */

    /*
     * Added following variable to store next payment date in a scheduled transaction, as existing variable nextDueDate is getting
     * overwritten with Effective Date
     */
    private DateTime nextPaymentDate;

    private String indexationType;
    private BigDecimal indexationAmount;
    private BigDecimal maximumAnnualPot;
    private String pensionPaymentType;
    private String contributionType;
    private boolean hasDrawdownInprogress;
    private String transSeqNo;

    public TransactionDto() {
        // Empty constructor
    }

    public TransactionDto(TransactionKey transactionKey, InvestmentKey investmentKey, String description, DateTime effectiveDate,
            BigDecimal netAmount, BigDecimal balance, TransactionTypeEnum transactionType) {
        super();
        this.transactionKey = transactionKey;
        this.investmentKey = investmentKey;
        this.description = description;
        this.effectiveDate = effectiveDate;
        this.netAmount = netAmount;
        this.balance = balance;
        this.transactionType = transactionType;
    }

    // Stores the description of one-off-advice - us2489
    public String getDocDescription() {
        return docDescription;
    }

    public void setDocDescription(String docDescription) {
        this.docDescription = docDescription;
    }

    public TransactionKey getTransactionKey() {
        return transactionKey;
    }

    public InvestmentKey getInvestmentKey() {
        return investmentKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public TransactionTypeEnum getTransactionType() {
        return transactionType;
    }

    public String getWorkFlowStatus() {
        return workFlowStatus;
    }

    public void setWorkFlowStatus(String workFlowStatus) {
        this.workFlowStatus = workFlowStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public DateTime getValDate() {
        return valDate;
    }

    public void setValDate(DateTime valDate) {
        this.valDate = valDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    /* Start :: Added fields for CASH Refactor Scheduled Transactions Implementation */
    /**
     * Fetch the First Payment Date
     * 
     * @return
     */
    public DateTime getFirstPayment() {
        return firstPayment;
    }

    /**
     * Set the First Payment Date
     * 
     * @param firstPayment
     */
    public void setFirstPayment(DateTime firstPayment) {
        this.firstPayment = firstPayment;
    }

    /**
     * Fetch the Last Payment Date
     * 
     * @return
     */
    public DateTime getLastPayment() {
        return lastPayment;
    }

    /**
     * Set the last Payment Date
     * 
     * @param lastPayment
     */

    public void setLastPayment(DateTime lastPayment) {
        this.lastPayment = lastPayment;
    }

    /**
     * fetch the Reciept Number
     * 
     * @return
     */

    public BigDecimal getRecieptNumber() {
        return recieptNumber;
    }

    /**
     * Set the Reciept Number
     * 
     * @param recieptNumber
     */
    public void setRecieptNumber(BigDecimal recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    /**
     * Fetch the Payee BSB
     * 
     * @return
     */

    public String getPayeeBsb() {
        return payeeBsb;
    }

    /**
     * Set the Payee BSB
     * 
     * @param payeeBsb
     */
    public void setPayeeBsb(String payeeBsb) {
        this.payeeBsb = payeeBsb;
    }

    /**
     * Fetch the Payee Account
     * 
     * @return
     */
    public String getPayeeAccount() {
        return payeeAccount;
    }

    /**
     * Set the Payee Account
     * 
     * @param payeeAccount
     */
    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    /**
     * Fetch the Payer BSB
     * 
     * @return
     */
    public String getPayerBsb() {
        return payerBsb;
    }

    /**
     * Set the Payer BSB
     * 
     * @param payerBsb
     */

    public void setPayerBsb(String payerBsb) {
        this.payerBsb = payerBsb;
    }

    /**
     * Fetch the Payer Account
     * 
     * @return
     */

    public String getPayerAccount() {
        return payerAccount;
    }

    /**
     * Set the Payer Account
     * 
     * @param payerAccount
     */
    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    /**
     * Fetch the Transaction Status Holder
     * 
     * @return
     */
    public int getTranStatusHolder() {
        return tranStatusHolder;
    }

    /**
     * Set the Transaction Status Holder
     * 
     * @param tranStatusHolder
     */
    public void setTranStatusHolder(int tranStatusHolder) {
        this.tranStatusHolder = tranStatusHolder;
    }

    /**
     * Fetch the Next Due Date
     * 
     * @return
     */
    public DateTime getNextDueDate() {
        return nextDueDate;
    }

    /**
     * Set the Next Due Date
     * 
     * @param nextDueDate
     */
    public void setNextDueDate(DateTime nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    /**
     * Fetch the Payment Id
     * 
     * @return
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Set the Payment Id
     * 
     * @param paymentId
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * Fetch the success field
     * 
     * @return
     */

    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * Set the success value
     * 
     * @param isSuccessful
     */
    public void setSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    /* End :: Added fields for CASH Refactor Scheduled Transactions Implementation */

    public String getFirstPaymentUpdated() {
        return firstPaymentUpdated;
    }

    public void setFirstPaymentUpdated(String firstPaymentUpdated) {
        this.firstPaymentUpdated = firstPaymentUpdated;
    }

    public String getLastPaymentUpdated() {
        return lastPaymentUpdated;
    }

    public void setLastPaymentUpdated(String lastPaymentUpdated) {
        this.lastPaymentUpdated = lastPaymentUpdated;
    }

    public String getEffectiveDateUpdated() {
        return effectiveDateUpdated;
    }

    public void setEffectiveDateUpdated(String effectiveDateUpdated) {
        this.effectiveDateUpdated = effectiveDateUpdated;
    }

    public String getMetaTypeDisplay() {
        return metaTypeDisplay;
    }

    public void setMetaTypeDisplay(String metaTypeDisplay) {
        this.metaTypeDisplay = metaTypeDisplay;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getCustRefno() {
        return custRefno;
    }

    public void setCustRefno(String custRefno) {
        this.custRefno = custRefno;
    }

    public String getBillerCode() {
        return billerCode;
    }

    public void setBillerCode(String billerCode) {
        this.billerCode = billerCode;
    }

    public String getRepeatInstr() {
        return repeatInstr;
    }

    public void setRepeatInstr(String repeatInstr) {
        this.repeatInstr = repeatInstr;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public String getStordPosId() {
        return stordPosId;
    }

    public void setStordPosId(String stordPosId) {
        this.stordPosId = stordPosId;
    }

    public DateTime getNextPaymentDate() {
        return nextPaymentDate;
    }

    public void setNextPaymentDate(DateTime nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
    }

    public DateTime getRecentTrxDate() {
        return recentTrxDate;
    }

    public void setRecentTrxDate(DateTime recentTrxDate) {
        this.recentTrxDate = recentTrxDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOrderTypeCode() {
        return orderTypeCode;
    }

    public void setOrderTypeCode(String orderTypeCode) {
        this.orderTypeCode = orderTypeCode;
    }

    public String getIndexationType() {
        return indexationType;
    }

    public void setIndexationType(String indexationType) {
        this.indexationType = indexationType;
    }

    public BigDecimal getIndexationAmount() {
        return indexationAmount;
    }

    public void setIndexationAmount(BigDecimal indexationAmount) {
        this.indexationAmount = indexationAmount;
    }

    public String getPensionPaymentType() {
        return pensionPaymentType;
    }

    public void setPensionPaymentType(String pensionPaymentType) {
        this.pensionPaymentType = pensionPaymentType;
    }

    public String getContributionType() {
        return contributionType;
    }

    public void setContributionType(String contributionType) {
        this.contributionType = contributionType;
    }

    public BigDecimal getMaximumAnnualPot() {
        return maximumAnnualPot;
    }

    public void setMaximumAnnualPot(BigDecimal maximumAnnualPot) {
        this.maximumAnnualPot = maximumAnnualPot;
    }

    public boolean getHasDrawdownInprogress() {
        return hasDrawdownInprogress;
    }

    public void setHasDrawdownInprogress(boolean hasDrawdownInprogress) {
        this.hasDrawdownInprogress = hasDrawdownInprogress;
    }

    @Override
    public TransactionKey getKey() {
        return transactionKey;
    }

    public String getTransSeqNo() {
        return transSeqNo;
    }

    public void setTransSeqNo(String transSeqNo) {
        this.transSeqNo = transSeqNo;
    }

}
