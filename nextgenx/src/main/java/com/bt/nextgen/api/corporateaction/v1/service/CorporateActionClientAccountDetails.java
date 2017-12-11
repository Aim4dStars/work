package com.bt.nextgen.api.corporateaction.v1.service;

import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

import java.util.Map;


public class CorporateActionClientAccountDetails {
	private Map<AccountKey, WrapAccount> accountsMap;

	private Map<ClientKey, Client> clientsMap;

	private Map<AccountKey, AccountBalance> accountBalancesMap;

	public CorporateActionClientAccountDetails() {
		// Empty constructor
	}

	public CorporateActionClientAccountDetails(Map<AccountKey, WrapAccount> accountsMap, Map<ClientKey, Client> clientsMap,
											   Map<AccountKey, AccountBalance> accountBalancesMap) {
		this.accountsMap = accountsMap;
		this.clientsMap = clientsMap;
		this.accountBalancesMap = accountBalancesMap;
	}

	public Map<AccountKey, WrapAccount> getAccountsMap() {
		return accountsMap;
	}

	public Map<ClientKey, Client> getClientsMap() {
		return clientsMap;
	}

	public Map<AccountKey, AccountBalance> getAccountBalancesMap() {
		return accountBalancesMap;
	}
}
