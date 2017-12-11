package com.bt.nextgen.api.movemoney.v2.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceError;

public class DailyLimitDto extends BaseDto implements KeyedDto<AccountKey> {

    /** The pay anyone limit. */
    private BigDecimal payAnyoneLimit;

    /** The bpay limit. */
    private BigDecimal bpayLimit;

    /** The linked limit. */
    private BigDecimal linkedLimit;

    /** The remaining pay anyone limit. */
    private BigDecimal remainingPayAnyoneLimit;

    /** The remaining bpay limit. */
    private BigDecimal remainingBpayLimit;

    /** The remaining linked limit. */
    private BigDecimal remainingLinkedLimit;

    /** The bpay limits. */
    private List<BigDecimal> bpayLimits;

    /** The linked limits. */
    private List<BigDecimal> linkedLimits;

    /** The pay anyone limits. */
    private List<BigDecimal> payAnyoneLimits;

    /** The error message. */
    private String errorMessage;

    /** The max limit. */
    private BigDecimal maxLimit;

    /** The analyze. */
    private boolean analyze;

    /** The key. */
    private AccountKey key;

    /** The amount. */
    private String amount;

    /** The payee type. */
    private String payeeType;

    /** The sms code. */
    private String smsCode;

    /** The limit. */
    private BigDecimal limit;

    /** The is limit updated. */
    private String isLimitUpdated;

    /** The available cash. */
    private BigDecimal availableCash;

    /** The transaction id. */
    private String transactionId;

    /** The errors. */
    private List<ServiceError> errors;


    public String getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(String previousAmount) {
        this.previousAmount = previousAmount;
    }

    /** The original Limit. */

    private String  previousAmount;

    /**
     * Gets the pay anyone limit.
     * 
     * @return the pay anyone limit
     */
    public BigDecimal getPayAnyoneLimit() {
        return payAnyoneLimit;
    }

    /**
     * Sets the pay anyone limit.
     * 
     * @param payAnyoneLimit
     *            the new pay anyone limit
     */
    public void setPayAnyoneLimit(BigDecimal payAnyoneLimit) {
        this.payAnyoneLimit = payAnyoneLimit;
    }

    /**
     * Gets the bpay limits.
     * 
     * @return the bpay limits
     */
    public List<BigDecimal> getBpayLimits() {
        return bpayLimits;
    }

    /**
     * Sets the bpay limits.
     * 
     * @param bpayLimits
     *            the new bpay limits
     */
    public void setBpayLimits(List<BigDecimal> bpayLimits) {
        this.bpayLimits = bpayLimits;
    }

    /**
     * Gets the linked limits.
     * 
     * @return the linked limits
     */
    public List<BigDecimal> getLinkedLimits() {
        return linkedLimits;
    }

    /**
     * Sets the linked limits.
     * 
     * @param linkedLimits
     *            the new linked limits
     */
    public void setLinkedLimits(List<BigDecimal> linkedLimits) {
        this.linkedLimits = linkedLimits;
    }

    /**
     * Gets the pay anyone limits.
     * 
     * @return the pay anyone limits
     */
    public List<BigDecimal> getPayAnyoneLimits() {
        return payAnyoneLimits;
    }

    /**
     * Sets the pay anyone limits.
     * 
     * @param payAnyoneLimits
     *            the new pay anyone limits
     */
    public void setPayAnyoneLimits(List<BigDecimal> payAnyoneLimits) {
        this.payAnyoneLimits = payAnyoneLimits;
    }

    /**
     * Gets the error message.
     * 
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     * 
     * @param errorMessage
     *            the new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Checks if is analyze.
     * 
     * @return true, if is analyze
     */
    public boolean isAnalyze() {
        return analyze;
    }

