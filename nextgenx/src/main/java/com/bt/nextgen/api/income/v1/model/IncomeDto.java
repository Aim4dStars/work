package com.bt.nextgen.api.income.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.NamedDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@Deprecated
@SuppressWarnings("squid:S00118") // fixed in v2
public abstract class IncomeDto extends BaseDto implements NamedDto
{
	private String name;
	private String code;
	private DateTime paymentDate;
	private BigDecimal amount;

	public IncomeDto(String name, String code, DateTime paymentDate, BigDecimal amount)
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
