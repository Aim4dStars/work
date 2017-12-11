package com.bt.nextgen.service.integration.transaction;

import java.util.List;

import com.bt.nextgen.service.integration.transaction.Transaction;

public interface TransactionHolder
{
	public abstract List <Transaction> getScheduledTransactions();
}