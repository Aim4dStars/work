package com.bt.nextgen.api.accountassociates.service;

import com.bt.nextgen.api.accountassociates.model.AccountAssociateDto;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentComplete;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
class AccountAssociatesDtoServiceImpl implements AccountAssociatesDtoService {

    private static final Logger logger = LoggerFactory.getLogger(AccountAssociatesDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Override
    public List<AccountAssociateDto> findAll(ServiceErrors serviceErrors) {
        logger.debug("Getting all the Accounts");
        List<AccountAssociateDto> resultSet = new ArrayList<>();
        Concurrent.when(loadAccountMap(serviceErrors), loadAllClients(serviceErrors)).done(processResults(resultSet)).execute();
        return resultSet;
    }

    private ConcurrentComplete processResults(final List<AccountAssociateDto> resultSet) {
        return new AbstractConcurrentComplete() {

            @Override
            public void run() {
                List<? extends ConcurrentResult<?>> r = this.getResults();
                Map<AccountKey, WrapAccount> accountMap = (Map<AccountKey, WrapAccount>) r.get(0).getResult();
                Map<ClientKey, Client> allClients = (Map<ClientKey, Client>) r.get(1).getResult();
                populateResultSet(resultSet, accountMap, allClients);
            }
        };
    }

    private ConcurrentCallable<?> loadAllClients(final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Map<ClientKey, Client>>() {

            @Override
            public Map<ClientKey, Client> call() {
                return clientIntegrationService.loadClientMap(serviceErrors);
            }
        };
    }

    private ConcurrentCallable<?> loadAccountMap(final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Map<AccountKey, WrapAccount>>() {

            @Override
            public Map<AccountKey, WrapAccount> call() {
                return accountService.loadWrapAccountWithoutContainers(serviceErrors);

            }
        };
    }

    private void populateResultSet(List<AccountAssociateDto> resultSet, Map<AccountKey, WrapAccount> accountMap,
            Map<ClientKey, Client> allClients) {
        for (Map.Entry<AccountKey, WrapAccount> wrapAccountEntry : accountMap.entrySet()) {
            WrapAccount account = wrapAccountEntry.getValue();
            if (account != null && AccountStatus.ACTIVE.equals(account.getAccountStatus())) {

                /* Get Approvers */
                Collection<ClientKey> accountApprovers = account.getApprovers();
                if (null != accountApprovers) {
                    for (ClientKey clientKey : accountApprovers) {
                        Client client = allClients.get(clientKey);
                        if (null != client) {
                            /* Always approvers are NPs */
                            if (ClientType.N.equals(client.getClientType())) {
                                setAccountAssociateDTO(resultSet, client.getFullName(), account.getAccountName(),
                                        ConsistentEncodedString.fromPlainText(account.getAccountKey().getId()).toString(),
                                        ConsistentEncodedString.fromPlainText(client.getClientKey().getId()).toString(),
                                        Boolean.TRUE);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setAccountAssociateDTO(List<AccountAssociateDto> resultSet, String fullName, String accountName,
            String encryptedAccountKey, String clientKey, Boolean aTrue) {
        AccountAssociateDto dto = new AccountAssociateDto();
        dto.setClientName(fullName);
        dto.setAccountName(accountName);
        dto.setEncryptedAccountKey(encryptedAccountKey);
        dto.setEncryptedClientKey(clientKey);
        dto.setOwner(aTrue);
        resultSet.add(dto);
    }
}
