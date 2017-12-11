package com.bt.nextgen.payments.repository;

import com.bt.nextgen.payments.domain.PayeeType;

import javax.persistence.*;

@Entity
@Table(name = "PAY_ANYONE_PAYEE")
@PrimaryKeyJoinColumn(name = "PAYEE_SEQ")
public class PayAnyonePayee extends Payee
{
	@Column(name = "NAME")
	private String name;

	@ManyToOne @JoinColumn(name = "BSB_CODE")
	private Bsb bsb;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@Transient
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

	@Override
	public String getCode()
	{
		return getBsb().getBsbCode();
	}

	@Override
	public String getReferenceCode()
	{
		return getAccountNumber();
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
