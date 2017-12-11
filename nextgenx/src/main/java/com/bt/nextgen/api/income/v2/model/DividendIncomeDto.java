package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.DividendIncome;
import com.bt.nextgen.service.integration.income.Income;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class DividendIncomeDto extends AbstractIncomeDto {
    private DateTime executionDate;
    private BigDecimal quantity;
    private BigDecimal incomeRate;
    private BigDecimal frankedDividend;
    private BigDecimal unfrankedDividend;
    private BigDecimal frankingCredit;
    private boolean wrapIncome;

    public DividendIncomeDto(String name, String code, Income income) {
        // Dividend reinvestment for income accrued must use cost base instead
        super(name, code, income.getPaymentDate(), ((DividendIncome) income).getAmount());
        DividendIncome dividendIncome = (DividendIncome) income;
        this.executionDate = dividendIncome.getExecutionDate();
        this.quantity = dividendIncome.getQuantity();
        this.incomeRate = dividendIncome.getIncomeRate();
        this.frankedDividend = dividendIncome.getFrankedDividend();
        this.unfrankedDividend = dividendIncome.getUnfrankedDividend();
        this.frankingCredit = dividendIncome.getFrankingCredit();
    }

    public DateTime getExecutionDate() {
        return executionDate;
    }

    public BigDecimal getQuantity() {
        if (quantity == null) {
            quantity = new BigDecimal(0.00);
        }
        return quantity;
    }

    public BigDecimal getIncomeRate() {
        if (incomeRate == null) {
            incomeRate = new BigDecimal(0.00);
        }
        return incomeRate;
    }

    public BigDecimal getFrankedDividend() {
        if (frankedDividend == null) {
            frankedDividend = new BigDecimal(0.00);
        }
        return frankedDividend;
    }

    public BigDecimal getUnfrankedDividend() {
        if (unfrankedDividend == null) {
            unfrankedDividend = new BigDecimal(0.00);
        }
        return unfrankedDividend;
    }

    public BigDecimal getFrankingCredit() {
        if (!wrapIncome && frankingCredit == null) {
            frankingCredit = new BigDecimal(0.00);
        }
        return frankingCredit;
    }

    public Boolean isWrapIncome() {
        return wrapIncome;
    }

    public void setWrapIncome(Boolean wrapIncome) {
        this.wrapIncome = wrapIncome;
    }
}
