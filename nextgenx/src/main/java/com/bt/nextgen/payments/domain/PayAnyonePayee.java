package com.bt.nextgen.payments.domain;

public class PayAnyonePayee extends Payee{
	private String name;
	private Bsb bsb;
	private String accountNumber;
	private String description;

	public PayAnyonePayee(String cashAccountId, String nickname, String name, Bsb bsb, String accountNumber)
	{
		super(cashAccountId, nickname, PayeeType.PAY_ANYONE);
		this.name = name;
		this.bsb = bsb;
		this.accountNumber = accountNumber;
	}

	public PayAnyonePayee()
	{
		super(PayeeType.PAY_ANYONE);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Bsb getBsb()
	{
		return bsb;
	}

	public void setBsb(Bsb bsb)
	{
		this.bsb = bsb;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}
