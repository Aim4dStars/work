package com.bt.nextgen.api.performance.model;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import org.joda.time.DateTime;

public class AccountPerformanceKey extends DateRangeAccountKey
{
	private String benchmarkId;

	public AccountPerformanceKey(String accountId, DateTime startDate, DateTime endDate, String benchmarkId)
	{
		super(accountId, startDate, endDate);
		this.benchmarkId = benchmarkId;
	}

	public String getBenchmarkId()
	{
		return benchmarkId;
	}

	public void setBenchmarkId(String benchmarkId)
	{
		this.benchmarkId = benchmarkId;
	}

}