package com.bt.nextgen.core.web.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class BaseObject implements Serializable
{
	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
