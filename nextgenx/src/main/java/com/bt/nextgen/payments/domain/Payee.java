package com.bt.nextgen.payments.domain;

public class Payee {
	private long id;
	private String cashAccountId;
	private String nickname;
	private PayeeType payeeType;

	public Payee(String cashAccountId, String nickname, PayeeType payeeType)
	{
		this();
		this.cashAccountId = cashAccountId;
		this.nickname = nickname;
		this.payeeType = payeeType;
	}

	public Payee(PayeeType payeeType)
	{
		this();
		this.payeeType = payeeType;
	}

	public Payee()
	{
		super();
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getCashAccountId()
	{
		return cashAccountId;
	}

	public void setCashAccountId(String cashAccountId)
	{
		this.cashAccountId = cashAccountId;
	}

	public String getNickname()
	{
		return nickname;
	}

	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}

	public PayeeType getPayeeType()
	{
		return payeeType;
	}

	public void setPayeeType(PayeeType payeeType)
	{
		this.payeeType = payeeType;
	}
}
