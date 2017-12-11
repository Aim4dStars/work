package com.bt.nextgen.api.transaction.model;


import com.bt.nextgen.core.domain.key.StringIdKey;

public class TransactionKey extends StringIdKey {
	private String accountId;
	private String transactionId;

	public TransactionKey(String transactionId) {
		this.transactionId = transactionId;
	}

	public TransactionKey(String accountId, String transactionId) {
		super(transactionId);
		this.accountId = accountId;
		this.transactionId = transactionId;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getTransactionId() {
		return transactionId;
	}
}
