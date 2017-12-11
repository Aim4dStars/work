/**
 * 
 */
package com.bt.nextgen.service.avaloq.account;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.account.PayeeRequest;

/**
 * @author L070589
 *
 */
public class UpdatePayeeDetailsTestImpl implements PayeeRequest
{

	private AccountKey accountkey;

	private BigDecimal identifier;

	private PayAnyOne bankAccount;

	@Override
	public AccountKey getAccountKey()
	{

		return accountkey;
	}

	@Override
	public AccountType getAccountType()
	{

		return null;
	}

	@Override
	public BigDecimal getModificationIdentifier()
	{
		return identifier;
	}

	@Override
	public void setModificationIdentifier(BigDecimal identifier)
	{
		this.identifier = identifier;
	}

	@Override
	public void setAccountKey(AccountKey accountkey)
	{
		this.accountkey = accountkey;

	}

	@Override
	public PayAnyOne getBankAccount()
	{
		return bankAccount;
	}

	@Override
	public void setBankAccount(PayAnyOne bankAccount)
	{
		this.bankAccount = bankAccount;

	}

}
