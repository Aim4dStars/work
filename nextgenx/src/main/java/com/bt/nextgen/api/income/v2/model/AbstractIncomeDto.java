package com.bt.nextgen.api.income.v2.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.NamedDto;

public abstract class AbstractIncomeDto extends BaseDto implements NamedDto
{
	private String name;
	private String code;
	private DateTime paymentDate;
	private BigDecimal amount;

	public AbstractIncomeDto(String name, String code, DateTime paymentDate, BigDecimal amount)
	{
		super();
		this.name = name;
		this.code = code;
		this.paymentDate = paymentDate;
		this.amount = amount;
	}

	public String getName()
	{
		return name;
	}

	public String getCode()
	{
		return code;
	}

	public DateTime getPaymentDate()
	{
		return paymentDate;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}
}
