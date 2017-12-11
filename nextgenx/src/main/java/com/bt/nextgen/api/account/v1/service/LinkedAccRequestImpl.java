package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.btfin.panorama.service.integration.account.LinkedAccount;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class LinkedAccRequestImpl implements LinkedAccRequest, DeleteLinkedAccRequest
{
	private AccountKey accountkey;
	private BigDecimal identifier;
	private LinkedAccount linkedAccount;
	private BankAccount bankAccount;

	@Override
	public AccountKey getAccountKey()
	{
		// TODO Auto-generated method stub
		return accountkey;

	}

	@Override
	public void setAccountKey(AccountKey accountkey)
	{
		this.accountkey = accountkey;

	}

	@Override
	public AccountType getAccountType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getModificationIdentifier()
	{
		// TODO Auto-generated method stub
		return identifier;
	}

	@Override
	public void setModificationIdentifier(BigDecimal identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public LinkedAccount getLinkedAccount()
	{
		return linkedAccount;
	}

	@Override
	public void setLinkedAccount(LinkedAccount linkedAccount)
	{
		this.linkedAccount = linkedAccount;

	}

	@Override
	public BankAccount getBankAccount()
	{
		return bankAccount;
	}

	@Override
	public void setBankAccount(BankAccount bankAccount)
	{
		this.bankAccount = bankAccount;

	}

}