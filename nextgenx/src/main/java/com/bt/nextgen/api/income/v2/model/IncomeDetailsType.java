package com.bt.nextgen.api.income.v2.model;

public enum IncomeDetailsType
{
	ACCRUED("Pending"), RECEIVED("Complete");

	private String status;

	private IncomeDetailsType(String status)
	{
		this.status = status;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}
