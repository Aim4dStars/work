package com.bt.nextgen.service.integration.deposit;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.bt.nextgen.service.integration.OverridableServiceErrorIdentifier;
import com.bt.nextgen.service.integration.PayAnyoneAccountDetails;

/**
 * @deprecated Use package com.bt.nextgen.service.integration.movemoney.DepositDetails
 */
@Deprecated
public interface DepositDetails {
    /**
     * @return the MoneyAccountIdentifier
     */
    public MoneyAccountIdentifier getMoneyAccountIdentifier();

    /**
     * @param moneyAccountIdentifier
     *            the moneyAccountIdentifier to set
     */
    public void setMoneyAccountIdentifier(MoneyAccountIdentifier moneyAccountIdentifier);

    /**
     * @return the payAnyoneAccountDetails
     */
    public PayAnyoneAccountDetails getPayAnyoneAccountDetails();

    /**
     * @param payAnyoneAccountDetails
     *            the payAnyoneAccountDetails to set
     */
    public void setPayAnyoneAccountDetails(PayAnyoneAccountDetails payAnyoneAccountDetails);

    /**
     * @return the depositAmount
     */
    public BigDecimal getDepositAmount();

    /**
     * @param depositAmount
     *            the depositAmount to set
     */
    public void setDepositAmount(BigDecimal depositAmount);

    /**
     * @return the currencyType
     */
    public CurrencyType getCurrencyType();

    /**
     * @param currencyType
     *            the currencyType to set
     */
    public void setCurrencyType(CurrencyType currencyType);

    /**
     * @return the description
     */
    public String getDescription();

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description);

    /**
     * @return the transactionDate
     */
    public Date getTransactionDate();

    /**
     * @param transactionDate
     *            the transactionDate to set
     */
    public void setTransactionDate(Date transactionDate);

    /**
     * @return the recieptNumber
     */
    public String getRecieptNumber();

    /**
     * @param recieptNumber
     *            the recieptNumber to set
     */
    public void setRecieptNumber(String recieptNumber);

    /**
     * @return the depositDate
     */

    public Date getDepositDate();

    /**
     * @param depositDate
     *            the depositDate to set
     */
    public void setDepositDate(Date depositDate);

    /**
     * @return the overridableErrorList
     */

    public List<OverridableServiceErrorIdentifier> getOverridableErrorList();

    /**
     * @param overridableErrorList
     *            the overridableErrorList to set
     */
    public void setOverridableErrorList(List<OverridableServiceErrorIdentifier> overridableErrorList);

}
