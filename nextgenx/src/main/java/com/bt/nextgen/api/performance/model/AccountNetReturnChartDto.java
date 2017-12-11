package com.bt.nextgen.api.performance.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class AccountNetReturnChartDto extends BaseDto implements KeyedDto <AccountPerformanceKey>
{
	private final AccountPerformanceKey accountPerformanceKey;
	private final List <BigDecimal> closingBalances;
	private final List <BigDecimal> netReturns;
	private final List <String> colHeaders;

	public AccountNetReturnChartDto(AccountPerformanceKey accountPerformanceKey, List <BigDecimal> closingBalances,
		List <BigDecimal> netReturns, List <String> colHeaders)
	{
		this.accountPerformanceKey = accountPerformanceKey;
		this.closingBalances = closingBalances;
		this.netReturns = netReturns;
		this.colHeaders = colHeaders;
	}

	public AccountPerformanceKey getAccountPerformanceKey()
	{
		return accountPerformanceKey;
	}

	public List <BigDecimal> getClosingBalances()
	{
		return closingBalances;
	}

	public List <BigDecimal> getNetReturns()
	{
		return netReturns;
	}

	public List <String> getColHeaders()
	{
		return colHeaders;
	}

	@Override
	public AccountPerformanceKey getKey()
	{
		return accountPerformanceKey;
	}
}
