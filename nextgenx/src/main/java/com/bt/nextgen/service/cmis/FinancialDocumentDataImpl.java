package com.bt.nextgen.service.cmis;

import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentKey;

public class FinancialDocumentDataImpl implements FinancialDocumentData
{
	private byte[] data;
	private FinancialDocumentKey documentKey;

	@Override
	public FinancialDocumentKey getDocumentKey()
	{
		return documentKey;
	}

	public void setDocumentKey(FinancialDocumentKey documentKey)
	{
		this.documentKey = documentKey;
	}

	@Override
	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}
}
