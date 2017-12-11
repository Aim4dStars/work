package com.bt.nextgen.service.integration.cashcategorisation.model;

public class DepositKey
{
	private String accountId;
	private String depositId;
	private String date;

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public DepositKey(String depositId, String date)
	{
		this.setDepositId(depositId);
		this.date = date;
	}

	public String getAccountId()
	{
		return accountId;
	}

	public void setAccountId(String accountId)
	{
		this.accountId = accountId;
	}

	public String getDepositId()
	{
		return depositId;
	}

	public void setDepositId(String depositId)
	{
		this.depositId = depositId;
	}
}
