package com.bt.nextgen.api.account.v1.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceError;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @deprecated Use V2
 */
@Deprecated
public class DepositDto extends BaseDto implements KeyedDto<AccountKey> {

    /** The key. */
    @JsonView(JsonViews.Write.class)
    private AccountKey key;

    /** The currency. */
    @JsonView(JsonViews.Write.class)
    private String currency;

    /** The limit. */
    @JsonView(JsonViews.Write.class)
    private BigDecimal limit;

    /** The payment id. */
    @JsonView(JsonViews.Write.class)
    private String paymentId;

    /** The is recurring. */
    @JsonView(JsonViews.Write.class)
    private boolean isRecurring;

    /** The repeat end date. */
    @JsonView(JsonViews.Write.class)
    private String repeatEndDate;

    /** The amount. */
    @JsonView(JsonViews.Write.class)
    private BigDecimal amount;

    /** The description. */
    @JsonView(JsonViews.Write.class)
    private String description;

    /** The transaction date. */
    @JsonView(JsonViews.Write.class)
    private String transactionDate;

    // private PaymentRepeatsEnd endRepeat;

    /** The end repeat. */
    @JsonView(JsonViews.Write.class)
    private String endRepeat;

    /** The end repeat number. */
    @JsonView(JsonViews.Write.class)
    private String endRepeatNumber;

    /** The error message. */
    @JsonView(JsonViews.Write.class)
    private String errorMessage;

    /** The to paytee dto. */
    @JsonView(JsonViews.Write.class)
    private PayeeDto toPayteeDto;

    /** The from pay dto. */
    @JsonView(JsonViews.Write.class)
    private PayeeDto fromPayDto;

    /** The frequency. */
    @JsonView(JsonViews.Write.class)
    private String frequency;

    /** The is primary. */
    @JsonView(JsonViews.Write.class)
    private boolean primary;

    /** The reciept number. */
    private String recieptNumber;

    /** The daily limit dto. */
    private DailyLimitDto dailyLimitDto;

    /** The errors. */
    @JsonView(JsonViews.Write.class)
    private List<ServiceError> errors;

    /** The updated bsb to paytee dto. */
    private String updatedBsbToPayteeDto;

    /** The updated bsb from pay dto. */
    private String updatedBsbFromPayDto;

    /** The reciept id. */
    private String recieptId;

    public DepositDto() {
        // Default Constructor - being referred in DepositUtil.java
    }

