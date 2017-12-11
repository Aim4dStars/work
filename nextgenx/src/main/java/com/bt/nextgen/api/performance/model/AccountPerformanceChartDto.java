package com.bt.nextgen.api.performance.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.service.integration.performance.PerformancePeriodType;

public class AccountPerformanceChartDto extends BaseDto implements KeyedDto <AccountPerformanceKey>
{
	private AccountPerformanceKey accountPerformanceKey;
	private List <ReportDataPointDto> activeReturnData;
	private List <ReportDataPointDto> totalPerformanceData;
	private List <ReportDataPointDto> benchmarkData;

	private PerformancePeriodType detailedPeriodType;
	private PerformancePeriodType summaryPeriodType;

	private List <String> colHeaders;

	public AccountPerformanceChartDto(AccountPerformanceKey accountPerformanceKey, List <ReportDataPointDto> activeReturnData,
		List <ReportDataPointDto> totalPerformanceData, List <ReportDataPointDto> benchmarkData,
		PerformancePeriodType detailedPeriodType, PerformancePeriodType summaryPeriodType, List <String> colHeaders)
	{
		this.accountPerformanceKey = accountPerformanceKey;
		this.activeReturnData = activeReturnData;
		this.totalPerformanceData = totalPerformanceData;
		this.benchmarkData = benchmarkData;
		this.detailedPeriodType = detailedPeriodType;
		this.summaryPeriodType = summaryPeriodType;
		this.colHeaders = colHeaders;
	}

	@Override
	public AccountPerformanceKey getKey()
	{
		return accountPerformanceKey;
	}

	public AccountPerformanceKey getAccountPerformanceKey()
	{
		return accountPerformanceKey;
	}

	public List <ReportDataPointDto> getActiveReturnData()
	{
		return activeReturnData;
	}

	public List <ReportDataPointDto> getTotalPerformanceData()
	{
		return totalPerformanceData;
	}

	public List <ReportDataPointDto> getBenchmarkData()
	{
		return benchmarkData;
	}

	public List <String> getColHeaders()
	{
		return colHeaders;
	}

	public PerformancePeriodType getDetailedPeriodType()
	{
		return detailedPeriodType;
	}

	public PerformancePeriodType getSummaryPeriodType()
	{
		return summaryPeriodType;
	}
}
