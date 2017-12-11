package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.Income;
import com.bt.nextgen.service.integration.income.InterestIncome;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class InterestIncomeDto extends AbstractIncomeDto {
    private DateTime executionDate;
    private BigDecimal quantity;
    private BigDecimal incomeRate;

    public InterestIncomeDto(String name, String code, Income income) {
        super(name, code, ((InterestIncome) income).getPaymentDate(), ((InterestIncome) income).getAmount());
        InterestIncome interestIncome = (InterestIncome) income;
        this.executionDate = interestIncome.getExecutionDate();
        this.quantity = interestIncome.getQuantity();
        this.incomeRate = interestIncome.getIncomeRate();
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
