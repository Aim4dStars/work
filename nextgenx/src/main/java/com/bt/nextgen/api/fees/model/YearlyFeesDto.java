package com.bt.nextgen.api.fees.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class YearlyFeesDto extends BaseDto implements KeyedDto <OneOffFeesKey>
{
	private BigDecimal yearlyFees;
	private OneOffFeesKey key;

	public BigDecimal getYearlyFees()
	{
		return yearlyFees;
	}

	public void setYearlyFees(BigDecimal yearlyFees)
	{
		this.yearlyFees = yearlyFees;
	}

	public OneOffFeesKey getKey()
	{
		return key;
	}

	public void setKey(OneOffFeesKey key)
	{
		this.key = key;
	}

}
