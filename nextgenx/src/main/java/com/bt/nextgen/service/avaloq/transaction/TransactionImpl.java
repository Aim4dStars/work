package com.bt.nextgen.service.avaloq.transaction;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.order.OrderType;
import com.bt.nextgen.service.integration.transaction.Transaction;

@ServiceBean(xpath = "pos")
public class TransactionImpl implements Transaction {
    private static final String XPATH_CONSTANT = "pos_head_list/pos_head/";

    /**
     * {@link CodeCategory}
     */
    private static final String CODE_CATEGORY_TRANSACTION_FREQUENCY = "CODES_PAYMENT_FREQUENCIES";

    /**
     * {@link CodeCategory}
     */
    private static final String CODE_CATEGORY_META_TYPE = "META_TYPE";

    /**
     * {@link CodeCategory}
     */
    private static final String ORDER_TYPE = "ORDER_TYPE";

    // Not called from anywhere
    private String key;

    // Not called from anywhere
    private String accountId;

    // Not called from anywhere
    private String clientId;

    // Not called from anywhere
    private String docId;

    // Not called from anywhere
    private String status;

    // never set in the service
    private BigDecimal balance;

    @ServiceElement(xpath = XPATH_CONSTANT + "trx/val")
    private String transactionId;

    @ServiceElement(xpath = XPATH_CONSTANT + "trx_descn/val")
    private String description;

    @ServiceElement(xpath = XPATH_CONSTANT + "payer/val")
    private String payer;

    @ServiceElement(xpath = XPATH_CONSTANT + "payee/val")
    private String payee;

    /* Start::CASH Refactor :: new fields added */
    @ServiceElement(xpath = XPATH_CONSTANT + "payer_bsb/val")
    private String payerBsb;

    @ServiceElement(xpath = XPATH_CONSTANT + "payee_bsb/val")
    private String payeeBsb;

    @ServiceElement(xpath = XPATH_CONSTANT + "payer_acct_no/val")
    private String payerAccount;

    @ServiceElement(xpath = XPATH_CONSTANT + "payee_acct_no/val")
    private String payeeAccount;

    @ServiceElement(xpath = "trx_list/trx/trx_head_list/trx_head/trx/val")
    private String paymentId;

    @ServiceElement(xpath = XPATH_CONSTANT + "payee_biller_code/val")
    private String payeeBillerCode;

    @ServiceElement(xpath = XPATH_CONSTANT + "payee_custr_ref/val")
    private String payeeCustrRef;

    @ServiceElement(xpath = XPATH_CONSTANT + "meta_typ_id/val", staticCodeCategory = CODE_CATEGORY_META_TYPE)
    private TransactionType metaType;

    @ServiceElement(xpath = XPATH_CONSTANT + "freq_id/val", staticCodeCategory = CODE_CATEGORY_TRANSACTION_FREQUENCY)
    private TransactionFrequency frequency;

    @ServiceElement(xpath = XPATH_CONSTANT + "order_type_id/val", converter = OrderTypeConverter.class)
    private String orderType;

    @ServiceElement(xpath = XPATH_CONSTANT + "order_type_id/val", staticCodeCategory = ORDER_TYPE)
    private OrderType orderTypeCode;

    @ServiceElement(xpath = XPATH_CONSTANT + "amount/val", converter = BigDecimalConverter.class)
    private BigDecimal netAmount;

