package com.bt.nextgen.core.security;


public enum AvaloqSubRoles
{
	ASSIST("assist"), ASSIST_CASH("assist_cash"), ASSIST_NOCASH("assist_nocash"), PRPLNR("prplnr"), PRPLNR_CASH("prplnr_cash"), PRPLNR_NOCASH("PRPLNR_NOCASH");

	String name;

	public String getName()
	{
		return name;
	}

	AvaloqSubRoles(String name)
	{
		this.name = name;
	}
}
