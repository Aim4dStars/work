package com.bt.nextgen.api.movemoney.v2.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class PaymentDto extends BaseDto implements KeyedDto<AccountKey> {
    private AccountKey key;

    private String currency;

    private BigDecimal limit;

    private String paymentId;

    private boolean recurring;

    private DateTime repeatEndDate;

    private BigDecimal amount;

    private String description;

    private String transactionId;

    private DateTime transactionDate;

    // private PaymentRepeatsEnd endRepeat;

    private String endRepeat;

    private BigInteger endRepeatNumber;

    private String errorMessage;

    private PayeeDto toPayeeDto;

    private PayeeDto fromPayDto;

    private String frequency;

    private boolean primary;

    private String receiptNumber;

    private boolean validBsb;

    private String updatedBsbToPayeeDto;

    private String updatedBsbFromPayDto;

    private String maccId;

    private List<DomainApiErrorDto> warnings;

    private String deviceNumber;

    private DailyLimitDto dailyLimitDto;

    private List<DomainApiErrorDto> errors;

    private String opType;

    private String receiptId;

    private String indexationType;

    private BigDecimal indexationAmount;

    private String withdrawalType;

    private String pensionPaymentType;

    private boolean twoFaRequired;

    private String paymentAction;

    private String transSeqNo;

    private String businessChannel;

    private String clientIp;

    public PaymentDto(AccountKey accountKey) {
        // Default Constructor - Being referred in PaymentDtoServiceImpl and PaymentUtil - TODO CI task to remove this in V2

    }

    public PaymentDto() {
        // Default Constructor - Being referred in PaymentDtoServiceImpl and PaymentUtil - TODO CI task to remove this in V2

    }

    public PaymentDto(PaymentDto paymentDto) {
        super();
        this.key = paymentDto.key;
        this.currency = paymentDto.currency;
        this.limit = paymentDto.limit;
        this.paymentId = paymentDto.paymentId;
        this.recurring = paymentDto.recurring;
        this.repeatEndDate = paymentDto.repeatEndDate;
        this.amount = paymentDto.amount;
        this.description = paymentDto.description;
        this.transactionId = paymentDto.transactionId;
        this.transactionDate = paymentDto.transactionDate;
        this.endRepeat = paymentDto.endRepeat;
        this.endRepeatNumber = paymentDto.endRepeatNumber;
        this.errorMessage = paymentDto.errorMessage;
        this.toPayeeDto = paymentDto.toPayeeDto;
        this.fromPayDto = paymentDto.fromPayDto;
        this.frequency = paymentDto.frequency;
        this.primary = paymentDto.primary;
        this.receiptNumber = paymentDto.receiptNumber;
        this.validBsb = paymentDto.validBsb;
        this.updatedBsbToPayeeDto = paymentDto.updatedBsbToPayeeDto;
        this.updatedBsbFromPayDto = paymentDto.updatedBsbFromPayDto;
        this.maccId = paymentDto.maccId;
        this.warnings = paymentDto.warnings;
        this.deviceNumber = paymentDto.deviceNumber;
        this.dailyLimitDto = paymentDto.dailyLimitDto;
        this.errors = paymentDto.errors;
        this.opType = paymentDto.opType;
        this.receiptId = paymentDto.receiptId;
        this.paymentAction = paymentDto.paymentAction;
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

    public DateTime getRepeatEndDate() {
        return repeatEndDate;
    }

    public void setRepeatEndDate(DateTime repeatEndDate) {
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

    public DateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(DateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getEndRepeat() {
        return endRepeat;
    }

    public void setEndRepeat(String endRepeat) {
        this.endRepeat = endRepeat;
    }

    public BigInteger getEndRepeatNumber() {
        return endRepeatNumber;
    }

    public void setEndRepeatNumber(BigInteger endRepeatNumber) {
        this.endRepeatNumber = endRepeatNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public PayeeDto getToPayeeDto() {
        return toPayeeDto;
    }

    public void setToPayeeDto(PayeeDto toPayeeDto) {
        this.toPayeeDto = toPayeeDto;
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

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
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

    public List<DomainApiErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<DomainApiErrorDto> errors) {
        this.errors = errors;
    }

    public boolean isValidBsb() {
        return validBsb;
    }

    public void setValidBsb(boolean isValidBsb) {
        this.validBsb = isValidBsb;
    }

    public String getUpdatedBsbToPayeeDto() {
        return updatedBsbToPayeeDto;
    }

    public void setUpdatedBsbToPayeeDto(String updatedBsbToPayeeDto) {
        this.updatedBsbToPayeeDto = updatedBsbToPayeeDto;
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

    public String getIndexationType() {
        return indexationType;
    }

    public void setIndexationType(String indexationType) {
        this.indexationType = indexationType;
    }

    public BigDecimal getIndexationAmount() {
        return indexationAmount;
    }

    public void setIndexationAmount(BigDecimal indexationAmount) {
        this.indexationAmount = indexationAmount;
    }

    public String getWithdrawalType() {
        return withdrawalType;
    }

    public void setWithdrawalType(String withdrawalType) {
        this.withdrawalType = withdrawalType;
    }

    public String getPensionPaymentType() {
        return pensionPaymentType;
    }

    public void setPensionPaymentType(String pensionPaymentType) {
        this.pensionPaymentType = pensionPaymentType;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public boolean isTwoFaRequired() {
        return twoFaRequired;
    }

    public void setTwoFaRequired(boolean twoFaRequired) {
        this.twoFaRequired = twoFaRequired;
    }

    public String getPaymentAction() {
        return paymentAction;
    }

    public void setPaymentAction(String paymentAction) {
        this.paymentAction = paymentAction;
    }

    public String getTransSeqNo() {
        return transSeqNo;
    }

    public void setTransSeqNo(String transSeqNo) {
        this.transSeqNo = transSeqNo;
    }

    public String getBusinessChannel() { return businessChannel; }

    public void setBusinessChannel(String businessChannel) { this.businessChannel = businessChannel; }

    public String getClientIp() { return clientIp; }

    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
}
