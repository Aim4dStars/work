package com.bt.nextgen.web.model.cash;

public enum InterestPaidType
{
	MONTHLY("Monthly"), ANNUALLY("Annually");

	InterestPaidType(String name)
	{
		this.name = name;
	}

	String name;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
