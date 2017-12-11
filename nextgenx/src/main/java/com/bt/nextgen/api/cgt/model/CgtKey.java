package com.bt.nextgen.api.cgt.model;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import org.joda.time.DateTime;

public class CgtKey extends AccountKey
{
	private DateTime startDate;
	private DateTime endDate;
	private String groupBy;

	public CgtKey(String accountId, DateTime startDate, DateTime endDate, String groupBy)
	{
		super(accountId);
		this.startDate = startDate;
		this.endDate = endDate;
		this.groupBy = groupBy;
	}

	public String getGroupBy()
	{
		return groupBy;
	}

	public DateTime getStartDate()
	{
		return startDate;
	}

	public DateTime getEndDate()
	{
		return endDate;
	}

}
