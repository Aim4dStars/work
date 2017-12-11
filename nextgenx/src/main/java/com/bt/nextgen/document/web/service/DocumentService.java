package com.bt.nextgen.document.web.service;

import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;

public interface DocumentService
{

	FinancialDocumentData loadDocument(String statementId, FinancialDocumentType financialDocumentType);

}
