/**
 * 
 */
package com.bt.nextgen.service.avaloq.account;

import java.math.BigDecimal;

import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.BankAccount;
import com.btfin.panorama.service.integration.account.Biller;
import com.bt.nextgen.service.integration.account.BillerRequest;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.account.DeleteLinkedAccRequest;
import com.bt.nextgen.service.integration.account.LinkedAccRequest;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.UpdatePaymentLimitRequest;
import com.bt.nextgen.service.integration.account.UpdatePrimContactRequest;
import com.bt.nextgen.service.integration.account.UpdateTaxPrefRequest;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionOrderType;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionType;

/**
 * @author L070589
 *
 */
public class UpdateBPDetailsTestImpl implements UpdateTaxPrefRequest, UpdatePrimContactRequest, LinkedAccRequest,
	DeleteLinkedAccRequest, BillerRequest, UpdatePaymentLimitRequest
{

	private AccountKey accountkey;

	private CGTLMethod taxPref;

	private ClientKey primaryContactPersonId;

	private BigDecimal identifier;

	private LinkedAccount linkedAccount;

	private BankAccount bankAccount;

	private Biller billerAccount;

	private TransactionType businessType;

	private TransactionOrderType businessOrderType;

	private CurrencyType currency;

	private BigDecimal amount;

	@Override
	public AccountKey getAccountKey()
	{
		// TODO Auto-generated method stub
		return accountkey;
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
	public CGTLMethod getCGTLMethod()
	{
		// TODO Auto-generated method stub
		return taxPref;
	}

	@Override
	public void setCGTLMethod(CGTLMethod taxPref)
	{
		this.taxPref = taxPref;

	}

	@Override
	public void setAccountKey(AccountKey accountkey)
	{
		this.accountkey = accountkey;

	}

	@Override
	public ClientKey getPrimaryContactPersonId()
	{
		// TODO Auto-generated method stub
		return primaryContactPersonId;
	}

	@Override
	public void setPrimaryContactPersonId(ClientKey primaryContactPersonId)
	{
		this.primaryContactPersonId = primaryContactPersonId;

	}

	@Override
	public LinkedAccount getLinkedAccount()
	{
		// TODO Auto-generated method stub
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

	@Override
	public Biller getBillerDetail()
	{
		return billerAccount;
	}

	@Override
	public void setBillerDetail(Biller billerDetail)
	{
		this.billerAccount = billerDetail;

	}

	@Override
	public void setBusinessTransactionType(TransactionType businessType)
	{
		this.businessType = businessType;

	}

	@Override
	public void setBusinessTransactionOrderType(TransactionOrderType businessOrderType)
	{
		this.businessOrderType = businessOrderType;

	}

	@Override
	public void setCurrency(CurrencyType currency)
	{
		this.currency = currency;

	}

	@Override
	public TransactionType getBusinessTransactionType()
	{

		return businessType;
	}

	@Override
	public TransactionOrderType getBusinessTransactionOrderType()
	{

		return businessOrderType;
	}

	@Override
	public CurrencyType getCurrency()
	{

		return currency;
	}

	@Override
	public BigDecimal getAmount()
	{

		return amount;
	}

	@Override
	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;

	}

}
