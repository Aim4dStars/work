package com.bt.nextgen.api.movemoney.v2.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;
import java.util.List;

public class DepositDto extends BaseDto implements KeyedDto<AccountKey> {

    @JsonView(JsonViews.Write.class)
    private AccountKey key;

    @JsonView(JsonViews.Write.class)
    private boolean isRecurring;

    @JsonView(JsonViews.Write.class)
    private String repeatEndDate;

    @JsonView(JsonViews.Write.class)
    private BigDecimal amount;

    @JsonView(JsonViews.Write.class)
    private String description;

    @JsonView(JsonViews.Write.class)
    private String transactionDate;

    @JsonView(JsonViews.Write.class)
    private PayeeDto toPayeeDto;

    @JsonView(JsonViews.Write.class)
    private PayeeDto fromPayDto;

    @JsonView(JsonViews.Write.class)
    private String frequency;

    private String currency;
    private BigDecimal limit;
    private String paymentId;

    @JsonView(JsonViews.Write.class)
    private String endRepeat;

    @JsonView(JsonViews.Write.class)
    private String endRepeatNumber;

    private String errorMessage;
    private boolean primary;
    private String receiptNumber;
    private DailyLimitDto dailyLimitDto;
    private String updatedBsbToPayeeDto;
    private String updatedBsbFromPayDto;
    private String receiptId;

    @JsonView(JsonViews.Write.class)
    private String depositType;

    private List<DomainApiErrorDto> errors;

    @JsonView(JsonViews.Write.class)
    private List<DomainApiErrorDto> warnings;

    public DepositDto() {
        // Default Constructor. Being referred in DepositUtil.java
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
        this.toPayeeDto = depositDtoKeyedObj.toPayeeDto;
        this.fromPayDto = depositDtoKeyedObj.fromPayDto;
        this.frequency = depositDtoKeyedObj.frequency;
        this.primary = depositDtoKeyedObj.primary;
        this.receiptNumber = depositDtoKeyedObj.receiptNumber;
        this.dailyLimitDto = depositDtoKeyedObj.dailyLimitDto;
        this.errors = depositDtoKeyedObj.errors;
        this.warnings = depositDtoKeyedObj.warnings;
        this.updatedBsbToPayeeDto = depositDtoKeyedObj.updatedBsbToPayeeDto;
        this.updatedBsbFromPayDto = depositDtoKeyedObj.updatedBsbFromPayDto;
        this.receiptId = depositDtoKeyedObj.receiptId;
        this.depositType = depositDtoKeyedObj.depositType;
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
     * Gets the to payee dto.
     * 
     * @return the to payee dto
     */
    public PayeeDto getToPayeeDto() {
        return toPayeeDto;
    }

    /**
     * Sets the to payee dto.
     * 
     * @param toPayeeDto
     *            the new to payee dto
     */
    public void setToPayeeDto(PayeeDto toPayeeDto) {
        this.toPayeeDto = toPayeeDto;
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
     * Gets the receipt number.
     * 
     * @return the receipt number
     */
    public String getReceiptNumber() {
        return receiptNumber;
    }

    /**
     * Sets the receipt number.
     * 
     * @param receiptNumber
     *            the new receipt number
     */
    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
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
     * Gets the updated bsb to payee dto.
     * 
     * @return the updated bsb to payee dto
     */
    public String getUpdatedBsbToPayeeDto() {
        return updatedBsbToPayeeDto;
    }

    /**
     * Sets the updated bsb to payee dto.
     * 
     * @param updatedBsbToPayeeDto
     *            the new updated bsb to payee dto
     */
    public void setUpdatedBsbToPayeeDto(String updatedBsbToPayeeDto) {
        this.updatedBsbToPayeeDto = updatedBsbToPayeeDto;
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
     * Gets the receipt id.
     * 
     * @return the receipt id
     */
    public String getReceiptId() {
        return receiptId;
    }

    /**
     * Sets the receipt id.
     * 
     * @param receiptId
     *            the new receipt id
     */
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public List<DomainApiErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<DomainApiErrorDto> errors) {
        this.errors = errors;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }
}
