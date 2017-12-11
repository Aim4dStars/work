package com.bt.nextgen.service.integration.payeedetails;

import com.bt.nextgen.service.avaloq.pasttransaction.TransactionOrderType;
import com.bt.nextgen.service.avaloq.pasttransaction.TransactionType;

public interface PayeeLimit
{
	public abstract TransactionType getMetaType();

	public abstract TransactionOrderType getOrderType();

	public abstract String getCurrency();

	public abstract String getLimitAmount();
	
	public abstract String getRemainingLimit();

}
