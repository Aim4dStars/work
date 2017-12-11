package com.bt.nextgen.api.account.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.avaloq.accountactivation.LinkedAccountStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated Use V2
 */
@Deprecated
public class PaymentDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;

    private String currency;

    private BigDecimal limit;

    private String paymentId;

    private boolean recurring;

    private String repeatEndDate;

    private BigDecimal amount;

    private String description;

    private String transactionId;

    private String transactionDate;

    // private PaymentRepeatsEnd endRepeat;

    private String endRepeat;

    private String endRepeatNumber;

    private String errorMessage;

    private PayeeDto toPayteeDto;

    private PayeeDto fromPayDto;

    private String frequency;

    private boolean primary;

    private String recieptNumber;

    private boolean validBsb;

    private String updatedBsbToPayteeDto;

    private String updatedBsbFromPayDto;

    private String maccId;

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    private List<DomainApiErrorDto> warnings;

    private String deviceNumber;

    private DailyLimitDto dailyLimitDto;

    private List<ServiceError> errors;

    private String opType;

    private String receiptId;

    private String businessChannel;

    private String clientIp;

    private List<LinkedAccountStatus> associatedAccounts;

    public PaymentDto(AccountKey accountKey) {
        // Default Constructor - Being referred in PaymentDtoServiceImpl and PaymentUtil - TODO CI task to remove this in V2
    }

    public PaymentDto() {
        // Default Constructor - Being referred in PaymentDtoServiceImpl and PaymentUtil - TODO CI task to remove this in V2

    }

    public PaymentDto(PaymentDto paymentDtoKeyedObj) {
        super();
        this.key = paymentDtoKeyedObj.key;
        this.currency = paymentDtoKeyedObj.currency;
        this.limit = paymentDtoKeyedObj.limit;
        this.paymentId = paymentDtoKeyedObj.paymentId;
        this.recurring = paymentDtoKeyedObj.recurring;
        this.repeatEndDate = paymentDtoKeyedObj.repeatEndDate;
        this.amount = paymentDtoKeyedObj.amount;
        this.description = paymentDtoKeyedObj.description;
        this.transactionId = paymentDtoKeyedObj.transactionId;
        this.transactionDate = paymentDtoKeyedObj.transactionDate;
        this.endRepeat = paymentDtoKeyedObj.endRepeat;
        this.endRepeatNumber = paymentDtoKeyedObj.endRepeatNumber;
        this.errorMessage = paymentDtoKeyedObj.errorMessage;
        this.toPayteeDto = paymentDtoKeyedObj.toPayteeDto;
        this.fromPayDto = paymentDtoKeyedObj.fromPayDto;
        this.frequency = paymentDtoKeyedObj.frequency;
        this.primary = paymentDtoKeyedObj.primary;
        this.recieptNumber = paymentDtoKeyedObj.recieptNumber;
        this.validBsb = paymentDtoKeyedObj.validBsb;
        this.updatedBsbToPayteeDto = paymentDtoKeyedObj.updatedBsbToPayteeDto;
        this.updatedBsbFromPayDto = paymentDtoKeyedObj.updatedBsbFromPayDto;
        this.maccId = paymentDtoKeyedObj.maccId;
        this.warnings = paymentDtoKeyedObj.warnings;
        this.deviceNumber = paymentDtoKeyedObj.deviceNumber;
        this.dailyLimitDto = paymentDtoKeyedObj.dailyLimitDto;
        this.errors = paymentDtoKeyedObj.errors;
        this.opType = paymentDtoKeyedObj.opType;
        this.receiptId = paymentDtoKeyedObj.receiptId;
    }

    @Override
    public AccountKey getKey() {
        return key;
    }

    public void setKey(AccountKey key) {
        this.key = key;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean isPrimary) {
        this.primary = isPrimary;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean isRecurring) {
        this.recurring = isRecurring;
    }

    public String getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(String repeatEndDate) {
        this.repeatEndDate = repeatEndDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getEndRepeat() {
        return endRepeat;
    }

    public void setEndRepeat(String endRepeat) {
        this.endRepeat = endRepeat;
    }

    public String getEndRepeatNumber() {
        return endRepeatNumber;
    }

    public void setEndRepeatNumber(String endRepeatNumber) {
        this.endRepeatNumber = endRepeatNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public PayeeDto getToPayteeDto() {
        return toPayteeDto;
    }

    public void setToPayteeDto(PayeeDto toPayteeDto) {
        this.toPayteeDto = toPayteeDto;
    }

    public boolean getIsRecurring() {
        return recurring;
    }

    public void setIsRecurring(boolean isRecurring) {
        this.recurring = isRecurring;
    }

    public PayeeDto getFromPayDto() {
        return fromPayDto;
    }

    public void setFromPayDto(PayeeDto fromPayDto) {
        this.fromPayDto = fromPayDto;
    }

    public List<DomainApiErrorDto> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<DomainApiErrorDto> warnings) {
        this.warnings = warnings;
    }

    public String getRecieptNumber() {
        return recieptNumber;
    }

    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }

    public DailyLimitDto getDailyLimitDto() {
        return dailyLimitDto;
    }

    public void setDailyLimitDto(DailyLimitDto dailyLimitDto) {
        this.dailyLimitDto = dailyLimitDto;
    }

    public List<ServiceError> getErrors() {
        return errors;
    }

    public void setErrors(List<ServiceError> errors) {
        this.errors = errors;
    }

    public boolean isValidBsb() {
        return validBsb;
    }

    public void setValidBsb(boolean isValidBsb) {
        this.validBsb = isValidBsb;
    }

    public String getUpdatedBsbToPayteeDto() {
        return updatedBsbToPayteeDto;
    }

    public void setUpdatedBsbToPayteeDto(String updatedBsbToPayteeDto) {
        this.updatedBsbToPayteeDto = updatedBsbToPayteeDto;
    }

    public String getUpdatedBsbFromPayDto() {
        return updatedBsbFromPayDto;
    }

    public void setUpdatedBsbFromPayDto(String updatedBsbFromPayDto) {
        this.updatedBsbFromPayDto = updatedBsbFromPayDto;
    }

    public String getMaccId() {
        return maccId;
    }

    public void setMaccId(String maccId) {
        this.maccId = maccId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public String getBusinessChannel() { return businessChannel; }

    public void setBusinessChannel(String businessChannel) { this.businessChannel = businessChannel; }

    public String getClientIp() { return clientIp; }

    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public List<LinkedAccountStatus> getAssociatedAccounts() {
        return associatedAccounts;
    }

    public void setAssociatedAccounts(List<LinkedAccountStatus> associatedAccounts) {
        this.associatedAccounts = associatedAccounts;
    }
}
