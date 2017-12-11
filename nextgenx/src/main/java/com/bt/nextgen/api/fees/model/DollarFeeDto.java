package com.bt.nextgen.api.fees.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;

public class DollarFeeDto extends BaseDto implements FeesComponentDto
{
	private boolean cpiindex;
	private BigDecimal amount;
	private String date;
	private String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public boolean isCpiindex()
	{
		return cpiindex;
	}

	public void setCpiindex(boolean cpiindex)
	{
		this.cpiindex = cpiindex;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	private String label;

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;

	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

}
