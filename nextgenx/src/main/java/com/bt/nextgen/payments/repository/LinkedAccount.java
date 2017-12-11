package com.bt.nextgen.payments.repository;

import com.bt.nextgen.payments.domain.PayeeType;

import javax.persistence.*;


@Entity
@Table(name = "LINKED_ACCOUNT")
@PrimaryKeyJoinColumn(name = "PAYEE_SEQ")
public class LinkedAccount extends Payee
{
	
	@Column(name = "ACCOUNT_NAME")
	private String accountName;

	@ManyToOne
	@JoinColumn(name = "BSB_CODE")
	private Bsb bsb;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	public LinkedAccount()
	{}

	public LinkedAccount(String cashAccountId, String nickname, String accountName, Bsb bsb, String accountNumber, PayeeType type)
	{
		super(cashAccountId, nickname, type);
		this.accountName = accountName;
		this.bsb = bsb;
		this.accountNumber = accountNumber;
		
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Bsb getBsb() {
		return bsb;
	}

	public void setBsb(Bsb bsb) {
		this.bsb = bsb;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReferenceCode() {
		// TODO Auto-generated method stub
		return null;
	}

	
}

