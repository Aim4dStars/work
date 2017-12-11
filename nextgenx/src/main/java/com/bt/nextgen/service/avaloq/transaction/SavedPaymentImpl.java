package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by L067218 on 27/01/2017.
 */
@ServiceBean(xpath = "doc")
public class SavedPaymentImpl implements SavedPayment{

    private static final String XPATH_CONSTANT = "doc_head_list/doc_head/";


    /**
     * {@link com.btfin.panorama.core.conversion.CodeCategory}
     */
    private static final String CODE_CATEGORY_TRANSACTION_FREQUENCY = "CODES_PAYMENT_FREQUENCIES";

    @ServiceElement(xpath = XPATH_CONSTANT + "trx/val")
    private String transactionId;

    @ServiceElement(xpath = XPATH_CONSTANT + "trx_descn/val")
    private String description;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_idx_mtd_id/val", converter = PensionPaymentOrIndexationTypeConverter.class)
    private PensionPaymentType pensionPaymentType = PensionPaymentType.SPECIFIC_AMOUNT;


    @ServiceElement(xpath = XPATH_CONSTANT + "ui_trx_status_id/val", converter = OrderStatusConverter.class)
    private String transactionStatus;

    @ServiceElement(xpath = XPATH_CONSTANT + "order_type_id/val", converter = PensionOrderTypeConverter.class)
    private String orderType;

    @ServiceElement(xpath = XPATH_CONSTANT + "amount/val", converter = BigDecimalConverter.class)
    private BigDecimal amount;

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

    @ServiceElement(xpath = XPATH_CONSTANT + "trans_seq_nr/val")
    private String transSeqNo;


    @ServiceElement(xpath = XPATH_CONSTANT + "pens_idx_mtd_id/val", converter = PensionPaymentOrIndexationTypeConverter.class)
    private IndexationType pensionIndexationType;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_fixed_amt/val", converter = BigDecimalConverter.class)
    private BigDecimal pensionIndexationAmount;

    @ServiceElement(xpath = XPATH_CONSTANT + "pens_fixed_pct/val", converter = BigDecimalConverter.class)
    private BigDecimal pensionIndexationPercent;

    @ServiceElement(xpath = XPATH_CONSTANT + "stord_pos/val")
    private String stordPos;


    @ServiceElement(xpath = XPATH_CONSTANT + "freq_id/val", staticCodeCategory = CODE_CATEGORY_TRANSACTION_FREQUENCY)
    private TransactionFrequency frequency;


    @ServiceElement(xpath = XPATH_CONSTANT + "first_date/val", converter = DateTimeTypeConverter.class)
    private DateTime firstDate;

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getTransactionStatus() {
        return transactionStatus;
    }

    @Override
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
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
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String getPayer() {
        return payer;
    }

    @Override
    public String getPayee() {
        return payee;
    }

    @Override
    public String getPayerBsb() {
        return payerBsb;
    }

    @Override
    public void setPayerBsb(String payerBsb) {
        this.payerBsb = payerBsb;
    }

    @Override
    public String getPayeeBsb() {
        return payeeBsb;
    }

    @Override
    public void setPayeeBsb(String payeeBsb) {
        this.payeeBsb = payeeBsb;
    }

    @Override
    public String getPayerAccount() {
        return payerAccount;
    }

    @Override
    public String getPayeeAccount() {
        return payeeAccount;
    }

    @Override
    public String getTransSeqNo() {
        return transSeqNo;
    }

    @Override
    public void setTransSeqNo(String transSeqNo) {
        this.transSeqNo = transSeqNo;
    }

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
    public String getStordPos() {
        return stordPos;
    }

    @Override
    public void setStordPos(String stordPos) {
        this.stordPos = stordPos;
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
    public DateTime getFirstDate() {
        return firstDate;
    }

    @Override
    public PensionPaymentType getPensionPaymentType() {
        return pensionPaymentType;
    }

    @Override
    public void setPensionPaymentType(PensionPaymentType pensionPaymentType) {
        this.pensionPaymentType = pensionPaymentType;
    }

    @Override
    public BigDecimal getPensionIndexationPercent() {
        return pensionIndexationPercent;
    }
}