    // never set in the service. Assuming val date is the effective date
    @ServiceElement(xpath = XPATH_CONSTANT + "val_date/val", converter = DateTimeTypeConverter.class)
    private DateTime effectiveDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "val_date/val", converter = DateTimeTypeConverter.class)
    private DateTime valDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "next_evt/val", converter = DateTimeTypeConverter.class)
    private DateTime nextDue;

    @ServiceElement(xpath = "trx_list/trx/trx_head_list/trx_head/last_trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime recentTrxDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "amount/val", converter = BigDecimalConverter.class)
    private BigDecimal amount;

    @ServiceElement(xpath = XPATH_CONSTANT + "first_date/val", converter = DateTimeTypeConverter.class)
    private DateTime firstDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "end_date/val", converter = DateTimeTypeConverter.class)
    private DateTime endDate;

    @ServiceElement(xpath = XPATH_CONSTANT + "instr_state/val")
    private String repeatInstr;

    @ServiceElement(xpath = "trx_list/trx/trx_head_list/trx_head/trx_status_id/val", converter = TransactionStatusConverter.class)
    private TransactionWorkflowStatus workFlowStatus;

    @ServiceElement(xpath = "trx_list/trx/trx_head_list/trx_head/trx_status_id/val", converter = TransactionStatusConverter.class)
    private TransactionWorkflowStatus transactionStatus;

    @ServiceElement(xpath = XPATH_CONSTANT + "max_period_cnt/val")
    private int maxPeriodCnt;

    @ServiceElement(xpath = XPATH_CONSTANT + "stord_pos/val")
    private String stordPos;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_idx_mtd_id/val", converter = PensionPaymentOrIndexationTypeConverter.class)
    private IndexationType pensionIndexationType;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_idx_mtd_id/val", converter = PensionPaymentOrIndexationTypeConverter.class)
    private PensionPaymentType pensionPaymentType = PensionPaymentType.SPECIFIC_AMOUNT;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_fixed_amt/val", converter = BigDecimalConverter.class)
    private BigDecimal pensionIndexationAmount;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_fixed_pct/val", converter = BigDecimalConverter.class)
    private BigDecimal pensionIndexationPercent;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_yearly_pot_max/val", converter = BigDecimalConverter.class)
    private BigDecimal pensionMaximumAnnualPot;

    @ServiceElement(xpath = XPATH_CONSTANT + "contr_type_name/val")
    private String contributionType;

    @ServiceElement(xpath = XPATH_CONSTANT + "is_bp_in_dd/val")
    private boolean hasDrawdownInprogress;

    @Override
    public String getPayeeBillerCode() {
        return payeeBillerCode;
    }

    @Override
    public void setPayeeBillerCode(String payeeBillerCode) {
        this.payeeBillerCode = payeeBillerCode;
    }

    @Override
    public String getPayeeCustrRef() {
        return payeeCustrRef;
    }

    @Override
    public void setPayeeCustrRef(String payeeCustrRef) {
        this.payeeCustrRef = payeeCustrRef;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String getDocId() {
        return docId;
    }

    @Override
    public void setDocId(String docId) {
        this.docId = docId;
    }

    @Override
    public TransactionType getMetaType() {
        return metaType;
    }

    @Override
    public void setMetaType(TransactionType metaType) {
        this.metaType = metaType;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public TransactionFrequency getFrequency() {
        return frequency;
    }

    @Override
    public void setFrequency(TransactionFrequency frequency) {
        this.frequency = frequency;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public BigDecimal getNetAmount() {
        return netAmount;
    }

    @Override
    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    @Override
    public void setEffectiveDate(DateTime effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public DateTime getValDate() {
        return valDate;
    }

    @Override
    public void setValDate(DateTime valDate) {
        this.valDate = valDate;
    }

    @Override
    public String getPayer() {
        return payer;
    }

    @Override
    public void setPayer(String payer) {
        this.payer = payer;
    }

    @Override
    public String getPayee() {
        return payee;
    }

    @Override
    public void setPayee(String payee) {
        this.payee = payee;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    @Override
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public TransactionWorkflowStatus getWorkFlowStatus() {
        return workFlowStatus;
    }

    @Override
    public void setWorkFlowStatus(TransactionWorkflowStatus workFlowStatus) {
        this.workFlowStatus = workFlowStatus;
    }

    /**
     * Fetch the Payer BSB
     *
     * @return
     */
    @Override
    public String getPayerBsb() {
        return payerBsb;
    }

    /**
     * Set the Payer BSB
     *
     * @param payerBsb
     */
    @Override
    public void setPayerBsb(String payerBsb) {
        this.payerBsb = payerBsb;
    }

    /**
     * Fetch the Payee BSB
     *
     * @return
     */
    @Override
    public String getPayeeBsb() {
        return payeeBsb;
    }

    /**
     * Set the Payee BSB
     *
     * @param payeeBsb
     */
    @Override
    public void setPayeeBsb(String payeeBsb) {
        this.payeeBsb = payeeBsb;
    }

    /**
     * Fetch the Payer Account
     *
     * @return
     */
    @Override
    public String getPayerAccount() {
        return payerAccount;
    }

    /**
     * Set the Payer Account
     *
     * @param payerAccount
     */
    @Override
    public void setPayerAccount(String payerAccount) {
        this.payerAccount = payerAccount;
    }

    /**
     * Fetch the Payee Account
     *
     * @return
     */
    @Override
    public String getPayeeAccount() {
        return payeeAccount;
    }

    /**
     * Set the Payee Account
     *
     * @param payeeAccount
     */
    @Override
    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    /**
     * Fetch the Next Due Date
     *
     * @return
     */
    @Override
    public DateTime getNextDue() {
        return nextDue;
    }

    /**
     * Set the Next Due Date
     *
     * @param nextDueDate
     */
    @Override
    public void setNextDue(DateTime nextDue) {
        this.nextDue = nextDue;
    }

    /**
     * Fetch the Recent Transaction Date
     *
     * @return
     */
    @Override
    public DateTime getRecentTrxDate() {
        return recentTrxDate;
    }

    /**
     * Set the Recent Transaction Date
     *
     * @param recentTrxDate
     */
    @Override
    public void setRecentTrxDate(DateTime recentTrxDate) {
        this.recentTrxDate = recentTrxDate;
    }

    /**
     * Pension indexation type/method
     *
     * @return IndexationType enum
     */
    @Override
    public IndexationType getPensionIndexationType() {
        return pensionIndexationType;
    }

    @Override
    public void setPensionIndexationType(IndexationType pensionIndexationType) {
        this.pensionIndexationType = pensionIndexationType;
    }

    @Override
    public BigDecimal getPensionIndexationAmount() {
        return pensionIndexationAmount;
    }

    @Override
    public void setPensionIndexationAmount(BigDecimal pensionIndexationAmount) {
        this.pensionIndexationAmount = pensionIndexationAmount;
    }

    @Override
    public BigDecimal getPensionIndexationPercent() {
        return pensionIndexationPercent;
    }

    @Override
    public void setPensionIndexationPercent(BigDecimal percent) {
        this.pensionIndexationPercent = percent;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Fetch the First Payment Date
     *
     * @return
     */
    @Override
    public DateTime getFirstDate() {
        return firstDate;
    }

    /**
     * Set the First Payment Date
     *
     * @param firstPayment
     */
    @Override
    public void setFirstDate(DateTime firstDate) {
        this.firstDate = firstDate;
    }

    /**
     * Fetch the Payment Id
     *
     * @return
     */
    @Override
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Set the Payment Id
     *
     * @param paymentId
     */
    @Override
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * Fetch the Transaction Status
     *
     * @return
     */
    @Override
    public TransactionWorkflowStatus getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * Set the Transaction Status
     *
     * @param transactionStatus
     */
    @Override
    public void setTransactionStatus(TransactionWorkflowStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @Override
    public DateTime getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    @Override
    public String getRepeatInstr() {
        return repeatInstr;
    }

    @Override
    public void setRepeatInstr(String repeatInstr) {
        this.repeatInstr = repeatInstr;
    }

    @Override
    public int getMaxPeriodCnt() {
        return maxPeriodCnt;
    }

    @Override
    public void setMaxPeriodCnt(int maxPeriodCnt) {
        this.maxPeriodCnt = maxPeriodCnt;
    }

    /* End::CASH Refactor :: new fields added */

    @Override
    public String getStordPos() {
        return stordPos;
    }

    @Override
    public void setStordPos(String stordPos) {
        this.stordPos = stordPos;
    }

    @Override
    public OrderType getOrderTypeCode() {
        return orderTypeCode;
    }

    @Override
    public void setOrderTypeCode(OrderType orderTypeCode) {
        this.orderTypeCode = orderTypeCode;
    }

    @Override
    public PensionPaymentType getPensionPaymentType() {
        return pensionPaymentType;
    }

    @Override
    public String getContributionType() {
        return contributionType;
    }

    @Override
    public BigDecimal getPensionMaximumAnnualPot() {
        return pensionMaximumAnnualPot;
    }

    @Override
    public void setPensionMaximumAnnualPot(BigDecimal pensionMaximumAnnualPot) {
        this.pensionMaximumAnnualPot = pensionMaximumAnnualPot;
    }

    @Override
    public boolean getHasDrawdownInprogress() {
        return hasDrawdownInprogress;
    }
}
