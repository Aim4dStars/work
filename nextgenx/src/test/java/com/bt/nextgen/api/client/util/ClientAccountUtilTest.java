package com.bt.nextgen.api.client.util;

import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.client.ClientImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ClientAccountUtilTest {

    private Map<ClientKey, Client> clientMap;
    private Map<AccountKey, WrapAccount> accountMap;

    private ClientAccountUtil clientAccountUtil;


    @Before
    public void setUp() {

        clientMap = new HashMap<>();
        accountMap = new HashMap<>();

        ClientKey clientKey1 = ClientKey.valueOf("12345");
        ClientKey clientKey2 = ClientKey.valueOf("67894");
        ClientKey clientKey3 = ClientKey.valueOf("65987");
        ClientKey clientKey4 = ClientKey.valueOf("23659");
        ClientKey clientKey5 = ClientKey.valueOf("36598");
        ClientKey clientKey6 = ClientKey.valueOf("45879");
        ClientKey clientKey7 = ClientKey.valueOf("65987");

        ClientImpl client1 = new IndividualImpl();
        ClientImpl client2 = new IndividualImpl();
        ClientImpl client3 = new IndividualImpl();
        ClientImpl client4 = new IndividualImpl();
        ClientImpl client5 = new IndividualImpl();
        ClientImpl client6 = new IndividualImpl();
        ClientImpl client7 = new IndividualImpl();

        List<ClientKey> associateClients1 = new ArrayList<>();
        associateClients1.add(clientKey3);
        associateClients1.add(clientKey4);
        associateClients1.add(clientKey6);
        client1.setClientKey(clientKey1);
        client1.setAssociatedPersonKeys(associateClients1);
        client1.setRegistrationOnline(false);

        List<ClientKey> associateClients2 = new ArrayList<>();
        associateClients2.add(clientKey3);
        associateClients2.add(clientKey6);
        client2.setClientKey(clientKey2);
        client2.setAssociatedPersonKeys(associateClients2);
        client2.setRegistrationOnline(false);

        List<ClientKey> associateClients3 = new ArrayList<>();
        associateClients3.add(clientKey6);
        client3.setClientKey(clientKey3);
        client3.setAssociatedPersonKeys(associateClients3);
        client3.setRegistrationOnline(false);

        List<ClientKey> associateClients4 = new ArrayList<>();
        client4.setClientKey(clientKey4);
        client4.setRegistrationOnline(true);

        List<ClientKey> associateClients5 = new ArrayList<>();
        associateClients5.add(clientKey4);
        client5.setClientKey(clientKey5);
        client5.setRegistrationOnline(false);
        client5.setAssociatedPersonKeys(associateClients5);

        List<ClientKey> associateClients6 = new ArrayList<>();
        client6.setClientKey(clientKey6);
        client6.setRegistrationOnline(false);

        List<ClientKey> associateClients7 = new ArrayList<>();
        associateClients7.add(clientKey1);
        client7.setClientKey(clientKey7);
        client7.setAssociatedPersonKeys(associateClients7);

        clientMap.put(clientKey1, client1);
        clientMap.put(clientKey2, client2);
        clientMap.put(clientKey3, client3);
        clientMap.put(clientKey4, client4);
        clientMap.put(clientKey5, client5);
        clientMap.put(clientKey6, client6);

        AccountKey accountKey1 = AccountKey.valueOf("58964");
        AccountKey accountKey2 = AccountKey.valueOf("45698");
        AccountKey accountKey3 = AccountKey.valueOf("25698");
        AccountKey accountKey4 = AccountKey.valueOf("46987");
        AccountKey accountKey5 = AccountKey.valueOf("12698");
        AccountKey accountKey6 = AccountKey.valueOf("56987");
        AccountKey accountKey7 = AccountKey.valueOf("56687");
        AccountKey accountKey8 = AccountKey.valueOf("65987");

        WrapAccountImpl wrapAccount1 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount2 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount3 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount4 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount5 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount6 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount7 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount8 = new WrapAccountImpl();

        List<ClientKey> accountOwner1 = new ArrayList<>();
        accountOwner1.add(clientKey1);
        List<ClientKey> accountApprover1 = new ArrayList<>();
        accountApprover1.add(clientKey3);
        wrapAccount1.setAccountKey(accountKey1);
        wrapAccount1.setAccountOwners(accountOwner1);
        wrapAccount1.setAccountNumber("236598745");
        wrapAccount1.setApprovers(accountApprover1);

        List<ClientKey> accountOwner2 = new ArrayList<>();
        accountOwner2.add(clientKey2);
        wrapAccount2.setAccountKey(accountKey2);
        wrapAccount2.setAccountNumber("659874563");
        wrapAccount2.setAccountOwners(accountOwner2);

        List<ClientKey> accountOwner3 = new ArrayList<>();
        accountOwner3.add(clientKey1);
        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountNumber("569874521");
        wrapAccount3.setAccountOwners(accountOwner3);

        List<ClientKey> accountOwner4 = new ArrayList<>();
        accountOwner4.add(clientKey4);
        wrapAccount4.setAccountKey(accountKey4);
        wrapAccount4.setAccountNumber("986587456");
        wrapAccount4.setAccountOwners(accountOwner4);

        List<ClientKey> accountOwner5 = new ArrayList<>();
        accountOwner5.add(clientKey6);
        wrapAccount5.setAccountKey(accountKey5);
        wrapAccount5.setAccountNumber("125547895");
        wrapAccount5.setAccountOwners(accountOwner5);

        List<ClientKey> accountOwner6 = new ArrayList<>();
        accountOwner6.add(clientKey3);
        wrapAccount6.setAccountKey(accountKey6);
        wrapAccount6.setAccountNumber("512569874");
        wrapAccount6.setAccountOwners(accountOwner6);

        List<ClientKey> accountOwner7 = new ArrayList<>();
        accountOwner7.add(clientKey5);
        wrapAccount7.setAccountKey(accountKey7);
        wrapAccount7.setAccountNumber("545698745");
        wrapAccount7.setAccountOwners(accountOwner7);

        List<ClientKey> accountOwner8 = new ArrayList<>();
        accountOwner8.add(clientKey7);
        List<ClientKey> accountApprover2 = new ArrayList<>();
        accountApprover2.add(clientKey1);
        wrapAccount8.setAccountKey(accountKey1);
        wrapAccount8.setAccountOwners(accountOwner8);
        wrapAccount8.setAccountNumber("236578745");
        wrapAccount8.setApprovers(accountApprover2);

        accountMap.put(accountKey1, wrapAccount1);
        accountMap.put(accountKey2, wrapAccount2);
        accountMap.put(accountKey3, wrapAccount3);
        accountMap.put(accountKey4, wrapAccount4);
        accountMap.put(accountKey5, wrapAccount5);
        accountMap.put(accountKey6, wrapAccount6);
        accountMap.put(accountKey7, wrapAccount7);
        accountMap.put(accountKey8, wrapAccount8);

        clientAccountUtil = new ClientAccountUtil(clientMap, accountMap);
    }

    @Test
    public void testGetLinkedAccountsForAccount() {
        Collection<WrapAccount> linkedAccounts = clientAccountUtil.getLinkedAccountsForAccount("58964");
        Assert.assertNotNull(linkedAccounts);
        Assert.assertTrue(linkedAccounts.size()==7);
    }

    @Test
    public void testGetLinkedAccountsForClient() {
        Collection<WrapAccount> linkedAccounts = clientAccountUtil.getLinkedAccountsForClient(ClientKey.valueOf("12345"));
        Assert.assertNotNull(linkedAccounts);
        Assert.assertTrue(linkedAccounts.size()==8);
    }
}
