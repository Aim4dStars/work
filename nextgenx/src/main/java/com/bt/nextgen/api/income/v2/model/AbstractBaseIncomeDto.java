package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractBaseIncomeDto extends BaseDto implements IncomeDto {

    public BigDecimal getAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        List<IncomeDto> incomes = getChildren();
        for (IncomeDto incomeDto : incomes) {
            amount = amount.add(incomeDto.getAmount());
        }
        return amount;
    }
}
