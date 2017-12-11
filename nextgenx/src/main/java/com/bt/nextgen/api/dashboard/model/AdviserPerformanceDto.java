package com.bt.nextgen.api.dashboard.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;

public class AdviserPerformanceDto extends BaseDto
{
	private DateTime startDate;
	private DateTime endDate;
	private Integer accounts;
	private BigDecimal fua;
	private BigDecimal inflow;
	private BigDecimal outflow;
	private BigDecimal netflow;
	private BigDecimal fee;

	public AdviserPerformanceDto(DateTime startDate, DateTime endDate, Integer accounts, BigDecimal fua, BigDecimal inflow,
		BigDecimal outflow, BigDecimal netflow, BigDecimal fee)
	{
		this.startDate = startDate;
		this.endDate = endDate;
		this.accounts = accounts;
		this.fua = fua;
		this.inflow = inflow;
		this.outflow = outflow;
		this.netflow = netflow;
		this.fee = fee;
	}

	public DateTime getStartDate()
	{
		return startDate;
	}

	public DateTime getEndDate()
	{
		return endDate;
	}

	public Integer getAccounts()
	{
		return accounts;
	}

	public BigDecimal getFua()
	{
		return fua;
	}

	public BigDecimal getInflow()
	{
		return inflow;
	}

	public BigDecimal getOutflow()
	{
		return outflow;
	}

	public BigDecimal getNetflow()
	{
		return netflow;
	}

	public BigDecimal getFee()
	{
		return fee;
	}
}
