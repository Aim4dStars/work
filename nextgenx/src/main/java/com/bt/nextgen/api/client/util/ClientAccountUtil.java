package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.core.security.integration.domain.Individual;
import com.btfin.panorama.core.security.integration.domain.Investor;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientAccountUtil {

    private Map<ClientKey, Client> clientMap;
    private Map<AccountKey, WrapAccount> accountMap;

    public ClientAccountUtil(Map<ClientKey, Client> clientMap) {
        this.clientMap = clientMap;
    }

    public ClientAccountUtil(Map<ClientKey, Client> clientMap, Map<AccountKey, WrapAccount> accountMap) {
        this.clientMap = clientMap;
        this.accountMap = accountMap;
    }

    /**
     * Retrieves the owner key(Parent client key) set of the associated persons related accounts
     *
     * @param clientKey
     *
     * @return Set <ClientKey>
     */
    private Set<ClientKey> getLinkedClients(ClientKey clientKey) {
        Set<ClientKey> linkedClients = new HashSet<>();
        linkedClients.add(clientKey);
        Set<ClientKey> associatedPersonsRelatedAccountsOwner = getAssociatedPersonsRelatedAccountsOwner(clientKey);
        if (CollectionUtils.isNotEmpty(associatedPersonsRelatedAccountsOwner)) {
            linkedClients.addAll(associatedPersonsRelatedAccountsOwner);
        }

        return linkedClients;
    }

    /**
     * Retrieve the Owner person key of all the related accounts of an associated person
     *
     * @return Set <ClientKey>
     */
    public Set<ClientKey> getAssociatedPersonsRelatedAccountsOwner(ClientKey associatedClient) {
        Set<ClientKey> linkedClients = new HashSet<>();
        for (Client existingClient : clientMap.values()) {
            final List<ClientKey> assoPersonsForOtherClient = ((Investor) existingClient).getAssociatedPersonKeys();
            if (CollectionUtils.isNotEmpty(assoPersonsForOtherClient) && assoPersonsForOtherClient.contains(associatedClient)) {
                linkedClients.add(existingClient.getClientKey());
            }
        }
        return linkedClients;
    }

    /**
     * Retrieves the linked accounts for an account
     * - For all the owners of the account
     * - Retrieves the associated person key
     * - Filters the associated persons based on account approver and registration done
     *
     * @param accountId
     *
     * @return Collection<WrapAccount>
     */

    public Collection<WrapAccount> getLinkedAccountsForAccount(String accountId) {
        if (accountMap != null) {
            WrapAccount currentAccount = accountMap.get(AccountKey.valueOf(accountId));
            Set<ClientKey> allLinkedClients = new HashSet<>();
            allLinkedClients.addAll(currentAccount.getAccountOwners());
            for (ClientKey clientKey : currentAccount.getAccountOwners()) {
                final Client client = clientMap.get(clientKey);
                allLinkedClients.addAll(getAssociatedPersonsRelatedAccountsOwner(clientKey));
                final List<ClientKey> associatedPersonKeys = ((Investor) client).getAssociatedPersonKeys();
                getAssociatedPersonKey(associatedPersonKeys, allLinkedClients, currentAccount.getApprovers());
            }
            return getAllLinkedAccounts(allLinkedClients);
        }
        return new ArrayList<>();
    }

    /**
     * Filters the associated person based on account approvers and regsitered
     *
     * @param associatedPersonKeys
     * @param allLinkedClients
     * @param approvers
     */
    private void getAssociatedPersonKey(final List<ClientKey> associatedPersonKeys, Set<ClientKey> allLinkedClients, final Collection<ClientKey> approvers) {
        if (CollectionUtils.isNotEmpty(associatedPersonKeys)) {
            for (ClientKey associatedPersonKey : associatedPersonKeys) {
                final Client associatedClient = clientMap.get(associatedPersonKey);
                if (approvers.contains(associatedPersonKey) ||
                        (associatedClient instanceof Individual && associatedClient.isRegistrationOnline())) {
                    allLinkedClients.addAll(getLinkedClients(associatedPersonKey));
                }
            }
        }
    }

    /**
     * Get all linked accounts for Client key
     *
     * @param clientKey
     *
     * @return Collection<WrapAccount>
     */
    public Collection<WrapAccount> getLinkedAccountsForClient(ClientKey clientKey) {
        Client client = clientMap.get(clientKey);
        Set<ClientKey> allLinkedClients = new HashSet<>();
        final List<ClientKey> associatedPersonKeys = ((Investor) client).getAssociatedPersonKeys();
        for (ClientKey associatedClientKay : associatedPersonKeys) {
            allLinkedClients.addAll(getLinkedClients(associatedClientKay));
        }
        return getAllLinkedAccounts(allLinkedClients);
    }

    /**
     * Retrieves distinct active and closed accounts of all the linked clients
     * if the client is an owner of the account and the account is active or closed
     *
     * @param allLinkedClients
     *
     * @return Collection<WrapAccount>
     */
    public Collection<WrapAccount> getAllLinkedAccounts(Set<ClientKey> allLinkedClients) {
        if (accountMap != null) {
            List<WrapAccount> linkedAccounts = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(allLinkedClients)) {
                for (ClientKey owner : allLinkedClients) {
                    for (WrapAccount wrapAccount : accountMap.values()) {
                        if ((AccountStatus.ACTIVE.equals(wrapAccount.getAccountStatus()) || AccountStatus.CLOSE.equals(wrapAccount.getAccountStatus())) &&
                                wrapAccount.getAccountOwners().contains(owner)) {
                            linkedAccounts.add(wrapAccount);
                        }
                    }
                }
                return Lambda.selectDistinct(linkedAccounts, "accountNumber");
            }
        }
        return new HashSet<>();
    }


    /**
     * Retrieves distinct active  accounts of all the linked clients filtering all the closed accounts
     * if the client is an owner of the account and the account is active or closed
     *
     * @param allLinkedClients
     *
     * @return Collection<WrapAccount>
     */
    public Collection<WrapAccount> getAllActiveLinkedAccounts(Set<ClientKey> allLinkedClients) {
        if (accountMap != null && CollectionUtils.isNotEmpty(allLinkedClients)) {
            List<WrapAccount> linkedAccounts = new ArrayList<>();
            for (ClientKey owner : allLinkedClients) {
                for (WrapAccount wrapAccount : accountMap.values()) {
                    if ((AccountStatus.ACTIVE.equals(wrapAccount.getAccountStatus())) &&
                            wrapAccount.getAccountOwners().contains(owner)) {
                        linkedAccounts.add(wrapAccount);
                    }
                }
            }
            return Lambda.selectDistinct(linkedAccounts, "accountNumber");
        }
        return new HashSet<>();
    }
}
