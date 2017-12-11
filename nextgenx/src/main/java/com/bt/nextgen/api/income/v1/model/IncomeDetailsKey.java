package com.bt.nextgen.api.income.v1.model;

import org.joda.time.DateTime;

@Deprecated
public class IncomeDetailsKey
{
	private String accountId;
	private IncomeDetailsType type;
	private DateTime startDate;
	private DateTime endDate;

	public IncomeDetailsKey(String accountId, IncomeDetailsType type, DateTime startDate, DateTime endDate)
	{
		this.accountId = accountId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.type = type;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public IncomeDetailsType getType()
	{
		return type;
	}

	public void setType(IncomeDetailsType type)
	{
		this.type = type;
	}

	public DateTime getStartDate()
	{
		return startDate;
	}

	public void setStartDate(DateTime startDate)
	{
		this.startDate = startDate;
	}

	public DateTime getEndDate()
	{
		return endDate;
	}

	public void setEndDate(DateTime endDate)
	{
		this.endDate = endDate;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		return result;
	}

    @Override
    @SuppressWarnings("squid:S1142") // IDE generated method
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IncomeDetailsKey other = (IncomeDetailsKey)obj;
		if (accountId == null)
		{
			if (other.accountId != null)
				return false;
		}
		else if (!accountId.equals(other.accountId))
			return false;
		return true;
	}

}
