package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.client.ClientListImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

/**
 * Created by L062329 on 29/12/2014.
 */
@RunWith(MockitoJUnitRunner.class)
public class ClientFilterUtilTest {

    @Mock
    private ClientIntegrationService clientIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    UserProfileService userProfileService;

    ServiceErrors serviceErrors;

    private ClientFilterUtil clientFilterUtil;

    private List<ApiSearchCriteria> criteriaList;
    Broker broker;

    private final String queryString = "[{\"prop\":\"portfolioValue\",\"op\":\"~<\",\"val\":\"100000\",\"type\":\"number\"},"
            + "{\"prop\":\"portfolioValue\",\"op\":\"<\",\"val\":\"300000\",\"type\":\"number\"},"
            + "{\"prop\":\"availableCash\",\"op\":\"~<\",\"val\":\"100000\",\"type\":\"number\"},"
            + "{\"prop\":\"availableCash\",\"op\":\"<\",\"val\":\"300000\",\"type\":\"number\"},"
            + "{\"prop\":\"product\",\"op\":\"=\",\"val\":\"White Label 35d1b65704184ae3b87799400f7ab93c\",\"type\":\"string\"},"
            + "{\"prop\":\"country\",\"op\":\"=\",\"val\":\"Australia\",\"type\":\"string\"},"
            + "{\"prop\":\"state\",\"op\":\"=\",\"val\":\"Australia Capital Territory\",\"type\":\"string\"},"
            + "{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"},"
            + "{\"op\":\"c\",\"prop\":\"displayName\",\"type\":\"string\",\"val\":\"Jo\"},"
            + "{\"prop\":\"registeredOnline\",\"op\":\"=\",\"val\":\"false\",\"type\":\"boolean\"}]";

    private final String clientSearchQueryString = "[{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"},"
            + "{\"op\":\"c\",\"prop\":\"displayName\",\"type\":\"string\",\"val\":\"Jo\"},"
            + "{\"prop\":\"registeredOnline\",\"op\":\"=\",\"val\":\"false\",\"type\":\"boolean\"}]";

    @Before
    public void setup() {
        serviceErrors = new FailFastErrorsImpl();
        clientFilterUtil = new ClientFilterUtil(clientIntegrationService, accountService, productIntegrationService,
                brokerIntegrationService);
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);

        ClientKey clientkey1 = ClientKey.valueOf("74609");
        ClientKey clientkey2 = ClientKey.valueOf("77172");
        broker = Mockito.mock(Broker.class);
        Mockito.when(broker.isDirectInvestment()).thenReturn(true);
        BrokerKey brokerKey1 = BrokerKey.valueOf("68747");

        Map<ClientKey, Client> clientMap = new HashMap<>();
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
        Map<ProductKey, Product> productMap = new HashMap<>();

        BrokerUser brokerUser = Mockito.mock(BrokerUser.class);
        Mockito.when(brokerUser.getBankReferenceKey()).thenReturn(UserKey.valueOf("testUser"));
        Mockito.when(brokerUser.getJob()).thenReturn(JobKey.valueOf("testJob"));
        Mockito.when(brokerUser.getFirstName()).thenReturn("Fperson-120_2682");
        Mockito.when(brokerUser.getLastName()).thenReturn("Lperson-120_2682");
        Mockito.when(brokerUser.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
        Mockito.when(brokerUser.isRegisteredOnline()).thenReturn(false);
        Mockito.when(brokerUser.getAge()).thenReturn(0);
        Mockito.when(brokerUser.isRegistrationOnline()).thenReturn(false);

        List<ClientKey> associatedPersons1 = new ArrayList<>();
        ClientKey associatedClientKey1 = ClientKey.valueOf("34125");
        ClientKey associatedClientKey2 = ClientKey.valueOf("34126");
        ClientKey associatedClientKey3 = ClientKey.valueOf("34127");
        associatedPersons1.add(associatedClientKey1);
        associatedPersons1.add(associatedClientKey2);
        associatedPersons1.add(associatedClientKey3);

        List<ClientKey> associatedPersons2 = new ArrayList<>();
        ClientKey associatedClientKey4 = ClientKey.valueOf("34128");
        ClientKey associatedClientKey5 = ClientKey.valueOf("34129");
        ClientKey associatedClientKey6 = ClientKey.valueOf("34130");
        associatedPersons2.add(associatedClientKey4);
        associatedPersons2.add(associatedClientKey5);
        associatedPersons2.add(associatedClientKey6);

        ProductKey productKey1 = ProductKey.valueOf("41422");
        ProductImpl product2 = new ProductImpl();
        product2.setProductKey(productKey1);
        product2.setProductName("White Label 1 35d1b65704184ae3b87799400f7ab93c");
        productMap.put(productKey1, product2);

        ClientListImpl individualClient = new ClientListImpl();
        individualClient.setClientKey(clientkey1);
        individualClient.setFirstName("Adrian");
        individualClient.setLastName("Smith");
        individualClient.setFullName("Adrian Demo Smith");
        individualClient.setClientType(ClientType.N);
        individualClient.setAssociatedPersonKeys(associatedPersons1);

        List<Address> addressList1 = new ArrayList<>();
        AddressImpl address1 = new AddressImpl();
        address1.setState("New South Wales");
        address1.setCountry("Australia");
        addressList1.add(address1);

        individualClient.setAddresses(addressList1);
        ClientListImpl individualClient2 = new ClientListImpl();
        individualClient2.setClientKey(clientkey2);
        individualClient2.setFirstName(null);
        individualClient2.setLastName(null);
        individualClient2.setFullName("Demo Parkside Pty Ltd");
        individualClient2.setClientType(ClientType.L);
        List<Address> addressList2 = new ArrayList<>();
        AddressImpl address2 = new AddressImpl();
        address2.setState("New South Wales");
        address2.setCountry("Australia");
        addressList2.add(address2);
        individualClient2.setAddresses(addressList2);
        individualClient2.setAssociatedPersonKeys(associatedPersons2);

        /* Associated clients */
        ClientListImpl associatedClient1 = new ClientListImpl();
        associatedClient1.setClientKey(associatedClientKey1);
        associatedClient1.setFirstName("Joe");
        associatedClient1.setLastName("Neel");
        associatedClient1.setFullName("Joe Demo Neel");
        associatedClient1.setClientType(ClientType.N);
        List<Address> addressList3 = new ArrayList<>();
        AddressImpl address3 = new AddressImpl();
        address3.setState("Australia Capital Territory");
        address3.setCountry("Australia");
        addressList3.add(address3);
        associatedClient1.setAddresses(addressList3);

        ClientListImpl associatedClient2 = new ClientListImpl();
        associatedClient2.setClientKey(associatedClientKey2);
        associatedClient2.setFirstName("Adrian");
        associatedClient2.setLastName("Smith");
        associatedClient2.setFullName("Adrian Demo Smith");
        associatedClient2.setClientType(ClientType.N);
        List<Address> addressList4 = new ArrayList<>();
        AddressImpl address4 = new AddressImpl();
        address4.setState("New South Wales");
        address4.setCountry("Australia");
        addressList4.add(address4);
        associatedClient2.setAddresses(addressList4);

        ClientListImpl associatedClient3 = new ClientListImpl();
        associatedClient3.setClientKey(associatedClientKey3);
        associatedClient3.setFirstName("Adrian");
        associatedClient3.setLastName("Smith");
        associatedClient3.setFullName("Adrian Demo Smith");
        associatedClient3.setClientType(ClientType.N);
        associatedClient3.setRegistrationOnline(true);
        List<Address> addressList5 = new ArrayList<>();
        AddressImpl address5 = new AddressImpl();
        address5.setState("New South Wales");
        address5.setCountry("Australia");
        addressList5.add(address5);
        associatedClient3.setAddresses(addressList5);

        ClientListImpl associatedClient4 = new ClientListImpl();
        associatedClient4.setClientKey(associatedClientKey4);
        associatedClient4.setFirstName("Adrian");
        associatedClient4.setLastName("Smith");
        associatedClient4.setFullName("Adrian Demo Smith");
        associatedClient4.setClientType(ClientType.N);
        List<Address> addressList6 = new ArrayList<>();
        AddressImpl address6 = new AddressImpl();
        address6.setState("New South Wales");
        address6.setCountry("Australia");
        addressList6.add(address6);
        associatedClient4.setAddresses(addressList6);

        ClientListImpl associatedClient5 = new ClientListImpl();
        associatedClient5.setClientKey(associatedClientKey5);
        associatedClient5.setFirstName("Adrian");
        associatedClient5.setLastName("Smith");
        associatedClient5.setFullName("Adrian Demo Smith");
        associatedClient5.setClientType(ClientType.N);
        List<Address> addressList7 = new ArrayList<>();
        AddressImpl address7 = new AddressImpl();
        address7.setState("New South Wales");
        address7.setCountry("Australia");
        addressList7.add(address7);
        associatedClient5.setAddresses(addressList7);

        ClientListImpl associatedClient6 = new ClientListImpl();
        associatedClient6.setClientKey(associatedClientKey6);
        associatedClient6.setFirstName("Adrian");
        associatedClient6.setLastName("Smith");
        associatedClient6.setFullName("Adrian Demo Smith");
        associatedClient6.setClientType(ClientType.N);
        List<Address> addressList8 = new ArrayList<>();
        AddressImpl address8 = new AddressImpl();
        address8.setState("New South Wales");
        address8.setCountry("Australia");
        addressList8.add(address8);
        associatedClient6.setAddresses(addressList8);
        /**/

        clientMap.put(clientkey1, individualClient);
        clientMap.put(clientkey2, individualClient2);
        clientMap.put(associatedClientKey1, associatedClient1);
        clientMap.put(associatedClientKey2, associatedClient2);
        clientMap.put(associatedClientKey3, associatedClient3);
        clientMap.put(associatedClientKey4, associatedClient4);
        clientMap.put(associatedClientKey5, associatedClient5);
        clientMap.put(associatedClientKey6, associatedClient6);

        AccountKey accountKey1 = AccountKey.valueOf("74611");
        AccountKey accountKey2 = AccountKey.valueOf("74643");
        AccountKey accountKey3 = AccountKey.valueOf("11263");
        WrapAccountImpl account1 = new WrapAccountImpl();
        WrapAccountImpl account2 = new WrapAccountImpl();
        WrapAccountImpl account3 = new WrapAccountImpl();

        List<ClientKey> clientIdentifiers = new ArrayList<>();
        clientIdentifiers.add(clientkey1);
        List<ClientKey> accountApprover1 = new ArrayList<>();
        accountApprover1.add(associatedClientKey1);
        account1.setAccountKey(accountKey1);
        account1.setAccountName("Michael Tonini");
        account1.setAccountNumber("120011366");
        account1.setAccountOwners(clientIdentifiers);
        account1.setAccountStructureType(AccountStructureType.Individual);
        account1.setAdviserPersonId(ClientKey.valueOf("1234"));
        account1.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits));
        account1.setAdviserPositionId(brokerKey1);
        account1.setAccountStatus(AccountStatus.ACTIVE);
        account1.setProductKey(productKey1);
        account1.setApprovers(accountApprover1);

        List<ClientKey> accountApprover2 = new ArrayList<>();
        accountApprover2.add(associatedClientKey2);
        account3.setAccountKey(accountKey2);
        account3.setAccountName("John Cooper");
        account3.setAccountNumber("120011366");
        account3.setAdviserPersonId(ClientKey.valueOf("1234"));
        account3.setAccountOwners(clientIdentifiers);
        account3.setAccountStructureType(AccountStructureType.Individual);
        account3.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits_To_Linked_Accounts));
        account3.setAdviserPositionId(brokerKey1);
        account3.setAccountStatus(AccountStatus.ACTIVE);
        account3.setProductKey(productKey1);
        account3.setApprovers(accountApprover2);

        List<ClientKey> clientIdentifiers2 = new ArrayList<>();
        clientIdentifiers2.add(clientkey2);
        List<ClientKey> accountApprover3 = new ArrayList<>();
        accountApprover3.add(associatedClientKey4);
        account2.setAccountKey(accountKey3);
        account2.setAccountName("Oniston Pty Limited - 01");
        account2.setAdviserPersonId(ClientKey.valueOf("1234"));
        account2.setAccountNumber("120000005");
        account2.setAccountOwners(clientIdentifiers2);
        account2.setAccountStructureType(AccountStructureType.Company);
        account2.setAdviserPermissions(Collections.singleton(TransactionPermission.Payments_Deposits_To_Linked_Accounts));
        account2.setAdviserPositionId(brokerKey1);
        account2.setAccountStatus(AccountStatus.ACTIVE);
        account2.setProductKey(productKey1);
        account2.setApprovers(accountApprover3);

        accountMap.put(accountKey1, account1);
        accountMap.put(accountKey2, account2);
        accountMap.put(accountKey3, account3);

        AccountBalanceImpl accountBalance1 = new AccountBalanceImpl();

        accountBalance1.setAccountKey(accountKey1.getId());
        accountBalance1.setAvailableCash(new BigDecimal(30000));
        accountBalance1.setPortfolioValue(new BigDecimal(20000));

        AccountBalanceImpl accountBalance2 = new AccountBalanceImpl();

        accountBalance2.setAccountKey(accountKey2.getId());
        accountBalance2.setAvailableCash(new BigDecimal(10000));
        accountBalance2.setPortfolioValue(new BigDecimal(20000));

        AccountBalanceImpl accountBalance3 = new AccountBalanceImpl();

        accountBalance3.setAccountKey(accountKey3.getId());
        accountBalance3.setAvailableCash(new BigDecimal(30000));
        accountBalance3.setPortfolioValue(new BigDecimal(20000));

        List<AccountBalance> accountBalanceList = new ArrayList<>();
        accountBalanceList.add(accountBalance1);
        accountBalanceList.add(accountBalance2);
        accountBalanceList.add(accountBalance3);

        accountBalanceMap.put(accountKey1, accountBalance1);
        accountBalanceMap.put(accountKey2, accountBalance2);
        accountBalanceMap.put(accountKey3, accountBalance3);

        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(brokerKey1, brokerUser, true, "");
        HashMap brokerWrapperMap = new HashMap<>();
        brokerWrapperMap.put(brokerKey1, brokerWrapper);

        Mockito.when(clientIntegrationService.loadClientMap(Mockito.any(ServiceErrors.class))).thenReturn(clientMap);
        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);
        Mockito.when(accountService.loadAccountBalancesMap(Mockito.any(ServiceErrors.class))).thenReturn(accountBalanceMap);
        when(brokerIntegrationService.getAdviserBrokerUser(anyListOf(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerWrapperMap);
        Mockito.when(productIntegrationService.loadProductsMap(Mockito.any(ServiceErrors.class))).thenReturn(productMap);
        Mockito.when(userProfileService.isInvestor()).thenReturn(false);
    }

    @Test
    public void accountFilterCriteriaTest() {
        List<ApiSearchCriteria> accountFilterCriteria = clientFilterUtil.accountFilterCriteria(criteriaList);
        assertThat(accountFilterCriteria.size(), is(6));
    }

    @Test
    public void clientFilterCriteriaTest() {
        List<ApiSearchCriteria> accountFilterCriteria = clientFilterUtil.clientFilterCriteria(criteriaList);
        assertThat(accountFilterCriteria.size(), is(4));
    }

    @Test
    public void getFilteredValueTest() {
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, clientSearchQueryString);
        List<ClientIdentificationDto> clientDtos = clientFilterUtil.getFilteredValue(criteriaList, serviceErrors);
        Assert.assertEquals(1, clientDtos.size());

        ClientDto clientDto = (ClientDto) clientDtos.get(0);

        Assert.assertEquals(clientDto.getFirstName(), "Joe");
        Assert.assertEquals(clientDto.getLastName(), "Neel");
        Assert.assertEquals("Neel, Joe", clientDto.getDisplayName());
        Assert.assertEquals("34125", new EncodedString(clientDto.getKey().getClientId()).plainText());
        Assert.assertEquals(2, clientDto.getAccounts().size());
    }
}