    public DepositDto(DepositDto depositDtoKeyedObj) {
        super();
        this.key = depositDtoKeyedObj.key;
        this.currency = depositDtoKeyedObj.currency;
        this.limit = depositDtoKeyedObj.limit;
        this.paymentId = depositDtoKeyedObj.paymentId;
        this.isRecurring = depositDtoKeyedObj.isRecurring;
        this.repeatEndDate = depositDtoKeyedObj.repeatEndDate;
        this.amount = depositDtoKeyedObj.amount;
        this.description = depositDtoKeyedObj.description;
        this.transactionDate = depositDtoKeyedObj.transactionDate;
        this.endRepeat = depositDtoKeyedObj.endRepeat;
        this.endRepeatNumber = depositDtoKeyedObj.endRepeatNumber;
        this.errorMessage = depositDtoKeyedObj.errorMessage;
        this.toPayteeDto = depositDtoKeyedObj.toPayteeDto;
        this.fromPayDto = depositDtoKeyedObj.fromPayDto;
        this.frequency = depositDtoKeyedObj.frequency;
        this.primary = depositDtoKeyedObj.primary;
        this.recieptNumber = depositDtoKeyedObj.recieptNumber;
        this.dailyLimitDto = depositDtoKeyedObj.dailyLimitDto;
        this.errors = depositDtoKeyedObj.errors;
        this.updatedBsbToPayteeDto = depositDtoKeyedObj.updatedBsbToPayteeDto;
        this.updatedBsbFromPayDto = depositDtoKeyedObj.updatedBsbFromPayDto;
        this.recieptId = depositDtoKeyedObj.recieptId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.bt.nextgen.core.api.model.KeyedDto#getKey()
     */
    @Override
    public AccountKey getKey() {
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
     * Gets the currency.
     * 
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the currency.
     * 
     * @param currency
     *            the new currency
     */
    public void setCurrency(String currency) {
        this.currency = currency;
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
     * Gets the payment id.
     * 
     * @return the payment id
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the payment id.
     * 
     * @param paymentId
     *            the new payment id
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * Gets the checks if is recurring.
     * 
     * @return the checks if is recurring
     */
    public boolean getIsRecurring() {
        return isRecurring;
    }

    /**
     * Sets the checks if is recurring.
     * 
     * @param isRecurring
     *            the new checks if is recurring
     */
    public void setIsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    /**
     * Gets the repeat end date.
     * 
     * @return the repeat end date
     */
    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    /**
     * Sets the repeat end date.
     * 
     * @param repeatEndDate
     *            the new repeat end date
     */
    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    /**
     * Gets the amount.
     * 
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the amount.
     * 
     * @param amount
     *            the new amount
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description
     *            the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the transaction date.
     * 
     * @return the transaction date
     */
    public String getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transaction date.
     * 
     * @param transactionDate
     *            the new transaction date
     */
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the end repeat.
     * 
     * @return the end repeat
     */
    public String getEndRepeat() {
        return endRepeat;
    }

    /**
     * Sets the end repeat.
     * 
     * @param endRepeat
     *            the new end repeat
     */
    public void setEndRepeat(String endRepeat) {
        this.endRepeat = endRepeat;
    }

    /**
     * Gets the end repeat number.
     * 
     * @return the end repeat number
     */
    public String getEndRepeatNumber() {
        return endRepeatNumber;
    }

    /**
     * Sets the end repeat number.
     * 
     * @param endRepeatNumber
     *            the new end repeat number
     */
    public void setEndRepeatNumber(String endRepeatNumber) {
        this.endRepeatNumber = endRepeatNumber;
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
     * Gets the to paytee dto.
     * 
     * @return the to paytee dto
     */
    public PayeeDto getToPayteeDto() {
        return toPayteeDto;
    }

    /**
     * Sets the to paytee dto.
     * 
     * @param toPayteeDto
     *            the new to paytee dto
     */
    public void setToPayteeDto(PayeeDto toPayteeDto) {
        this.toPayteeDto = toPayteeDto;
    }

    /**
     * Gets the from pay dto.
     * 
     * @return the from pay dto
     */
    public PayeeDto getFromPayDto() {
        return fromPayDto;
    }

    /**
     * Sets the from pay dto.
     * 
     * @param fromPayDto
     *            the new from pay dto
     */
    public void setFromPayDto(PayeeDto fromPayDto) {
        this.fromPayDto = fromPayDto;
    }

    /**
     * Gets the frequency.
     * 
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the frequency.
     * 
     * @param frequency
     *            the new frequency
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Checks if is primary.
     * 
     * @return true, if is primary
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Sets the primary.
     * 
     * @param isPrimary
     *            the new primary
     */
    public void setPrimary(boolean isPrimary) {
        this.primary = isPrimary;
    }

    /**
     * Gets the reciept number.
     * 
     * @return the reciept number
     */
    public String getRecieptNumber() {
        return recieptNumber;
    }

    /**
     * Sets the reciept number.
     * 
     * @param recieptNumber
     *            the new reciept number
     */
    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    /**
     * Gets the daily limit dto.
     * 
     * @return the daily limit dto
     */
    public DailyLimitDto getDailyLimitDto() {
        return dailyLimitDto;
    }

    /**
     * Sets the daily limit dto.
     * 
     * @param dailyLimitDto
     *            the new daily limit dto
     */
    public void setDailyLimitDto(DailyLimitDto dailyLimitDto) {
        this.dailyLimitDto = dailyLimitDto;
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

    /**
     * Gets the updated bsb to paytee dto.
     * 
     * @return the updated bsb to paytee dto
     */
    public String getUpdatedBsbToPayteeDto() {
        return updatedBsbToPayteeDto;
    }

    /**
     * Sets the updated bsb to paytee dto.
     * 
     * @param updatedBsbToPayteeDto
     *            the new updated bsb to paytee dto
     */
    public void setUpdatedBsbToPayteeDto(String updatedBsbToPayteeDto) {
        this.updatedBsbToPayteeDto = updatedBsbToPayteeDto;
    }

    /**
     * Gets the updated bsb from pay dto.
     * 
     * @return the updated bsb from pay dto
     */
    public String getUpdatedBsbFromPayDto() {
        return updatedBsbFromPayDto;
    }

    /**
     * Sets the updated bsb from pay dto.
     * 
     * @param updatedBsbFromPayDto
     *            the new updated bsb from pay dto
     */
    public void setUpdatedBsbFromPayDto(String updatedBsbFromPayDto) {
        this.updatedBsbFromPayDto = updatedBsbFromPayDto;
    }

    /**
     * Gets the reciept id.
     * 
     * @return the reciept id
     */
    public String getRecieptId() {
        return recieptId;
    }

    /**
     * Sets the reciept id.
     * 
     * @param recieptId
     *            the new reciept id
     */
    public void setRecieptId(String recieptId) {
        this.recieptId = recieptId;
    }

}
