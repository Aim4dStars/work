package com.bt.nextgen.service.bassil;

import java.util.List;

import com.bt.nextgen.portfolio.web.model.StatementTypeErrorModel;

/**
 * This service returns the basil document based for the client account. It return all the documents available in basil server.
 * @see <a href="http://sharepoint.btfin.com/it/applicationservices/ICC/UDDI/BTFin/Forms/AllItems.aspx?RootFolder=%2fit%2fapplicationservices%2fICC%2fUDDI%2fBTFin%2fSharedServices%2fBPM%2fImage%2fImageService%2fV1&FolderCTID=&View=%7b2CA07412-8C6D-4A88-ADCA-B4718A69B6E3%7d">Bassil service document</a>
 * @deprecated Statements service replaced by FinancialDocumentService
 */
//TODO remove this package once revenue statements have been cut over to the new CMIS system.
@Deprecated
public interface StatementService
{
	/**
	 * Loads the client documents from the basil service.
	 * @param accountId
	 * @param error wraps any error while calling the service or services itself has return any error code in response.
	 * @return	 List of client statements  
	 * @throws Exception
	 */
	public List <ClientStatements> loadClientStatements(String accountId, StatementTypeErrorModel error) throws Exception;

	//byte[] loadPdfStatements(String documentId) throws Exception;
}
