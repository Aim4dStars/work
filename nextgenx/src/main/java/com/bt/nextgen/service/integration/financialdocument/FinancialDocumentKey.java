package com.bt.nextgen.service.integration.financialdocument;

import com.bt.nextgen.core.domain.key.StringIdKey;

public class FinancialDocumentKey extends StringIdKey
{
	private FinancialDocumentKey(String id)
	{
		super(id);
	}

	public static FinancialDocumentKey valueOf(String accountId)
	{
		if (accountId == null)
			return null;
		else
			return new FinancialDocumentKey(accountId);
	}
}
