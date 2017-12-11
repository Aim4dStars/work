package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

@Deprecated
public class CashIncomeDto extends IncomeDto
{
	public CashIncomeDto(String name, String code, DateTime paymentDate, BigDecimal amount)
	{
		super(name, code, paymentDate, amount);
	}

}
