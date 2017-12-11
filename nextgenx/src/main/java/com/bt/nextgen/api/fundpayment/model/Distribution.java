package com.bt.nextgen.api.fundpayment.model;

public class Distribution
{
	private String component;

	private String amount;

	public Distribution(String component, String amount)
	{
		this.component = component;
		this.amount = amount;
	}

	public String getComponent()
	{
		return component;
	}

	public String getAmount()
	{
		return amount;
	}

}
