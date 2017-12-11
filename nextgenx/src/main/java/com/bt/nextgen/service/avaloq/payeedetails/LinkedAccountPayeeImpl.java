package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.CurrencyType;
import com.btfin.panorama.service.integration.account.LinkedAccount;

import java.math.BigDecimal;

@ServiceBean(xpath="linked_acc|reg_acc")
public class LinkedAccountPayeeImpl implements LinkedAccount
{
	@ServiceElement(xpath="linked_acc_is_pri/val|reg_acc_is_pri/val")
	private boolean primary;

	@ServiceElement(xpath="linked_acc_curry_id/val",staticCodeCategory = "CURRENCY_TYPE")
	private CurrencyType currency;
	
	@ServiceElement(xpath="linked_acc_curry_id/val|reg_acc_curry_id/val")
	private String currencyId;

	@ServiceElement(xpath="linked_acc_limit/val", converter=BigDecimalConverter.class)
	private BigDecimal limit;

	@ServiceElement(xpath="linked_acc_bsb/val|reg_acc_bsb/val")
	private String bsb;

	@ServiceElement(xpath="linked_acc_nr/val|reg_acc_nr/val")
	private String accountNumber;

	@ServiceElement(xpath="linked_acc_name/val|reg_acc_name/val")
	private String name;

	@ServiceElement(xpath="linked_acc_nick_name/val|reg_acc_nick_name/val")
	private String nickName;
	
	@ServiceElement(xpath="linked_acc_remn_limit/val")
	private BigDecimal remainingLimit;

	@ServiceElement(xpath = "linked_acc_is_pens_pay/val")
	private boolean pensionPayment;

	@ServiceElement(xpath = "linked_acc_status_id/val")
	private String linkedAccountStatus;



	@Override
	public String getAccountNumber()
	{
		return accountNumber;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getNickName()
	{
		return nickName;
	}

	@Override
	public String getBsb()
	{
		return bsb;
	}

	@Override
	public boolean isPrimary()
	{
		return primary;
	}

	@Override
	public String getCurrencyId()
	{
		return currencyId;
	}

	@Override
	public BigDecimal getLimit()
	{
		return limit;
	}

	@Override
	public CurrencyType getCurrency()
	{
		return currency;
	}

	public void setPrimary(boolean primary)
	{
		this.primary = primary;
	}

	public void setCurrency(CurrencyType currency)
	{
		this.currency = currency;
	}

	public void setCurrencyId(String currencyId)
	{
		this.currencyId = currencyId;
	}

	public void setLimit(BigDecimal limit)
	{
		this.limit = limit;
	}

	public void setBsb(String bsb)
	{
		this.bsb = bsb;
	}

	public void setAccountNumber(String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	@Override
	public BigDecimal getRemainingLimit() {
		return remainingLimit;
	}

	public void setRemainingLimit(BigDecimal remainingLimit) {
		this.remainingLimit = remainingLimit;
	}

	@Override
	public boolean isPensionPayment() {
		return pensionPayment;
	}

	public void setPensionPayment(boolean pensionPayment) {
		this.pensionPayment = pensionPayment;
	}

    @Override
	public String getLinkedAccountStatus() { return linkedAccountStatus; }

	public void setLinkedAccountStatus(String linkedAccountStatus) { this.linkedAccountStatus = linkedAccountStatus; }
}
