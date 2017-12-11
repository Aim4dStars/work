package com.bt.nextgen.service.avaloq;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.NumberedAccount;

public class NumberedAccountImpl implements NumberedAccount
{

	private AccountStructureType accountStructureType;
	private AccountType accountType;
	private String accountIdentifier;
	private String accountNumber;
	private AccountKey accountKey;
	private BigDecimal modificationIdentifier;

	public BigDecimal getModificationIdentifier()
	{
		return modificationIdentifier;
	}

	public void setModificationIdentifier(BigDecimal modificationIdentifier)
	{
		this.modificationIdentifier = modificationIdentifier;
	}

	public AccountStructureType getAccountStructureType()
	{
		return accountStructureType;
	}

	public void setAccountStructureType(AccountStructureType accountStructureType)
	{
		this.accountStructureType = accountStructureType;
	}

	public AccountType getAccountType()
	{
		return accountType;
	}

	public void setAccountType(AccountType accountType)
	{
		this.accountType = accountType;
	}

	public String getAccountIdentifier()
	{
		return accountIdentifier;
	}

	public void setAccountIdentifier(String accountIdentifier)
	{
		this.accountIdentifier = accountIdentifier;
	}

	public String getAccountNumber()
	{
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public AccountKey getAccountKey()
	{
		return accountKey;
	}

	public void setAccountKey(AccountKey accountKey)
	{
		this.accountKey = accountKey;
	}

}
