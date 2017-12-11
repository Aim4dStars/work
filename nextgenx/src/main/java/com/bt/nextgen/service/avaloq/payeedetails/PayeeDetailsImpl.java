package com.bt.nextgen.service.avaloq.payeedetails;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.avaloq.MoneyAccountIdentifierImpl;
import com.btfin.panorama.service.integration.account.Biller;
import com.btfin.panorama.service.integration.account.LinkedAccount;
import com.bt.nextgen.service.integration.account.PayAnyOne;
import com.bt.nextgen.service.integration.account.VersionedObjectIdentifier;
import com.bt.nextgen.service.integration.payeedetails.CashAccountDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeAuthority;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;

@ServiceBean(xpath="/")
public class PayeeDetailsImpl extends AvaloqBaseResponseImpl implements PayeeDetails
{
	@ServiceElement(xpath="//data/bp_list/bp/bp_head_list/bp_head/max_daily_limit/val")
	private String maxDailyLimit;

	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head", type=CashAccountDetailsImpl.class)
	private List<CashAccountDetailsImpl> cashAccount;

	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head/reg_payee_list/reg_payee", type=PayAnyOneImpl.class)
	private List <PayAnyOne> payanyonePayeeList;

	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head/reg_biller_list/reg_biller", type=BillerPayeeImpl.class)
	private List <Biller> bpayBillerPayeeList;

	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head/linked_acc_list/linked_acc", type=LinkedAccountPayeeImpl.class)
	private List <LinkedAccount> linkedAccountList;
	
	private MoneyAccountIdentifierImpl moneyAccountIdentifier;

	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head/bp_limit_list/bp_limit", type=PayeeLimitImpl.class)
	private List <PayeeLimit> payeeLimits;

	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head/bp_auth_list/bp_auth", type=PayeeAuthorityImpl.class)
	private List <PayeeAuthority> payeeAuthorityList;
	
	@ServiceElementList(xpath="//data/bp_list/bp/bp_head_list/bp_head/cont_list/cont", type=LinkedCashAccount.class)
	private List <LinkedCashAccount> linkedCashAccount;
	
	@ServiceElement(xpath="//data/bp_list/bp/bp_head_list/bp_head/bp_modi_seq_nr/val", converter=BigDecimalConverter.class)
	private BigDecimal modifierSeqNumber; 

	private VersionedObjectIdentifier versionedIdentifier; 

	@Override
	public String getMaxDailyLimit()
	{
		return maxDailyLimit;
	}

	public void setMaxDailyLimit(String maxDailyLimit)
	{
		this.maxDailyLimit = maxDailyLimit;
	}

	@Override
	public CashAccountDetails getCashAccount()
	{
		if(null != cashAccount) {
			return cashAccount.get(0);
		}else{
			return null;
		}
	}

	public void setCashAccount(List<CashAccountDetailsImpl> cashAccount)
	{
		this.cashAccount = cashAccount;
	}

	@Override
	public List <PayAnyOne> getPayanyonePayeeList()
	{
		return payanyonePayeeList;
	}

	public void setPayanyonePayeeList(List <PayAnyOne> payanyonePayeeList)
	{
		this.payanyonePayeeList = payanyonePayeeList;
	}

	@Override
	public List <Biller> getBpayBillerPayeeList()
	{
		return bpayBillerPayeeList;
	}

	public void setBpayBillerPayeeList(List <Biller> bpayBillerPayeeList)
	{
		this.bpayBillerPayeeList = bpayBillerPayeeList;
	}

	@Override
	public List <LinkedAccount> getLinkedAccountList()
	{
		return linkedAccountList;
	}

	public void setLinkedAccountList(List <LinkedAccount> linkedAccountList)
	{
		this.linkedAccountList = linkedAccountList;
	}

	@Override
	public MoneyAccountIdentifierImpl getMoneyAccountIdentifier()
	{
		return moneyAccountIdentifier;
	}

	public void setMoneyAccountIdentifier(MoneyAccountIdentifierImpl moneyAccountIdentifier)
	{
		this.moneyAccountIdentifier = moneyAccountIdentifier;
	}

	@Override
	public List <PayeeLimit> getPayeeLimits()
	{
		return payeeLimits;
	}

	public void setPayeeLimits(List <PayeeLimit> payeeLimits)
	{
		this.payeeLimits = payeeLimits;
	}

	@Override
	public List <PayeeAuthority> getPayeeAuthorityList()
	{
		return payeeAuthorityList;
	}

	public void setPayeeAuthorityList(List <PayeeAuthority> payeeAuthorityList)
	{
		this.payeeAuthorityList = payeeAuthorityList;
	}

	@Override
	public List <LinkedCashAccount> getLinkedCashAccounts()
	{
		return linkedCashAccount;
	}

	@Override
	public VersionedObjectIdentifier getModifierSequenceNumber()
	{
		return versionedIdentifier;
	}

	public void setModifierSequenceNumber(VersionedObjectIdentifier versionedIdentifier)
	{
		this.versionedIdentifier = versionedIdentifier;
	}

	public BigDecimal getModifierSeqNumber()
	{
		return modifierSeqNumber;
	}

	public void setModifierSeqNumber(BigDecimal modifierSeqNumber)
	{
		this.modifierSeqNumber = modifierSeqNumber;
	}
}
