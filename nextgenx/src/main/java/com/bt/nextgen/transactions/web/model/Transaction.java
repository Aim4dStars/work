package com.bt.nextgen.transactions.web.model;

import java.util.List;
import java.util.Map;


public class Transaction
{
	private Map<String, List<TransactionInterface>> scheduledTransactionModels;
	private Map<String, List<TransactionInterface>> pastTransactionMap;
	private List<TransactionInterface> pastTransactionList;
	private int totalPastTransactionCount = 0;
	
	private boolean hasMorePastTransaction;

	public Map<String, List<TransactionInterface>> getScheduledTransactionModels() {
		return scheduledTransactionModels;
	}

	public void setScheduledTransactionModels(
			Map<String, List<TransactionInterface>> scheduledTransactionModels) {
		this.scheduledTransactionModels = scheduledTransactionModels;
	}

	public Map<String, List<TransactionInterface>> getPastTransactionMap() {
		return pastTransactionMap;
	}

	public void setPastTransactionMap(
			Map<String, List<TransactionInterface>> pastTransactionMap) {
		this.pastTransactionMap = pastTransactionMap;
	}

	public List<TransactionInterface> getPastTransactionList() {
		return pastTransactionList;
	}

	public void setPastTransactionList(
			List<TransactionInterface> pastTransactionList) {
		this.pastTransactionList = pastTransactionList;
	}

	public boolean isHasMorePastTransaction() {
		return hasMorePastTransaction;
	}

	public void setHasMorePastTransaction(boolean hasMorePastTransaction) {
		this.hasMorePastTransaction = hasMorePastTransaction;
	}
	
	public void setTotalPastTransactionCount(int totalPastTransactionCount) {
		this.totalPastTransactionCount = totalPastTransactionCount;
	}
	
	public int getTotalPastTransactionCount() {
		return this.totalPastTransactionCount;
	}	
}
