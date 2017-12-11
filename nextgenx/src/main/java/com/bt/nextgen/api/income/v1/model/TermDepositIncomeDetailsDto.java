package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;


@Deprecated
public class TermDepositIncomeDetailsDto extends BaseDto
{
	private final List <TermDepositIncomeDto> incomes;
	private BigDecimal incomeTotal;

	public TermDepositIncomeDetailsDto(List <TermDepositIncomeDto> incomes, BigDecimal incomeTotal)
	{
		super();
		this.incomes = incomes;
		this.incomeTotal = incomeTotal;
	}

	public List <TermDepositIncomeDto> getIncomes()
	{
		return incomes;
	}

	public BigDecimal getIncomeTotal()
	{
		return incomeTotal;
	}
}
