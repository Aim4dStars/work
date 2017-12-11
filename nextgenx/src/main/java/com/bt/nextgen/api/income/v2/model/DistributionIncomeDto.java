package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.DistributionIncome;
import com.bt.nextgen.service.integration.income.Income;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class DistributionIncomeDto extends AbstractIncomeDto {
    private DateTime executionDate;
    private BigDecimal quantity;
    private BigDecimal incomeRate;

    public DistributionIncomeDto(String name, String code, Income income) {
        super(name, code, ((DistributionIncome) income).getPaymentDate(), ((DistributionIncome) income).getAmount());
        DistributionIncome distributionIncome = (DistributionIncome) income;
        this.executionDate = distributionIncome.getExecutionDate();
        this.quantity = distributionIncome.getQuantity();
        this.incomeRate = distributionIncome.getIncomeRate();
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

}
