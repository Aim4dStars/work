package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.account.UpdatePrimContactRequest;

import java.math.BigDecimal;

/**
 * @deprecated Use V2
 */
@Deprecated
public class UpdatePrimContactRequestImpl implements UpdatePrimContactRequest
{

	private AccountKey accountkey;
	private BigDecimal identifier;
	private ClientKey primaryContactPersonId;

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
	public ClientKey getPrimaryContactPersonId()
	{
		return primaryContactPersonId;
	}

	@Override
	public void setPrimaryContactPersonId(ClientKey primaryContactPersonId)
	{
		this.primaryContactPersonId = primaryContactPersonId;

	}

}
