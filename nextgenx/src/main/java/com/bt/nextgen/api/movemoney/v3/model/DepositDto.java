package com.bt.nextgen.api.movemoney.v3.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.movemoney.DepositDetails;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.time.FastDateFormat;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = DepositDto.class)
public class DepositDto extends BaseDto implements KeyedDto<DepositKey> {
    public static final FastDateFormat dateFormat = FastDateFormat.getInstance("dd MMM yyyy");

    private DepositKey key;
    private String transactionSeq;
    private AccountKey accountKey;
    private String currency;
    private boolean isRecurring;
    private DateTime repeatEndDate;
    private BigDecimal amount;
    private String description;
    private DateTime transactionDate;
    private String endRepeat;
    private String endRepeatNumber;
    private PayeeDto fromPayDto;
    private String frequency;
    private String status;
    private String receiptNumber;
    private String depositType;
    private String orderType;
    private List<DomainApiErrorDto> errors;
    private List<DomainApiErrorDto> warnings;

    public DepositDto() {
        // default constructor
    }

    public DepositDto(DepositDetails deposit) {
        this.key = new DepositKey(deposit.getDepositId());
        this.transactionSeq = deposit.getTransactionSeq();
        this.amount = deposit.getDepositAmount();
        this.description = deposit.getDescription();
        this.receiptNumber = deposit.getReceiptNumber();
        this.transactionDate = deposit.getTransactionDate();
        this.depositType = deposit.getContributionType() != null ? deposit.getContributionType().getDisplayName() : "";
        this.orderType = deposit.getOrderType() != null ? deposit.getOrderType().getName() : "";
        this.fromPayDto = new PayeeDto();
        this.fromPayDto.setAccountId(deposit.getPayerAccount());
        this.fromPayDto.setCode(deposit.getPayerBsb());
        this.fromPayDto.setAccountName(deposit.getPayerName());
        this.status = deposit.getStatus() != null ? deposit.getStatus().getDisplayName() : "";
        this.frequency = deposit.getRecurringFrequency() != null ? deposit.getRecurringFrequency().name() : "";

        if (deposit instanceof RecurringDepositDetails) {
            RecurringDepositDetails recurringDeposit = (RecurringDepositDetails) deposit;
            this.repeatEndDate = recurringDeposit.getEndDate();
            this.transactionDate = recurringDeposit.getStartDate();
            this.endRepeatNumber = recurringDeposit.getMaxCount() != null ? recurringDeposit.getMaxCount().toString() : "";
        }
    }

    public DepositDto(DepositDto depositDtoKeyedObj) {
        super();
        this.key = depositDtoKeyedObj.key;
        this.transactionSeq = depositDtoKeyedObj.transactionSeq;
        this.accountKey = depositDtoKeyedObj.accountKey;
        this.currency = depositDtoKeyedObj.currency;
        this.isRecurring = depositDtoKeyedObj.isRecurring;
        this.repeatEndDate = depositDtoKeyedObj.repeatEndDate;
        this.amount = depositDtoKeyedObj.amount;
        this.description = depositDtoKeyedObj.description;
        this.transactionDate = depositDtoKeyedObj.transactionDate;
        this.endRepeat = depositDtoKeyedObj.endRepeat;
        this.endRepeatNumber = depositDtoKeyedObj.endRepeatNumber;
        this.fromPayDto = depositDtoKeyedObj.fromPayDto;
        this.frequency = depositDtoKeyedObj.frequency;
        this.receiptNumber = depositDtoKeyedObj.receiptNumber;
        this.errors = depositDtoKeyedObj.errors;
        this.warnings = depositDtoKeyedObj.warnings;
        this.depositType = depositDtoKeyedObj.depositType;
        this.status = depositDtoKeyedObj.status;
        this.orderType = depositDtoKeyedObj.orderType;
    }

    @Override
    public DepositKey getKey() {
        return key;
    }

    public void setKey(DepositKey key) {
        this.key = key;
    }

    public String getTransactionSeq() {
        return transactionSeq;
    }

    public void setTransactionSeq(String transactionSeq) {
        this.transactionSeq = transactionSeq;
    }

    public AccountKey getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(AccountKey accountKey) {
        this.accountKey = accountKey;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
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

    public String getEndRepeatNumber() {
        return endRepeatNumber;
    }

    public void setEndRepeatNumber(String endRepeatNumber) {
        this.endRepeatNumber = endRepeatNumber;
    }

    public PayeeDto getFromPayDto() {
        return fromPayDto;
    }

    public void setFromPayDto(PayeeDto fromPayDto) {
        this.fromPayDto = fromPayDto;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getDepositType() {
        return depositType;
    }

    public void setDepositType(String depositType) {
        this.depositType = depositType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
