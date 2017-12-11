package com.bt.nextgen.service.integration.termdeposit;

public class TermDepositTrxRequestImpl implements TermDepositTrxRequest 
{
	private String portfolio; //contrprty
	private String asset;
	private String amount;
	private String renewMode;
	private String currencyCode;
	private String renewAmount;
	private String renewCount;
	
	public String getPortfolio() 
	{
		return portfolio;
	}
	public void setPortfolio(String portfolio) 
	{
		this.portfolio = portfolio;
	}
	public String getAsset() 
	{
		return asset;
	}
	public void setAsset(String asset) 
	{
		this.asset = asset;
	}
	public String getAmount() 
	{
		return amount;
	}
	public void setAmount(String amount) 
	{
		this.amount = amount;
	}
	public String getRenewMode() 
	{
		return renewMode;
	}
	public void setRenewMode(String renewMode) 
	{
		this.renewMode = renewMode;
	}
	public String getCurrencyCode() 
	{
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) 
	{
		this.currencyCode = currencyCode;
	}
	public String getRenewAmount() 
	{
		return renewAmount;
	}
	public void setRenewAmount(String renewAmount) 
	{
		this.renewAmount = renewAmount;
	}
	public String getRenewCount() 
	{
		return renewCount;
	}
	public void setRenewCount(String renewCount) 
	{
		this.renewCount = renewCount;
	}
}
