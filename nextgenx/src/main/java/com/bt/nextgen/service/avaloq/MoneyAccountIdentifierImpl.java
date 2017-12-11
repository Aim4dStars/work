package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.integration.MoneyAccountIdentifier;

public class MoneyAccountIdentifierImpl implements MoneyAccountIdentifier
{
	private String moneyAccountId;

	@Override
	public String getMoneyAccountId()
	{
		// TODO Auto-generated method stub
		return moneyAccountId;
	}

	@Override
	public void setMoneyAccountId(String moneyAccountId)
	{
		this.moneyAccountId = moneyAccountId;

	}

}
