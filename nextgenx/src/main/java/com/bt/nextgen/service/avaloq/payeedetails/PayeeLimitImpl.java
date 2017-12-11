package com.bt.nextgen.service.avaloq.payeedetails;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;

@ServiceBean(xpath="bp_limit")
public class PayeeLimitImpl implements PayeeLimit
{
	@ServiceElement(xpath="bp_limit_meta_typ_id/val", staticCodeCategory="META_TYPE")
	private TransactionType metaType;
	
	@ServiceElement(xpath="bp_limit_ot_id/val", staticCodeCategory="ORDER_TYPE")
	private TransactionOrderType orderType;
	
	@ServiceElement(xpath="bp_limit_curry_id/val", staticCodeCategory="CURRENCY_TYPE")
	private String currency;
	
	@ServiceElement(xpath="bp_limit_amount/val")
	private String limitAmount;
	
	@ServiceElement(xpath="bp_limit_remn_amount/val")
	private String remainingLimit;

	@Override
	public TransactionType getMetaType()
	{
		return metaType;
	}

	public void setMetaType(TransactionType metaType)
	{
		this.metaType = metaType;
	}

	@Override
	public TransactionOrderType getOrderType()
	{
		return orderType;
	}

	public void setOrderType(TransactionOrderType orderType)
	{
		this.orderType = orderType;
	}

	@Override
	public String getCurrency()
	{
		return currency;
	}

	public void setCurrency(String currency)
	{
		this.currency = currency;
	}

	@Override
	public String getLimitAmount()
	{
		return limitAmount;
	}

	public void setLimitAmount(String limitAmount)
	{
		this.limitAmount = limitAmount;
	}

	@Override
	public String getRemainingLimit() {
		return remainingLimit;
	}

	public void setRemainingLimit(String remainingLimit) {
		this.remainingLimit = remainingLimit;
	}
}
