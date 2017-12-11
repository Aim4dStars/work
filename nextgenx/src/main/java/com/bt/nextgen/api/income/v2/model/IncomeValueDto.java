package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.IncomeType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class IncomeValueDto implements IncomeDto {

    private String name;
    private String code;
    private DateTime paymentDate;
    private BigDecimal amount;
    private DateTime maturityDate;
    private String term;
    private String paymentFrequency;
    private DateTime executionDate;
    private BigDecimal quantity;
    private BigDecimal incomeRate;
    private BigDecimal frankedDividend;
    private BigDecimal unfrankedDividend;
    private BigDecimal frankingCredit;
    private IncomeType incomeType;
    private Boolean wrapIncome;

    // Cash
    public IncomeValueDto(CashIncomeDto cashIncomeDto) {
        this.name = cashIncomeDto.getName();
        this.code = cashIncomeDto.getCode();
        this.paymentDate = cashIncomeDto.getPaymentDate();
        this.amount = cashIncomeDto.getAmount();
        this.incomeType = cashIncomeDto.getIncomeType();
    }

    // TD
    public IncomeValueDto(TermDepositIncomeDto termDepositIncomeDto) {
        this.name = termDepositIncomeDto.getName();
        this.code = termDepositIncomeDto.getCode();
        this.paymentDate = termDepositIncomeDto.getPaymentDate();
        this.amount = termDepositIncomeDto.getAmount();
        this.maturityDate = termDepositIncomeDto.getMaturityDate();
        this.term = termDepositIncomeDto.getTerm();
        this.paymentFrequency = termDepositIncomeDto.getPaymentFrequency();
        this.incomeType = IncomeType.INTEREST;
        this.wrapIncome = termDepositIncomeDto.isWrapTermDeposit();
    }

    // Distribution
    public IncomeValueDto(DistributionIncomeDto distributionIncomeDto) {
        this.name = distributionIncomeDto.getName();
        this.code = distributionIncomeDto.getCode();
        this.paymentDate = distributionIncomeDto.getPaymentDate();
        this.amount = distributionIncomeDto.getAmount();
        this.executionDate = distributionIncomeDto.getExecutionDate();
        this.quantity = distributionIncomeDto.getQuantity();
        this.incomeRate = distributionIncomeDto.getIncomeRate();
        this.incomeType = IncomeType.DISTRIBUTION;
    }

    // Dividend
    public IncomeValueDto(DividendIncomeDto dividendIncomeDto) {
        this.name = dividendIncomeDto.getName();
        this.code = dividendIncomeDto.getCode();
        this.paymentDate = dividendIncomeDto.getPaymentDate();
        this.amount = dividendIncomeDto.getAmount();
        this.executionDate = dividendIncomeDto.getExecutionDate();
        this.quantity = dividendIncomeDto.getQuantity();
        this.incomeRate = dividendIncomeDto.getIncomeRate();
        this.frankedDividend = dividendIncomeDto.getFrankedDividend();
        this.unfrankedDividend = dividendIncomeDto.getUnfrankedDividend();
        this.frankingCredit = dividendIncomeDto.getFrankingCredit();
        this.incomeType = IncomeType.DIVIDEND;
        this.wrapIncome = dividendIncomeDto.isWrapIncome();
    }

    // Fee rebate
    public IncomeValueDto(FeeRebateIncomeDto feeRebateIncomeDto) {
        this.name = feeRebateIncomeDto.getName();
        this.code = feeRebateIncomeDto.getCode();
        this.paymentDate = feeRebateIncomeDto.getPaymentDate();
        this.amount = feeRebateIncomeDto.getAmount();
        this.incomeType = IncomeType.DISTRIBUTION;
    }

    // interest
    public IncomeValueDto(InterestIncomeDto interest) {
        this.name = interest.getName();
        this.code = interest.getCode();
        this.paymentDate = interest.getPaymentDate();
        this.amount = interest.getAmount();
        this.executionDate = interest.getExecutionDate();
        this.quantity = interest.getQuantity();
        this.incomeRate = interest.getIncomeRate();
        this.incomeType = IncomeType.INTEREST;
    }

    // TODO This is used by a legay cvs report, not the api. It should be removed when the csv is upgraded
    public IncomeValueDto(IncomeValueDto incomeValue) {
        this.name = incomeValue.name;
        this.code = incomeValue.code;
        this.paymentDate = incomeValue.paymentDate;
        this.maturityDate = incomeValue.maturityDate;
        this.amount = incomeValue.amount;
        this.executionDate = incomeValue.executionDate;
        this.quantity = incomeValue.quantity;
        this.incomeRate = incomeValue.incomeRate;
        this.frankedDividend = incomeValue.frankedDividend;
        this.unfrankedDividend = incomeValue.unfrankedDividend;
        this.frankingCredit = incomeValue.frankingCredit;
        this.wrapIncome = incomeValue.wrapIncome;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public DateTime getPaymentDate() {
        return paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public String getTerm() {
        return term;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

    public DateTime getExecutionDate() {
        return executionDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getIncomeRate() {
        return incomeRate;
    }

    public BigDecimal getFrankedDividend() {
        return frankedDividend;
    }

    public BigDecimal getUnfrankedDividend() {
        return unfrankedDividend;
    }

    public BigDecimal getFrankingCredit() {
        return frankingCredit;
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public Boolean getWrapIncome() {
        return wrapIncome;
    }

    @Override
    public List<IncomeDto> getChildren() {
        return Collections.emptyList();
    }

}
