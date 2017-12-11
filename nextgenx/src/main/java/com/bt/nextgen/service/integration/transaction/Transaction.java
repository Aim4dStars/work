package com.bt.nextgen.service.integration.transaction;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.avaloq.transaction.TransactionFrequency;
import com.bt.nextgen.service.avaloq.transaction.TransactionWorkflowStatus;
import com.bt.nextgen.service.integration.movemoney.IndexationType;
import com.bt.nextgen.service.integration.movemoney.PensionPaymentType;
import com.bt.nextgen.service.integration.order.OrderType;

public interface Transaction extends BaseTransaction {
    public void setKey(String key);

    public void setAccountId(String accountId);

    public String getClientId();

    public void setClientId(String clientId);

    public void setDocId(String docId);

    public TransactionType getMetaType();

    public void setMetaType(TransactionType metaType);

    public void setStatus(String status);

    public void setOrderType(String orderType);

    void setOrderTypeCode(OrderType orderType);

    public void setTransactionId(String transactionId);

    public TransactionFrequency getFrequency();

    public void setFrequency(TransactionFrequency frequency);

    public void setDescription(String description);

    public void setNetAmount(BigDecimal netAmount);

    public BigDecimal getBalance();

    public void setBalance(BigDecimal balance);

    public void setEffectiveDate(DateTime effectiveDate);

    @Override
    public void setValDate(DateTime valDate);

    public String getPayer();

    public void setPayer(String payer);

    public String getPayee();

    public void setPayee(String payee);

    public TransactionWorkflowStatus getWorkFlowStatus();

    public void setWorkFlowStatus(TransactionWorkflowStatus workFlowStatus);

    /* Start :: Added for CASH Refactor */

    /**
     * Fetch the Payer BSB
     * 
     * @return
     */
    public String getPayerBsb();

    /**
     * Set the Payer BSB
     * 
     * @param payerBsb
     */
    public void setPayerBsb(String payerBsb);

    /**
     * Fetch the Payee BSB
     * 
     * @return
     */
    public String getPayeeBsb();

    /**
     * Set the Payee BSB
     * 
     * @param payeeBsb
     */
    public void setPayeeBsb(String payeeBsb);

    /**
     * Fetch the Payer Account
     * 
     * @return
     */

    public String getPayerAccount();

    /**
     * Set the Payer Account
     * 
     * @param payerAccount
     */
    public void setPayerAccount(String payerAccount);

    /**
     * Fetch the Payee Account
     * 
     * @return
     */
    public String getPayeeAccount();

    /**
     * Set the Payee Account
     * 
     * @param payeeAccount
     */

    public void setPayeeAccount(String payeeAccount);

    /**
     * Fetch the Next Due Date
     * 
     * @return
     */
    public DateTime getNextDue();

    /**
     * Set the Next Due Date
     * 
     * @param nextDueDate
     */
    public void setNextDue(DateTime nextDue);

    public BigDecimal getAmount();

    public void setAmount(BigDecimal amount);

    /**
     * Fetch the First Payment Date
     * 
     * @return
     */
    public DateTime getFirstDate();

    /**
     * Set the First Payment Date
     * 
     * @param firstPayment
     */
    public void setFirstDate(DateTime firstDate);

    /**
     * Fetch the Transaction Status
     * 
     * @return
     */
    public TransactionWorkflowStatus getTransactionStatus();

    /**
     * Set the Transaction Status
     * 
     * @param transactionStatus
     */
    public void setTransactionStatus(TransactionWorkflowStatus transactionStatus);

    /**
     * Fetch the Payment Id
     * 
     * @return
     */
    public String getPaymentId();

    /**
     * Set the Payment Id
     * 
     * @param paymentId
     */
    public void setPaymentId(String paymentId);

    /* End :: Added for CASH Refactor */

    public String getPayeeBillerCode();

    public void setPayeeBillerCode(String payeeBillerCode);

    public String getPayeeCustrRef();

    public void setPayeeCustrRef(String payeeCustrRef);

    public DateTime getEndDate();

    public void setEndDate(DateTime endDate);

    public String getRepeatInstr();

    public void setRepeatInstr(String repeatInstr);

    public int getMaxPeriodCnt();

    public void setMaxPeriodCnt(int maxPeriodCnt);

    public String getStordPos();

    public void setStordPos(String stordPos);

    public DateTime getRecentTrxDate();

    public void setRecentTrxDate(DateTime recentTrxDate);

    IndexationType getPensionIndexationType();

    void setPensionIndexationType(IndexationType indexationType);

    BigDecimal getPensionIndexationAmount();

    void setPensionIndexationAmount(BigDecimal amount);

    BigDecimal getPensionIndexationPercent();

    void setPensionIndexationPercent(BigDecimal percent);

    BigDecimal getPensionMaximumAnnualPot();

    void setPensionMaximumAnnualPot(BigDecimal pensionYearlyPotMax);

    PensionPaymentType getPensionPaymentType();

    String getContributionType();

    boolean getHasDrawdownInprogress();
}