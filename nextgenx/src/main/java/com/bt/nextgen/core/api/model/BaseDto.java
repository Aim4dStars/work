package com.bt.nextgen.core.api.model;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class BaseDto implements Dto
{
	@Override
	public String getType()
	{
		String className = getClass().getSimpleName();
		return className.replaceAll("Dto$", "");
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
