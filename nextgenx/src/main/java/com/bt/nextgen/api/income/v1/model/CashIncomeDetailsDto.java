package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;

@Deprecated
public class CashIncomeDetailsDto extends BaseDto
{
	private final List <CashIncomeDto> incomes;
	private final BigDecimal incomeTotal;

	public CashIncomeDetailsDto(List <CashIncomeDto> incomes, BigDecimal incomeTotal)
	{
		super();
		this.incomes = incomes;
		this.incomeTotal = incomeTotal;
	}

	public List <CashIncomeDto> getIncomes()
	{
		return incomes;
	}

	public BigDecimal getIncomeTotal()
	{
		return incomeTotal;
	}
}
