package com.bt.nextgen.api.financialyear.model;

import org.joda.time.LocalDate;

import com.bt.nextgen.core.api.model.BaseDto;

public class FinancialYearDto extends BaseDto
{
	private LocalDate key;
	private String displayText;
	private LocalDate startDate;
	private LocalDate endDate;

	public LocalDate getKey()
	{
		return key;
	}

	public void setKey(LocalDate key)
	{
		this.key = key;
	}

	public String getDisplayText()
	{
		return displayText;
	}

	public void setDisplayText(String displayText)
	{
		this.displayText = displayText;
	}

	public LocalDate getStartDate()
	{
		return startDate;
	}

	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}
}
