package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

@Deprecated
public class IncomeValue {

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
    private boolean feeRebate;
    private BigDecimal frankedDividend;
    private BigDecimal unfrankedDividend;
    private BigDecimal frankingCredit;

    // Cash
    public IncomeValue(CashIncomeDto cashIncomeDto) {
        this.name = cashIncomeDto.getName();
        this.code = cashIncomeDto.getCode();
        this.paymentDate = cashIncomeDto.getPaymentDate();
        this.amount = cashIncomeDto.getAmount();
    }

    // TD
    public IncomeValue(TermDepositIncomeDto termDepositIncomeDto) {
        this.name = termDepositIncomeDto.getName();
        this.code = termDepositIncomeDto.getCode();
        this.paymentDate = termDepositIncomeDto.getPaymentDate();
        this.amount = termDepositIncomeDto.getAmount();
        this.maturityDate = termDepositIncomeDto.getMaturityDate();
        this.term = termDepositIncomeDto.getTerm();
        this.paymentFrequency = termDepositIncomeDto.getPaymentFrequency();
    }

    // Distribution
    public IncomeValue(DistributionIncomeDto distributionIncomeDto) {
        this.name = distributionIncomeDto.getName();
        this.code = distributionIncomeDto.getCode();
        this.paymentDate = distributionIncomeDto.getPaymentDate();
        this.amount = distributionIncomeDto.getAmount();
        this.executionDate = distributionIncomeDto.getExecutionDate();
        this.quantity = distributionIncomeDto.getQuantity();
        this.incomeRate = distributionIncomeDto.getIncomeRate();
        this.feeRebate = distributionIncomeDto.getIsFeeRebate();
    }

    // Dividend
    public IncomeValue(DividendIncomeDto dividendIncomeDto) {
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
    }

    // Fee rebate
    public IncomeValue(FeeRebateIncomeDto feeRebateIncomeDto) {
        this.name = feeRebateIncomeDto.getName();
        this.code = feeRebateIncomeDto.getCode();
        this.paymentDate = feeRebateIncomeDto.getPaymentDate();
        this.amount = feeRebateIncomeDto.getAmount();
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

    public boolean isFeeRebate() {
        return feeRebate;
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
}
