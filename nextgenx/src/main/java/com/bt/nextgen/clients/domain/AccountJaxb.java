package com.bt.nextgen.clients.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountJaxb
{
	private String accountName;
	private String wrapAccountId;
	private String accountType;
	private String adviserName;
	private String balance;
	private String portfolio;
	private String primaryFirstName;
	private String primaryLastName;
	private String clientId;

	public String getWrapAccountId()
	{
		return wrapAccountId;
	}

	public void setWrapAccountId(String wrapAccountId)
	{
		this.wrapAccountId = wrapAccountId;
	}

	public String getAccountType()
	{
		return accountType;
	}

	public void setAccountType(String accountType)
	{
		this.accountType = accountType;
	}

	public String getAdviserName()
	{
		return adviserName;
	}

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public void setAdviserName(String adviserName)
	{
		this.adviserName = adviserName;
	}

	public String getBalance()
	{
		return balance;
	}

	public void setBalance(String balance)
	{
		this.balance = balance;
	}

	public String getPortfolio()
	{
		return portfolio;
	}

	public void setPortfolio(String portfolio)
	{
		this.portfolio = portfolio;
	}

	public String getPrimaryFirstName()
	{
		return primaryFirstName;
	}

	public void setPrimaryFirstName(String primaryFirstName)
	{
		this.primaryFirstName = primaryFirstName;
	}

	public String getPrimaryLastName()
	{
		return primaryLastName;
	}

	public void setPrimaryLastName(String primaryLastName)
	{
		this.primaryLastName = primaryLastName;
	}

	public String getClientId()
	{
		return clientId;
	}

	public void setClientId(String clientId)
	{
		this.clientId = clientId;
	}
}
