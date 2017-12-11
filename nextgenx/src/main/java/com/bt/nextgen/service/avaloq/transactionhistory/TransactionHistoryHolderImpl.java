package com.bt.nextgen.service.avaloq.transactionhistory;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistory;
import com.bt.nextgen.service.integration.transactionhistory.TransactionHistoryHolder;

@ServiceBean(xpath = "/")
public class TransactionHistoryHolderImpl extends AvaloqBaseResponseImpl implements TransactionHistoryHolder
{
	@ServiceElementList(xpath = "//data/bp_list/bp/cont_list/cont/pos_list/pos/evt_list/evt", type = TransactionHistoryImpl.class)
	private List <TransactionHistory> transactions;

	@Override
	public List <TransactionHistory> getTransactions()
	{
		return transactions;
	}
}
