package com.bt.nextgen.api.fundpayment.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum MitWhtDistributionComponentType
{
	TARP_DISCOUNT("TARP - Discounted CG"), TARP_NON_DISCOUNT("TARP - Other Non-Discountable CG"), OTHER_INCOME("Other Income");

	private final String component;
	private static final List <String> componentList = new ArrayList <String>();

	MitWhtDistributionComponentType(String component)
	{
		this.component = component;
	}

	static
	{
		for (MitWhtDistributionComponentType type : EnumSet.allOf(MitWhtDistributionComponentType.class))
			componentList.add(type.getComponent());
	}

	public static boolean isMitWhtType(String value)
	{
		return componentList.contains(value);
	}

	public String getComponent()
	{
		return component;
	}

}