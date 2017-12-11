package com.bt.nextgen.api.performance.model;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import org.joda.time.DateTime;

public class SubaccountPerformanceKey extends DateRangeAccountKey
{
	public SubaccountPerformanceKey(String investmentId, DateTime startDate, DateTime endDate)
	{
		super(investmentId, startDate, endDate);
	}

}