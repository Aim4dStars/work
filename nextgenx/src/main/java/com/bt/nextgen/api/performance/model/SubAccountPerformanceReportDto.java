package com.bt.nextgen.api.performance.model;

import java.util.List;

import org.joda.time.DateTime;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class SubAccountPerformanceReportDto extends BaseDto implements KeyedDto <AccountPerformanceKey>
{
	private AccountPerformanceKey accountPerformanceKey;
	private String assetId;
	private String investmentCode;
	private String investmentName;
	private DateTime calcFrom;
	private List <PerformanceReportDataDto> performanceData;
	private List <PerformanceReportDataDto> netReturnData;
	private List <String> colHeaders;

	public SubAccountPerformanceReportDto(AccountPerformanceKey accountPerformanceKey, String assetId, String investmentCode,
		String investmentName, DateTime calcFrom, List <PerformanceReportDataDto> performanceData,
		List <PerformanceReportDataDto> netReturnData, List <String> colHeaders)
	{
		this.accountPerformanceKey = accountPerformanceKey;
		this.assetId = assetId;
		this.investmentCode = investmentCode;
		this.investmentName = investmentName;
		this.calcFrom = calcFrom;
		this.performanceData = performanceData;
		this.netReturnData = netReturnData;
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

	public List <String> getColHeaders()
	{
		return colHeaders;
	}

	public List <PerformanceReportDataDto> getPerformanceData()
	{
		return performanceData;
	}

	public List <PerformanceReportDataDto> getNetReturnData()
	{
		return netReturnData;
	}

	public String getAssetId()
	{
		return assetId;
	}

	public DateTime getCalcFrom()
	{
		return calcFrom;
	}

	public String getInvestmentCode()
	{
		return investmentCode;
	}

	public String getInvestmentName()
	{
		return investmentName;
	}

}
