package com.bt.nextgen.api.income.v1.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

@Deprecated
@SuppressWarnings("squid:S00107") // fixed in v2
public class DividendIncomeDto extends IncomeDto {
    private DateTime executionDate;
    private BigDecimal quantity;
    private BigDecimal incomeRate;
    private BigDecimal frankedDividend;
    private BigDecimal unfrankedDividend;
    private BigDecimal frankingCredit;

    public DividendIncomeDto(String name, String code, DateTime executionDate, DateTime paymentDate, BigDecimal quantity,
            BigDecimal incomeRate, BigDecimal frankedDividend, BigDecimal unfrankedDividend, BigDecimal frankingCredit,
            BigDecimal amount) {
        super(name, code, paymentDate, amount);
        this.executionDate = executionDate;
        this.quantity = quantity;
        this.incomeRate = incomeRate;
        this.frankedDividend = frankedDividend;
        this.unfrankedDividend = unfrankedDividend;
        this.frankingCredit = frankingCredit;
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
        if (frankingCredit == null) {
            frankingCredit = new BigDecimal(0.00);
        }
        return frankingCredit;
    }
}
