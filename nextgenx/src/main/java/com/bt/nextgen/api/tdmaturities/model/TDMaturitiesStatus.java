package com.bt.nextgen.api.tdmaturities.model;

public enum TDMaturitiesStatus
{
	WITHDRAWN("Withdrawn"), MATURED("Matured"), OPEN("Open"), UNKNOWN("Unknown");

	String status;

	public String getStatus()
	{
		return status;
	}

	TDMaturitiesStatus(String status)
	{
		this.status = status;
	}
}
