package com.bt.nextgen.service.avaloq.deposit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.OverridableServiceErrorIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.btfin.panorama.service.integration.RecurringFrequency;
import com.bt.nextgen.service.integration.RecurringTransaction;
import com.bt.nextgen.service.integration.deposit.RecurringDepositDetails;

/**
 * @deprecated Use package com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl
 */
@Deprecated
public class RecurringDepositDetailsImpl implements RecurringDepositDetails {
    private MoneyAccountIdentifier moneyAccountIdentifier;
    private PayAnyoneAccountDetails payAnyoneAccountDetails;
    private BigDecimal depositAmount;
    private CurrencyType currencyType;
    private String description;
    private Date transactionDate;
    private RecurringTransaction recurringTransaction;
    private String recieptNumber;
    private Date depositDate;
    private List<OverridableServiceErrorIdentifier> overridableErrorList;
    private RecurringFrequency recurringFrequency;
    private Date startDate;
    private Date endDate;
    private Integer maxCount;
    private String positionId;
    private DateTime nextTransactionDate;

    /**
     * @return the moneyAccountIdentifier
     */
    public MoneyAccountIdentifier getMoneyAccountIdentifier() {
        return moneyAccountIdentifier;
    }

    /**
     * @param moneyAccountIdentifier
     *            the moneyAccountIdentifier to set
     */
    public void setMoneyAccountIdentifier(MoneyAccountIdentifier moneyAccountIdentifier) {
        this.moneyAccountIdentifier = moneyAccountIdentifier;
    }

    /**
     * @return the payAnyoneAccountDetails
     */
    public PayAnyoneAccountDetails getPayAnyoneAccountDetails() {
        return payAnyoneAccountDetails;
    }

    /**
     * @param payAnyoneAccountDetails
     *            the payAnyoneAccountDetails to set
     */
    public void setPayAnyoneAccountDetails(PayAnyoneAccountDetails payAnyoneAccountDetails) {
        this.payAnyoneAccountDetails = payAnyoneAccountDetails;
    }

    /**
     * @return the depositAmount
     */
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    /**
     * @param depositAmount
     *            the depositAmount to set
     */
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    /**
     * @return the currencyType
     */
    public CurrencyType getCurrencyType() {
        return currencyType;
    }

    /**
     * @param currencyType
     *            the currencyType to set
     */
    public void setCurrencyType(CurrencyType currencyType) {
        this.currencyType = currencyType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the transactionDate
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * @param transactionDate
     *            the transactionDate to set
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * @return the recieptNumber
     */
    public String getRecieptNumber() {
        return recieptNumber;
    }

    /**
     * @param recieptNumber
     *            the recieptNumber to set
     */
    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    /**
     * @return the depositDate
     */
    public Date getDepositDate() {
        return depositDate;
    }

    /**
     * @param depositDate
     *            the depositDate to set
     */
    public void setDepositDate(Date depositDate) {
        this.depositDate = depositDate;
    }

    /**
     * @return the overridableErrorList
     */
    public List<OverridableServiceErrorIdentifier> getOverridableErrorList() {
        if (overridableErrorList == null)
            return new ArrayList<OverridableServiceErrorIdentifier>();
        return overridableErrorList;
    }

    /**
     * @param overridableErrorList
     *            the overridableErrorList to set
     */
    public void setOverridableErrorList(List<OverridableServiceErrorIdentifier> overridableErrorList) {
        this.overridableErrorList = overridableErrorList;
    }

    /**
     * @return the recurringFrequency
     */
    public RecurringFrequency getRecurringFrequency() {
        return recurringFrequency;
    }

    /**
     * @param recurringFrequency
     *            the recurringFrequency to set
     */
    public void setRecurringFrequency(RecurringFrequency recurringFrequency) {
        this.recurringFrequency = recurringFrequency;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the maxCount
     */
    public Integer getMaxCount() {
        return maxCount;
    }

    /**
     * @param maxCount
     *            the maxCount to set
     */
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    @Override
    public DateTime getNextTransactionDate() {
        return nextTransactionDate;
    }

    @Override
    public void setNextTransactionDate(DateTime txnDate) {
        this.nextTransactionDate = txnDate;
    }

}
