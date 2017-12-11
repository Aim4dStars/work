package com.bt.nextgen.service.integration.payeedetails;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.service.avaloq.payeedetails.LinkedCashAccount;
import com.bt.nextgen.service.integration.MoneyAccountIdentifier;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.account.VersionedObjectIdentifier;

public interface PayeeDetails
{
	public VersionedObjectIdentifier getModifierSequenceNumber(); 
	
	public String getMaxDailyLimit();

	public CashAccountDetails getCashAccount();
	
	public List <PayAnyOne> getPayanyonePayeeList();

	public List <Biller> getBpayBillerPayeeList();

	public List <LinkedAccount> getLinkedAccountList();

	public MoneyAccountIdentifier getMoneyAccountIdentifier();

	public List <PayeeLimit> getPayeeLimits();

	public List <PayeeAuthority> getPayeeAuthorityList();
	
	public List <LinkedCashAccount> getLinkedCashAccounts();
	
	public BigDecimal getModifierSeqNumber();

}
