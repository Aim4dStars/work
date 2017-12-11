package com.bt.nextgen.api.performance.model;

import com.bt.nextgen.api.account.v1.model.PerformanceDto;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

public class PerformancePeriodicReportDto extends BaseDto
{
	private PerformancePeriodType periodType;
	private List <PerformanceDto> performanceData;
	private PerformanceDto periodReturnData;
	private PerformanceDto sinceInceptionData;

	public PerformancePeriodicReportDto(PerformancePeriodType periodType, List <PerformanceDto> performanceData,
		PerformanceDto periodReturnData, PerformanceDto sinceInceptionData)
	{
		this.periodType = periodType;
		this.performanceData = performanceData;
		this.periodReturnData = periodReturnData;
		this.sinceInceptionData = sinceInceptionData;
	}

	public PerformancePeriodType getPeriodType()
	{
		return periodType;
	}

	public List <PerformanceDto> getPerformanceData()
	{
		return performanceData;
	}

	public PerformanceDto getPeriodReturnData()
	{
		return periodReturnData;
	}

	public PerformanceDto getSinceInceptionData()
	{
		return sinceInceptionData;
	}

}
