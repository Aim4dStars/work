package com.bt.nextgen.api.dashboard.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AdviserPerformanceSummaryDto extends BaseDto implements KeyedDto <PeriodKey>
{

	private PeriodKey periodKey;
	private BigDecimal totalFua;
	private BigDecimal totalInflow;
	private BigDecimal totalOutflow;
	private BigDecimal totalNetflow;
	private BigDecimal totalFee;
	private Integer totalAccounts;
	private List <AdviserPerformanceDto> performance;

	public AdviserPerformanceSummaryDto(PeriodKey periodKey, BigDecimal totalFua, BigDecimal totalInflow,
		BigDecimal totalOutflow, BigDecimal totalNetflow, BigDecimal totalFee, Integer totalAccounts,
		List <AdviserPerformanceDto> performance)
	{
		this.periodKey = periodKey;
		this.totalFua = totalFua;
		this.totalInflow = totalInflow;
		this.totalOutflow = totalOutflow;
		this.totalNetflow = totalNetflow;
		this.totalFee = totalFee;
		this.totalAccounts = totalAccounts;
		this.performance = performance;
	}

	public BigDecimal getTotalFua()
	{
		return totalFua;
	}

	public BigDecimal getTotalInflow()
	{
		return totalInflow;
	}

	public BigDecimal getTotalOutflow()
	{
		return totalOutflow;
	}

	public BigDecimal getTotalNetflow()
	{
		return totalNetflow;
	}

	public BigDecimal getTotalFee()
	{
		return totalFee;
	}

	public Integer getTotalAccounts()
	{
		return totalAccounts;
	}

	public List <AdviserPerformanceDto> getPerformance()
	{
		return this.performance;
	}

	@Override
	public PeriodKey getKey()
	{
		// TODO Auto-generated method stub
		return periodKey;
	}

}
