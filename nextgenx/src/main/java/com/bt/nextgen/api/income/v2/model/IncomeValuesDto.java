package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class IncomeValuesDto extends AbstractBaseIncomeDto implements KeyedDto<IncomeDetailsKey> {
    private IncomeDetailsKey incomeDetailsKey;
    private final List<IncomeDto> investmentTypes;
    private IncomeValueTotals incomeValueTotals;

    public IncomeValuesDto(IncomeDetailsKey incomeDetailsKey, List<IncomeDto> investmentTypes) {
        this.incomeDetailsKey = incomeDetailsKey;
        this.investmentTypes = investmentTypes;
        incomeValueTotals = new IncomeValueTotals(investmentTypes);
    }

    public List<IncomeDto> getInvestmentTypes() {
        return investmentTypes;
    }

    @Override
    public IncomeDetailsKey getKey() {
        return incomeDetailsKey;
    }

    public IncomeValueTotals getIncomeValueTotals() {
        return incomeValueTotals;
    }

    @Override
    public List<IncomeDto> getChildren() {
        return investmentTypes;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getCode() {
        return null;
    }

}
