package com.bt.nextgen.service.integration.cashcategorisation.model;

public enum CashCategorisationAction
{
	ADD("add"),
	REMOVE("remove");

	private String action = "";

	CashCategorisationAction(String action)
	{
		this.action = action;
	}

	public String toString()
	{
		return this.action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}