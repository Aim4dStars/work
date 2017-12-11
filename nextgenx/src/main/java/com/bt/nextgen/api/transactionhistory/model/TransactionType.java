package com.bt.nextgen.api.transactionhistory.model;

public enum TransactionType
{
	BUY("Buy"), SELL("Sell"), DEPOSIT("Deposit"), PAYMENT("Payment");

	private String code;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	TransactionType(String code)
	{
		this.code = code;
	}

	public static TransactionType forCode(String code)
	{
		for (TransactionType transactionType : TransactionType.values())
		{
			if (transactionType.code.equals(code))
			{
				return transactionType;
			}
		}
		return null;
	}
}