package com.bt.nextgen.payments.domain;

import com.btfin.panorama.core.security.avaloq.Constants;

public enum InterestFrequency
{
	MONTHLY("monthly", Constants.TD_FREQUENCY_MONTHLY), YEARLY("yearly", Constants.TD_FREQUENCY_YEARLY), MATURITY(
		"at maturity",
		Constants.TD_FREQUENCY_MATURITY);

	InterestFrequency(String name, String avaloqKey)
	{
		this.name = name;
		this.avaloqKey = avaloqKey;
	}

	private String name;
	public final String avaloqKey;

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAvaloqKey()
	{
		return avaloqKey;
	}

	public static InterestFrequency forAvaloqKey(String avaloqKey)
	{
		for (InterestFrequency frequency : InterestFrequency.values())
		{
			if (frequency.getAvaloqKey().equals(avaloqKey))
			{
				return frequency;
			}
		}
		return null;
	}

}
