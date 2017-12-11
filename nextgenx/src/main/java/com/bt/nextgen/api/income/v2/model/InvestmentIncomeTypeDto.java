package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.IncomeType;

import java.math.BigDecimal;
import java.util.List;

public class InvestmentIncomeTypeDto extends AbstractBaseIncomeDto {
    private final IncomeType incomeType; // Div, Dist, Fee Rebates, Cash
    private final List<IncomeDto> incomeValues;
    private final BigDecimal incomeTotal;

    public InvestmentIncomeTypeDto(IncomeType incomeType, List<IncomeDto> incomeValues, BigDecimal incomeTotal) {
        this.incomeType = incomeType;
        this.incomeValues = incomeValues;
        this.incomeTotal = incomeTotal;
    }

    public IncomeType getIncomeType() {
        return incomeType;
    }

    public String getInvestmentIncomeTypeName() {
        return incomeType.getGroupDescription();
    }

    public List<IncomeDto> getIncomeValues() {
        return incomeValues;
    }

    public BigDecimal getIncomeTotal() {
        return incomeTotal;
    }

    @Override
    public List<IncomeDto> getChildren() {
        return incomeValues;
    }

    @Override
    public String getName() {
        return incomeType.getGroupDescription();
    }

    @Override
    public String getCode() {
        return null;
    }

}