    /**
     * Sets the analyze.
     * 
     * @param analyze
     *            the new analyze
     */
    public void setAnalyze(boolean analyze) {
        this.analyze = analyze;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bt.nextgen.core.api.model.KeyedDto#getKey()
     */
    @Override
    public AccountKey getKey() {
        // TODO Auto-generated method stub
        return key;
    }

    /**
     * Sets the key.
     * 
     * @param key
     *            the new key
     */
    public void setKey(AccountKey key) {
        this.key = key;
    }

    /**
     * Gets the payee type.
     * 
     * @return the payee type
     */
    public String getPayeeType() {
        return payeeType;
    }

    /**
     * Sets the payee type.
     * 
     * @param payeeType
     *            the new payee type
     */
    public void setPayeeType(String payeeType) {
        this.payeeType = payeeType;
    }

    /**
     * Gets the sms code.
     * 
     * @return the sms code
     */
    public String getSmsCode() {
        return smsCode;
    }

    /**
     * Sets the sms code.
     * 
     * @param smsCode
     *            the new sms code
     */
    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    /**
     * Gets the checks if is limit updated.
     * 
     * @return the checks if is limit updated
     */
    public String getIsLimitUpdated() {
        return isLimitUpdated;
    }

    /**
     * Sets the checks if is limit updated.
     * 
     * @param isLimitUpdated
     *            the new checks if is limit updated
     */
    public void setIsLimitUpdated(String isLimitUpdated) {
        this.isLimitUpdated = isLimitUpdated;
    }

    /**
     * Gets the bpay limit.
     * 
     * @return the bpay limit
     */
    public BigDecimal getBpayLimit() {
        return bpayLimit;
    }

    /**
     * Sets the bpay limit.
     * 
     * @param bpayLimit
     *            the new bpay limit
     */
    public void setBpayLimit(BigDecimal bpayLimit) {
        this.bpayLimit = bpayLimit;
    }

    /**
     * Gets the linked limit.
     * 
     * @return the linked limit
     */
    public BigDecimal getLinkedLimit() {
        return linkedLimit;
    }

    /**
     * Sets the linked limit.
     * 
     * @param linkedLimit
     *            the new linked limit
     */
    public void setLinkedLimit(BigDecimal linkedLimit) {
        this.linkedLimit = linkedLimit;
    }

    /**
     * Gets the remaining pay anyone limit.
     * 
     * @return the remaining pay anyone limit
     */
    public BigDecimal getRemainingPayAnyoneLimit() {
        return remainingPayAnyoneLimit;
    }

    /**
     * Sets the remaining pay anyone limit.
     * 
     * @param remainingPayAnyoneLimit
     *            the new remaining pay anyone limit
     */
    public void setRemainingPayAnyoneLimit(BigDecimal remainingPayAnyoneLimit) {
        this.remainingPayAnyoneLimit = remainingPayAnyoneLimit;
    }

    /**
     * Gets the remaining bpay limit.
     * 
     * @return the remaining bpay limit
     */
    public BigDecimal getRemainingBpayLimit() {
        return remainingBpayLimit;
    }

    /**
     * Sets the remaining bpay limit.
     * 
     * @param remainingBpayLimit
     *            the new remaining bpay limit
     */
    public void setRemainingBpayLimit(BigDecimal remainingBpayLimit) {
        this.remainingBpayLimit = remainingBpayLimit;
    }

    /**
     * Gets the remaining linked limit.
     * 
     * @return the remaining linked limit
     */
    public BigDecimal getRemainingLinkedLimit() {
        return remainingLinkedLimit;
    }

    /**
     * Sets the remaining linked limit.
     * 
     * @param remainingLinkedLimit
     *            the new remaining linked limit
     */
    public void setRemainingLinkedLimit(BigDecimal remainingLinkedLimit) {
        this.remainingLinkedLimit = remainingLinkedLimit;
    }

    /**
     * Gets the max limit.
     * 
     * @return the max limit
     */
    public BigDecimal getMaxLimit() {
        return maxLimit;
    }

    /**
     * Sets the max limit.
     * 
     * @param maxLimit
     *            the new max limit
     */
    public void setMaxLimit(BigDecimal maxLimit) {
        this.maxLimit = maxLimit;
    }

    /**
     * Gets the amount.
     * 
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     * 
     * @param amount
     *            the new amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * Gets the limit.
     * 
     * @return the limit
     */
    public BigDecimal getLimit() {
        return limit;
    }

    /**
     * Sets the limit.
     * 
     * @param limit
     *            the new limit
     */
    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    /**
     * Gets the available cash.
     * 
     * @return the available cash
     */
    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    /**
     * Sets the available cash.
     * 
     * @param availableCash
     *            the new available cash
     */
    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }

    /**
     * Gets the transaction id.
     * 
     * @return the transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction id.
     * 
     * @param transactionId
     *            the new transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the errors.
     * 
     * @return the errors
     */
    public List<ServiceError> getErrors() {
        return errors;
    }

    /**
     * Sets the errors.
     * 
     * @param errors
     *            the new errors
     */
    public void setErrors(List<ServiceError> errors) {
        this.errors = errors;
    }
}
