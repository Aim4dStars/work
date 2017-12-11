package com.bt.nextgen.clients.service;

import java.util.List;
import java.util.Map;

import com.bt.nextgen.clients.web.model.ClientInterface;
import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.core.web.model.SearchParameters;
import com.bt.nextgen.portfolio.web.model.AccountModel;

public interface ClientService
{

	List <ClientModel> loadAllClients() throws Exception;

	ClientModel loadClient(String clientId) throws Exception;

	List <ClientModel> advanceClientSearch(SearchParameters searchParameters) throws Exception;

	List <AccountModel> searchClientAccounts(String searchCriteria) throws Exception;

	List <AccountModel> searchClientAccounts(SearchParameters searchParameters) throws Exception;

	String getClientListCsv(SearchParameters searchParameters) throws Exception;

	String getTermDepositTransactionsCsv(String clientId, String portfolioId, Map <String, String[]> searchParametersMap)
		throws Exception;

	List <ClientInterface> getClientList(String clientId);

	List <PersonInterface> getPersonDetail(List <String> clientId);

}
