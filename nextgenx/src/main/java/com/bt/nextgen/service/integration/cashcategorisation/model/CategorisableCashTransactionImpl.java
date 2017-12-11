package com.bt.nextgen.service.integration.cashcategorisation.model;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;

/**
 * Represents an SMSF Cash deposit - Contribution.<p>
 * Each contribution can be split and categorised amongst one or more smsf members.
 */
@ServiceBean(xpath = "/")
public class CategorisableCashTransactionImpl implements CategorisableCashTransaction
{
	private static final String XML_HEADER = "//data/top/dt_list/dt/dt_head_list/dt_head";

	private AccountKey accountKey;

	private String docId;

	private String transactionCategory;

	@ServiceElementList(xpath = XML_HEADER, type = MemberContributionImpl.class)
	private List <Contribution> contributionList = new ArrayList <>();

	
	private String status;

	@Override
	public String getDocId()
	{
		return docId;
	}

	@Override
	public List <Contribution> getContributionSplit()
	{
		return contributionList;
	}

	@Override
	public void setDocId(String docId)
	{
		this.docId = docId;
	}

	@Override
	public void setContributionSplit(List <Contribution> contributionList)
	{
		this.contributionList = contributionList;
	}

	@Override
	public AccountKey getAccountKey()
	{
		return accountKey;
	}

	@Override
	public void setAccountKey(AccountKey accountKey)
	{
		this.accountKey = accountKey;
	}

	@Override
	public String getStatus()
	{
		return status;
	}

	@Override
	public void setStatus(String status)
	{
		this.status = status;
	}

	@Override
	public String getTransactionCategory()
	{
		return transactionCategory;
	}

	@Override
	public void setTransactionCategory(String transactionCategory)
	{
		this.transactionCategory = transactionCategory;
	}
}