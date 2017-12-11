/**
 * 
 */
package com.bt.nextgen.service.avaloq.deposit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.OverridableServiceErrorIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;
import com.bt.nextgen.service.integration.deposit.DepositDetails;

/**
 * @deprecated Use package com.bt.nextgen.service.avaloq.movemoney.DepositDetailsImpl
 */
@Deprecated
public class DepositDetailsImpl implements DepositDetails {

    private MoneyAccountIdentifier moneyAccountIdentifier;
    private PayAnyoneAccountDetails payAnyoneAccountDetails;
    private BigDecimal depositAmount;
    private CurrencyType currencyType;
    private String description;
    private Date transactionDate;
    // boolean isRecurring;
    // RecurringTransaction recurringTransaction;
    private String recieptNumber;
    private Date depositDate;
    private List<OverridableServiceErrorIdentifier> overridableErrorList;

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
     * @return the isRecurring
     */
    /*
     * public boolean isRecurring() { return isRecurring; }
     * 
     *//**
       * @param isRecurring
       *            the isRecurring to set
       */
    /*
     * public void setRecurring(boolean isRecurring) { this.isRecurring = isRecurring; }
     * 
     *//**
       * @return the recurringTransaction
       */
    /*
     * public RecurringTransaction getRecurringTransaction() { return recurringTransaction; }
     * 
     *//**
       * @param recurringTransaction
       *            the recurringTransaction to set
       */

    /*
     * public void setRecurringTransaction(RecurringTransaction recurringTransaction) { this.recurringTransaction =
     * recurringTransaction; }
     */
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

}
