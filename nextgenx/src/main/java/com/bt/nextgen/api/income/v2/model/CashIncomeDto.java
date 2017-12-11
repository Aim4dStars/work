package com.bt.nextgen.api.income.v2.model;

import com.bt.nextgen.service.integration.income.IncomeType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class CashIncomeDto extends AbstractIncomeDto
{
    private IncomeType incomeType;

    public CashIncomeDto(String name, String code, DateTime paymentDate, BigDecimal amount, IncomeType incomeType)
	{
		super(name, code, paymentDate, amount);
        this.incomeType = incomeType;
	}

    public IncomeType getIncomeType() {
        return incomeType;
    }
}
