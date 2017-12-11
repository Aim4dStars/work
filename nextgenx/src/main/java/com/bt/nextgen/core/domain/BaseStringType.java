package com.bt.nextgen.core.domain;

import java.io.Serializable;

public abstract class BaseStringType extends BaseType<String> implements Serializable
{
	public BaseStringType(String value)
	{
		super(value);
	}

	public boolean startsWith(BaseStringType startsWithMe)
	{
		return getValue().startsWith(startsWithMe.getValue());
	}

}
