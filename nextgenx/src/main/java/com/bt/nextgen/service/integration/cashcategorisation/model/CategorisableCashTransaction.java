package com.bt.nextgen.service.integration.cashcategorisation.model;

import java.util.List;

import com.bt.nextgen.api.account.v2.model.AccountKey;

/**
 * Interface for Cash transactions that are contributions.
 */
public interface CategorisableCashTransaction
{
	AccountKey getAccountKey();

	void setAccountKey(AccountKey accountKey);

	String getDocId();

	void setDocId(String docId);

	List <Contribution> getContributionSplit();

	void setContributionSplit(List <Contribution> contributionList);

	String getStatus();

	void setStatus(String status);

	String getTransactionCategory();

	void setTransactionCategory(String transactionCategory);
}