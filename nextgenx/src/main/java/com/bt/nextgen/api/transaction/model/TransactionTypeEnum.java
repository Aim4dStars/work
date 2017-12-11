package com.bt.nextgen.api.transaction.model;

public enum TransactionTypeEnum
{

	DEBIT, CREDIT;
	
	public static TransactionTypeEnum fromValue(String value)
	{
		for (TransactionTypeEnum txnType : TransactionTypeEnum.values())
		{
			if (txnType.name().equalsIgnoreCase(value))
			{
				return txnType;
			}
		}
		throw new IllegalArgumentException(value);
	}
	

}