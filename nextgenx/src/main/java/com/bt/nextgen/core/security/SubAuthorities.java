package com.bt.nextgen.core.security;


public enum SubAuthorities
{
	ASSIST("assist"), WITH_CASH("with_cash"), NOCASH("nocash");

	String name;

	public String getName()
	{
		return name;
	}

	SubAuthorities(String name)
	{
		this.name = name;
	}
}
