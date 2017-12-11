package com.bt.nextgen.api.performance.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;

public class ReportDataPointDto extends BaseDto
{
	private DateTime date;
	private BigDecimal value;

	public ReportDataPointDto(DateTime date, BigDecimal value)
	{
		this.date = date;
		this.value = value;
	}

	public DateTime getDate()
	{
		return date;
	}

	public BigDecimal getValue()
	{
		return value;
	}

}
