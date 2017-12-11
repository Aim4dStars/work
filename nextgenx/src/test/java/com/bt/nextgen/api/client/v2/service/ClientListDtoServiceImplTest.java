package com.bt.nextgen.api.client.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.client.model.AddressDto;
import com.bt.nextgen.api.client.model.AddressKey;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.model.ClientTxnDto;
import com.bt.nextgen.api.client.model.EmailDto;
import com.bt.nextgen.api.client.model.PhoneDto;
import com.bt.nextgen.api.client.service.ClientUpdateCategory;
import com.bt.nextgen.api.client.v2.model.ClientDto;
import com.bt.nextgen.api.client.v2.util.ClientDetailDtoConverter;
import com.bt.nextgen.api.client.validation.ClientDetailsDtoErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.TransactionPermission;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.client.ClientImpl;
import com.bt.nextgen.service.avaloq.client.ClientListImpl;
import com.bt.nextgen.service.avaloq.domain.AddressImpl;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualImpl;
import com.bt.nextgen.service.avaloq.domain.InvestorDetailImpl;
import com.bt.nextgen.service.avaloq.domain.PersonDetailImpl;
import com.bt.nextgen.service.avaloq.domain.RegisteredEntityImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.gesb.locationmanagement.PostalAddress;
import com.bt.nextgen.service.gesb.locationmanagement.v1.LocationManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerDataManagementIntegrationService;
import com.bt.nextgen.service.group.customer.CustomerLoginManagementIntegrationService;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.ExemptionReason;
import com.bt.nextgen.service.integration.domain.IdentityVerificationStatus;
import com.btfin.panorama.core.security.integration.domain.IndividualDetail;
import com.btfin.panorama.core.security.integration.domain.InvestorDetail;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.util.StringUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientListDtoServiceImplTest {
    @InjectMocks
    private ClientListDtoServiceImpl clientListDtoService;

    @Mock
    ClientIntegrationService clientService;

    @Mock
    AccountIntegrationService accountService;

    @Mock
    ProductIntegrationService productIntegrationService;

    @Mock
    private ClientDetailsDtoErrorMapper clientDetailsDtoErrorMapper;

    @Mock
    BrokerIntegrationService brokerIntegrationService;

    @Mock
    UserProfileService userProfileService;

    @Mock
    CustomerDataManagementIntegrationService preferredNameManagementIntegrationService;

    @Mock
    CustomerDataManagementIntegrationService customerContactDetailsManagementIntegrationService;

    @Mock
    CustomerLoginManagementIntegrationService customerLoginManagementIntegrationService;

    @Mock
    private LocationManagementIntegrationService addressService;

    private HttpServletRequest request;

    List<ApiSearchCriteria> criteriaList;
    ServiceErrors serviceErrors;


    List<EmailDto> emails;
    List<String> emailsFinal;
    List<PhoneDto> phones;
    List<String> phonesFinal;

    @Before
    public void setup() {
        serviceErrors = new FailFastErrorsImpl();
        emails = new ArrayList<EmailDto>();
        emailsFinal = new ArrayList<String>();
        phones = new ArrayList<PhoneDto>();
        phonesFinal = new ArrayList<String>();

        ClientKey clientkey1 = ClientKey.valueOf("74609");
        ClientKey clientkey2 = ClientKey.valueOf("77172");

        BrokerKey brokerKey1 = BrokerKey.valueOf("68747");

        Map<ClientKey, Client> clientMap = new HashMap<>();
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
        Map<ProductKey, Product> productMap = new HashMap<>();

        BrokerUser brokerUser = Mockito.mock(BrokerUser.class);
        Broker broker = mock(Broker.class);
        Mockito.when(brokerUser.getBankReferenceKey()).thenReturn(UserKey.valueOf("testUser"));
        Mockito.when(brokerUser.getJob()).thenReturn(JobKey.valueOf("testJob"));
        Mockito.when(brokerUser.getFirstName()).thenReturn("Fperson-120_2682");
        Mockito.when(brokerUser.getLastName()).thenReturn("Lperson-120_2682");
        Mockito.when(brokerUser.getClientKey()).thenReturn(ClientKey.valueOf("testClient"));
        Mockito.when(brokerUser.isRegisteredOnline()).thenReturn(false);
        Mockito.when(brokerUser.getAge()).thenReturn(0);
        Mockito.when(brokerUser.isRegistrationOnline()).thenReturn(false);

        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(brokerKey1, brokerUser, true, "");
        Map<BrokerKey, BrokerWrapper>brokerWrapperMap = new HashMap<>();
        brokerWrapperMap.put(brokerKey1, brokerWrapper);

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

        /*Associated clients*/
        ClientListImpl associatedClient1 = new ClientListImpl();
        associatedClient1.setClientKey(associatedClientKey1);
        associatedClient1.setFirstName("Adrian");
        associatedClient1.setLastName("Smith");
        associatedClient1.setFullName("Adrian Demo Smith");
        associatedClient1.setClientType(ClientType.N);
        List<Address> addressList3 = new ArrayList<>();
        AddressImpl address3 = new AddressImpl();
        address3.setState("Victoria");
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

        EmailDto e1 = new EmailDto();
        e1.setPreferred(false);
        e1.setEmailType("Secondary");
        e1.setEmail("Dummy@BTFinancialGroup.com");

        EmailDto e2 = new EmailDto();
        e2.setPreferred(false);
        e2.setEmailType("Secondary");
        e2.setEmail("abc@btfinancialgroup.com");

        EmailDto e3 = new EmailDto();
        e3.setPreferred(true);
        e3.setEmailType("Secondary");
        e3.setEmail("test@btfinancialgroup.com");

        EmailDto e4 = new EmailDto();
        e4.setPreferred(true);
        e4.setEmailType("Primary");
        e4.setEmail("Dummy@BTFinancialGroup.com");


        EmailDto e5 = new EmailDto();
        e5.setPreferred(false);
        e5.setEmailType("Secondary");
        e5.setEmail("test@btfinancialgroup.com");

        EmailDto e6 = new EmailDto();
        e6.setPreferred(false);
        e6.setEmailType("Secondary");
        e6.setEmail("abc@btfinancialgroup.com");


        emails.add(e1);
        emails.add(e2);
        emails.add(e3);
        emails.add(e4);
        emails.add(e5);
        emails.add(e6);


        emailsFinal.add("Dummy@BTFinancialGroup.com");
        emailsFinal.add("abc@btfinancialgroup.com");
        emailsFinal.add("test@btfinancialgroup.com");
        emailsFinal.add("Dummy@BTFinancialGroup.com");


        PhoneDto phone1 = new PhoneDto();
        PhoneDto phone2 = new PhoneDto();
        PhoneDto phone3 = new PhoneDto();
        PhoneDto phone4 = new PhoneDto();
        PhoneDto phone5 = new PhoneDto();

        phone1.setPreferred(false);
        phone1.setNumber("61419123456");
        phone1.setPhoneType("Secondary");


        phone2.setPreferred(true);
        phone2.setNumber("61419122223");
        phone2.setPhoneType("Secondary");

        phone3.setPreferred(true);
        phone3.setNumber("61419123456");
        phone3.setPhoneType("Secondary");

        phone4.setPreferred(true);
        phone4.setNumber("61419123456");
        phone4.setPhoneType("Primary");

        phone5.setPreferred(false);
        phone5.setNumber("61456562228");
        phone5.setPhoneType("Work");

        phones.add(phone1);
        phones.add(phone2);
        phones.add(phone3);
        phones.add(phone4);


        phonesFinal.add("61419122223");
        phonesFinal.add("61419123456");
        phonesFinal.add("61419123456");

        when(clientService.loadClientMap(any(ServiceErrors.class))).thenReturn(clientMap);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accountMap);
        when(accountService.loadAccountBalancesMap(any(ServiceErrors.class))).thenReturn(accountBalanceMap);
        when(brokerIntegrationService.getAdviserBrokerUser(any(BrokerKey.class),
                any(ServiceErrors.class)))
                .thenReturn(brokerUser);
        when(brokerIntegrationService.getAdviserBrokerUser(anyListOf(BrokerKey.class),
                any(ServiceErrors.class)))
                .thenReturn(brokerWrapperMap);
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class)))
                .thenReturn(productMap);
        when(userProfileService.isInvestor()).thenReturn(false);
        createMockRequest();
    }

    private HttpServletRequest createMockRequest() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        return request;
    }

    private String getCriteriaString(String minPortfolioVal, String maxPortfolioVal, String minAvailableCash, String maxAvailableCash, String product, String country, String state, boolean isOwner, boolean isApprover) {
        StringBuilder stringBuilder = new StringBuilder("[{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"}");

        if (StringUtil.isNotNullorEmpty(minPortfolioVal)) {
            stringBuilder.append(",{\"prop\":\"portfolioValue\",\"op\":\"~<\",\"val\":\"" + minPortfolioVal + "\",\"type\":\"number\"}");
        }
        if (StringUtil.isNotNullorEmpty(maxPortfolioVal)) {
            stringBuilder.append(",{\"prop\":\"portfolioValue\",\"op\":\"<\",\"val\":\"" + maxPortfolioVal + "\",\"type\":\"number\"}");
        }
        if (StringUtil.isNotNullorEmpty(minAvailableCash)) {
            stringBuilder.append(",{\"prop\":\"availableCash\",\"op\":\"~<\",\"val\":\"" + minAvailableCash + "\",\"type\":\"number\"}");
        }
        if (StringUtil.isNotNullorEmpty(maxAvailableCash)) {
            stringBuilder.append(",{\"prop\":\"availableCash\",\"op\":\"<\",\"val\":\"" + maxAvailableCash + "\",\"type\":\"number\"}");
        }
        if (StringUtil.isNotNullorEmpty(product)) {
            stringBuilder.append(",{\"prop\":\"product\",\"op\":\"=\",\"val\":\"" + product + "\",\"type\":\"string\"}");
        }
        if (StringUtil.isNotNullorEmpty(country)) {
            stringBuilder.append(",{\"prop\":\"country\",\"op\":\"=\",\"val\":\"" + country + "\",\"type\":\"string\"}");
        }
        if (StringUtil.isNotNullorEmpty(state)) {
            stringBuilder.append(",{\"prop\":\"state\",\"op\":\"=\",\"val\":\"" + state + "\",\"type\":\"string\"}");
        }
        if (isOwner) {
            stringBuilder.append(",{\"prop\":\"Owner\",\"op\":\"=\",\"val\":\"Owner\",\"type\":\"string\"}");
        }
        if (isApprover) {
            stringBuilder.append(",{\"prop\":\"Approver\",\"op\":\"=\",\"val\":\"Approver\",\"type\":\"string\"}");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Test
    public void testfindAll() {

        List<ClientIdentificationDto> clientDtos = clientListDtoService.findAll(serviceErrors);
        Assert.assertEquals(8, clientDtos.size());

        ClientDto clientDto = (ClientDto) clientDtos.get(0);

        Assert.assertEquals(clientDto.getFirstName(), "Adrian");
        Assert.assertEquals(clientDto.getLastName(), "Smith");
        Assert.assertEquals("Smith, Adrian", clientDto.getDisplayName());
        Assert.assertEquals("34127", new EncodedString(clientDto.getKey().getClientId()).plainText());
        Assert.assertEquals(2, clientDto.getAccounts().size());

        List<AccountDto> accountDtos = clientDto.getAccounts();

        AccountDto accountDto = accountDtos.get(0);

        Assert.assertEquals(accountDto.getAccountId(), "120011366");
        Assert.assertEquals(ConsistentEncodedString.toPlainText(accountDto.getKey().getAccountId()).toString(), "74611");
        Assert.assertEquals(accountDto.getAccountName(), "Michael Tonini");
        Assert.assertEquals(accountDto.getAccountNumber(), "120011366");
        Assert.assertEquals(accountDto.getAccountType(), "Individual");
        Assert.assertEquals(accountDto.getAccountStatus(), "Active");
        Assert.assertEquals(new EncodedString(accountDto.getAdviserId()).plainText(), "68747");
        Assert.assertEquals(accountDto.getAdviserName(), "Lperson-120_2682, Fperson-120_2682");
        Assert.assertEquals(accountDto.getAdviserPermission(), "All payments");
        Assert.assertEquals(accountDto.getProduct(), "White Label 1 35d1b65704184ae3b87799400f7ab93c");
      /*  Assert.assertEquals(accountDto.getAvailableCash(),new BigDecimal(0));
        Assert.assertEquals(accountDto.getPortfolioValue(),new BigDecimal(0));*/

        accountDto = accountDtos.get(1);

        Assert.assertEquals(accountDto.getAccountId(), "120000005");
        Assert.assertEquals(ConsistentEncodedString.toPlainText(accountDto.getKey().getAccountId()).toString(), "11263");
        Assert.assertEquals(accountDto.getAccountName(), "Oniston Pty Limited - 01");
        Assert.assertEquals(accountDto.getAccountNumber(), "120000005");
        Assert.assertEquals(accountDto.getAccountType(), "Company");
        Assert.assertEquals(accountDto.getAccountStatus(), "Active");
        Assert.assertEquals(new EncodedString(accountDto.getAdviserId()).plainText(), "68747");
        Assert.assertEquals(accountDto.getAdviserName(), "Lperson-120_2682, Fperson-120_2682");
        Assert.assertEquals(accountDto.getAdviserPermission(), "Linked accounts only");
        Assert.assertEquals(accountDto.getProduct(), "White Label 1 35d1b65704184ae3b87799400f7ab93c");
      /*  Assert.assertEquals(accountDto.getAvailableCash(),new BigDecimal(0));
        Assert.assertEquals(accountDto.getPortfolioValue(),new BigDecimal(0));*/

        clientDto = (ClientDto) clientDtos.get(1);

        Assert.assertEquals(clientDto.getFullName(), "Adrian Demo Smith");
        Assert.assertEquals(clientDto.getFirstName(), "Adrian");
        Assert.assertEquals(clientDto.getLastName(), "Smith");
        Assert.assertEquals("Smith, Adrian", clientDto.getDisplayName());
        Assert.assertEquals("34126", new EncodedString(clientDto.getKey().getClientId()).plainText());
        Assert.assertEquals(2, clientDto.getAccounts().size());

        accountDtos = clientDto.getAccounts();

        accountDto = accountDtos.get(0);

        Assert.assertEquals(accountDto.getAccountId(), "120011366");
        Assert.assertEquals(ConsistentEncodedString.toPlainText(accountDto.getKey().getAccountId()).toString(), "74611");
        Assert.assertEquals(accountDto.getAccountName(), "Michael Tonini");
        Assert.assertEquals(accountDto.getAccountNumber(), "120011366");
        Assert.assertEquals(accountDto.getAccountType(), "Individual");
        Assert.assertEquals(accountDto.getAccountStatus(), "Active");
        Assert.assertEquals(new EncodedString(accountDto.getAdviserId()).plainText(), "68747");
        Assert.assertEquals(accountDto.getAdviserName(), "Lperson-120_2682, Fperson-120_2682");
        Assert.assertEquals(accountDto.getAdviserPermission(), "All payments");
        Assert.assertEquals(accountDto.getProduct(), "White Label 1 35d1b65704184ae3b87799400f7ab93c");
      /*  Assert.assertEquals(accountDto.getAvailableCash(),new BigDecimal(0));
        Assert.assertEquals(accountDto.getPortfolioValue(),new BigDecimal(0));*/

    }

    @Test
    public void testSearchForOwnerAndApprover() {
        String queryString = getCriteriaString(null, null, null, null, null, null, null, true, true);
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);

        List<ClientIdentificationDto> clientDtos = clientListDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(6, clientDtos.size());

        ClientDto clientDto = (ClientDto) clientDtos.get(0);
        Assert.assertEquals(new EncodedString(clientDto.getKey().getClientId()).plainText(), "34127");
        Assert.assertEquals(clientDto.getAccounts().size(), 2);

        AccountDto accountDto = clientDto.getAccounts().get(0);

        Assert.assertEquals(accountDto.getAccountNumber(), "120011366");
        Assert.assertEquals(accountDto.getAccountName(), "Michael Tonini");
    }

    @Test
    public void testSearchForOwner() {
        String queryString = getCriteriaString(null, null, null, null, null, null, null, true, false);
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);

        List<ClientIdentificationDto> clientDtos = clientListDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(2, clientDtos.size());
    }

    @Test
    public void testSearchForApprover() {
        String queryString = getCriteriaString(null, null, null, null, null, null, null, false, true);
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);

        List<ClientIdentificationDto> clientDtos = clientListDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(4, clientDtos.size());
    }

    @Test(expected = NullPointerException.class)
    public void testSearchForAvailableCashFilter() {
        String queryString = getCriteriaString(null, null, "20000", null, null, null, null, true, true);
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);

        List<ClientIdentificationDto> clientDtos = clientListDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(4, clientDtos.size());
    }

    @Test
    public void testSearchForStateFilter() {
        String queryString = getCriteriaString(null, null, null, null, null, null, "New South Wales", true, true);
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);

        List<ClientIdentificationDto> clientDtos = clientListDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(5, clientDtos.size());
    }

    @Ignore //Fails under mockito upgrade
    @Test
    public void testUpdatePreferredName() {

        ClientTxnDto trxDto = new ClientTxnDto();
        trxDto.setUpdatedAttribute(ClientUpdateCategory.PREFERRED_NAME.getType());
        trxDto.setInvestorTypeUpdated(InvestorType.INDIVIDUAL.name());
        trxDto.setPreferredName("Adrian");
        trxDto.setModificationSeq("1");
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        trxDto.setKey(key);

        List<DomainApiErrorDto> warnings = new ArrayList<DomainApiErrorDto>();
        List<ValidationError> errorList = new ArrayList<>();
        warnings.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        warnings.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        when(clientDetailsDtoErrorMapper.map(anyList())).thenReturn(warnings);
        final PersonDetailImpl client = new PersonDetailImpl();
        client.setClientType(ClientType.N);
        client.setCisId("990123876598");
        client.setBrandSiloId("WPAC");
        when(clientService.loadClient(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(client);
        when(preferredNameManagementIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(true);

        ClientTxnDto responseDto = (ClientTxnDto) clientListDtoService.update(trxDto, serviceErrors);
        assertNotNull(responseDto);
        assertEquals(request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO), "WPAC");
    }

    @Test
    public void testToDomainModelWhenPreferredName()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object propertyValue = "Adrian";
        String propertyName = ClientUpdateCategory.PREFERRED_NAME.getType();
        InvestorDetail investorDetail;
        investorDetail = new IndividualDetailImpl();
        investorDetail = clientListDtoService.toDomainModel(propertyValue, propertyName, investorDetail);
        assertNotNull(investorDetail);
        assertEquals(((IndividualDetailImpl) investorDetail).getPreferredName(), propertyValue);

    }

    @Test
    public void testToDomainModelWhenCollection() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<PhoneDto> phoneList = new ArrayList<PhoneDto>();
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPreferred(true);
        phoneDto.setCountryCode("1256");
        phoneDto.setNumber("12568975");
        phoneDto.setPhoneType("Primary");
        phoneList.add(phoneDto);
        Object propertyValue = phoneList;
        String propertyName = "phones";
        InvestorDetail investorDetail = new IndividualDetailImpl();
        investorDetail = clientListDtoService.toDomainModel(propertyValue, propertyName, investorDetail);
        assertNotNull(investorDetail);
        assertEquals(((IndividualDetailImpl) investorDetail).getPhones().size(), phoneList.size());
        assertEquals(((IndividualDetailImpl) investorDetail).getPhones().get(0).getCountryCode(), phoneList.get(0)
                .getCountryCode());
        assertEquals(((IndividualDetailImpl) investorDetail).getPhones().get(0).getNumber(), phoneList.get(0).getNumber());
    }

    @Test
    public void testToDomainModelWhenRegisteredEntity()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object propertyValue = true;
        String propertyName = ClientUpdateCategory.GST.getType();
        InvestorDetail investorDetail;
        investorDetail = new RegisteredEntityImpl();
        investorDetail = clientListDtoService.toDomainModel(propertyValue, propertyName, investorDetail);
        assertNotNull(investorDetail);
        assertEquals(((RegisteredEntityImpl) investorDetail).isRegistrationForGst(), propertyValue);
    }

    @Test
    public void testUpdateRegisteredForGST() {
        ClientTxnDto trxDto = new ClientTxnDto();
        trxDto.setUpdatedAttribute(ClientUpdateCategory.GST.getType());
        trxDto.setInvestorTypeUpdated(InvestorType.COMPANY.name());
        trxDto.setRegistrationForGst(false);
        trxDto.setModificationSeq("1");
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        trxDto.setKey(key);

        List<DomainApiErrorDto> warnings = new ArrayList<DomainApiErrorDto>();
        List<ValidationError> errorList = new ArrayList<>();
        warnings.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        warnings.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        InvestorDetail investorDetail;
        investorDetail = new RegisteredEntityImpl();
        ((RegisteredEntityImpl) investorDetail).setModificationSeq("1");
        ((RegisteredEntityImpl) investorDetail).setRegistrationForGst(trxDto.isRegistrationForGst());
        when(clientService.update(any(InvestorDetail.class),
                any(ClientUpdateCategory.class),
                any(ServiceErrors.class))).thenReturn(investorDetail);

        ((RegisteredEntityImpl) investorDetail).setWarnings(errorList);

        when(clientDetailsDtoErrorMapper.map(anyList())).thenReturn(warnings);
        ClientTxnDto responseDto = (ClientTxnDto) clientListDtoService.update(trxDto, serviceErrors);

        assertNotNull(responseDto);
        assertEquals(trxDto.isRegistrationForGst(), responseDto.isRegistrationForGst());
    }

    @Test
    public void testUpdateContactDetails() {
        ClientTxnDto trxDto = new ClientTxnDto();
        trxDto.setUpdatedAttribute(ClientUpdateCategory.CONTACT.getType());
        trxDto.setInvestorTypeUpdated(InvestorType.INDIVIDUAL.name());

        trxDto.setModificationSeq("1");
        List<PhoneDto> phoneList = new ArrayList<PhoneDto>();
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPreferred(true);
        phoneDto.setCountryCode("1256");
        phoneDto.setNumber("12568975");
        phoneDto.setPhoneType("Primary");
        AddressKey phoneKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        phoneDto.setPhoneKey(phoneKey);
        phoneDto.setModificationSeq("3");
        phoneList.add(phoneDto);

        List<AddressDto> addressList = new ArrayList<AddressDto>();
        AddressDto addressDto = new AddressDto();
        addressDto.setModificationSeq("2");
        AddressKey addressKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        addressDto.setAddressKey(addressKey);
        addressList.add(addressDto);

        List<EmailDto> emailList = new ArrayList<EmailDto>();
        EmailDto emailDto = new EmailDto();
        emailDto.setModificationSeq("2");
        emailDto.setEmail("a@a.com");
        AddressKey emailKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        emailDto.setEmailKey(emailKey);
        emailList.add(emailDto);

        trxDto.setPhones(phoneList);
        trxDto.setAddresses(addressList);
        trxDto.setEmails(emailList);
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        trxDto.setKey(key);

        List<DomainApiErrorDto> warnings = new ArrayList<DomainApiErrorDto>();
        List<ValidationError> errorList = new ArrayList<>();
        warnings.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        warnings.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        InvestorDetail investorDetail;
        investorDetail = new IndividualDetailImpl();
        ((InvestorDetailImpl) investorDetail).setModificationSeq("1");
        List<Phone> phoneModelList = ClientDetailDtoConverter.setPhoneListForUpdate(phoneList);
        List<Email> emailModelList = ClientDetailDtoConverter.setEmailListForUpdate(emailList);
        List<Address> addressModelList = ClientDetailDtoConverter.setAddressListForUpdate(addressList);
        ((IndividualDetailImpl) investorDetail).setAddresses(addressModelList);
        ((IndividualDetailImpl) investorDetail).setPhones(phoneModelList);
        ((IndividualDetailImpl) investorDetail).setEmails(emailModelList);
        when(clientService.update(any(InvestorDetail.class),
                any(ClientUpdateCategory.class),
                any(ServiceErrors.class))).thenReturn(investorDetail);

        ((IndividualDetailImpl) investorDetail).setWarnings(errorList);

        when(clientDetailsDtoErrorMapper.map(anyList())).thenReturn(warnings);

        ClientTxnDto responseDto = (ClientTxnDto) clientListDtoService.update(trxDto, serviceErrors);

        assertNotNull(responseDto);
        assertEquals(responseDto.getAddresses().size(), trxDto.getAddresses().size());
        assertEquals(responseDto.getEmails().size(), trxDto.getEmails().size());
        assertEquals(responseDto.getPhones().size(), trxDto.getPhones().size());
    }

    @Test
    public void testUpdateContactDetailsWithAddressV2()
    {
        ClientTxnDto trxDto = new ClientTxnDto();
        trxDto.setUpdatedAttribute(ClientUpdateCategory.ADDRESS.getType());
        trxDto.setInvestorTypeUpdated(InvestorType.INDIVIDUAL.name());

        trxDto.setModificationSeq("1");
        List <PhoneDto> phoneList = new ArrayList <PhoneDto>();
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPreferred(true);
        phoneDto.setCountryCode("1256");
        phoneDto.setNumber("12568975");
        phoneDto.setPhoneType("Primary");
        AddressKey phoneKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        phoneDto.setPhoneKey(phoneKey);
        phoneDto.setModificationSeq("3");
        phoneList.add(phoneDto);

        List <AddressDto> addressList = new ArrayList <AddressDto>();
        AddressDto addressDto = new AddressDto();
        addressDto.setModificationSeq("2");
        addressDto.setAddressIdentifier("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");

        addressList.add(addressDto);

        List <EmailDto> emailList = new ArrayList <EmailDto>();
        EmailDto emailDto = new EmailDto();
        emailDto.setModificationSeq("2");
        emailDto.setEmail("a@a.com");
        emailDto.setEmailType("Primary");
        AddressKey emailKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        emailDto.setEmailKey(emailKey);
        emailList.add(emailDto);

        trxDto.setPhones(phoneList);
        trxDto.setAddresses(addressList);
        trxDto.setEmails(emailList);
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        trxDto.setKey(key);

        List <DomainApiErrorDto> warnings = new ArrayList <DomainApiErrorDto>();
        List <ValidationError> errorList = new ArrayList <>();
        warnings.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        warnings.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        InvestorDetail investorDetail;
        investorDetail = new IndividualDetailImpl();
        ((InvestorDetailImpl)investorDetail).setModificationSeq("1");
        List <Phone> phoneModelList = com.bt.nextgen.api.client.util.ClientDetailDtoConverter.setPhoneListForUpdate(phoneList);
        List <Email> emailModelList = com.bt.nextgen.api.client.util.ClientDetailDtoConverter.setEmailListForUpdate(emailList);
        List <Address> addressModelList = com.bt.nextgen.api.client.util.ClientDetailDtoConverter.setAddressListForUpdate(addressList);
        ((IndividualDetailImpl)investorDetail).setAddresses(addressModelList);
        ((IndividualDetailImpl)investorDetail).setPhones(phoneModelList);
        ((IndividualDetailImpl)investorDetail).setEmails(emailModelList);

        PostalAddress addressResponse = new PostalAddress();
        addressResponse.setUnitNumber("UNIT1");
        addressResponse.setBuildingName("BARANGAROO");
        addressResponse.setFloor("FLOOR1");
        addressResponse.setStreetName("HICKSON");
        addressResponse.setStreetType("ROAD");
        addressResponse.setStreetNumber("123");
        addressResponse.setPostcode("P123");
        addressResponse.setCity("Sydney");
        addressResponse.setState("NSW");

        when(addressService.retrievePostalAddress(anyString(), any(ServiceErrors.class))).thenReturn(addressResponse);

        when(clientService.update(any(InvestorDetail.class),
                any(ClientUpdateCategory.class),
                any(ServiceErrors.class))).thenReturn(investorDetail);

        ((IndividualDetailImpl)investorDetail).setWarnings(errorList);

        when(clientDetailsDtoErrorMapper.map(anyList())).thenReturn(warnings);

        ClientTxnDto responseDto = (ClientTxnDto)clientListDtoService.update(trxDto, serviceErrors);

        ArgumentCaptor<InvestorDetail> captor = ArgumentCaptor.forClass(InvestorDetail.class);
        verify(clientService).update(captor.capture(), any(ClientUpdateCategory.class),
                any(ServiceErrors.class));
        AddressImpl address = (AddressImpl)captor.getValue().getAddresses().get(0);

        assertThat(address.getUnit() , is("UNIT1"));
        assertThat(address.getBuilding() , is("BARANGAROO"));
        assertThat(address.getFloor() , is("FLOOR1"));
        assertThat(address.getStreetName() , is("HICKSON"));
        assertThat(address.getStreetType() , is("ROAD"));
        assertThat(address.getStreetNumber() , is("123"));
        assertThat(address.getPostCode() , is("P123"));
        assertThat(address.getState() , is("NSW"));
        assertThat(address.getSuburb() , is("Sydney"));
        assertThat(address.getCountry() , is("AU"));
    }

    @Test
    public void testUpdateEmailWithContactDetailsinGCM()//
    {
        ClientTxnDto trxDto = new ClientTxnDto();
        trxDto.setUpdatedAttribute(ClientUpdateCategory.EMAILS.getType());
        trxDto.setInvestorTypeUpdated(InvestorType.INDIVIDUAL.name());
        trxDto.setCisKey("990123876598");
        trxDto.setModificationSeq("1");

        List <PhoneDto> phoneList = new ArrayList <PhoneDto>();
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setPreferred(true);
        phoneDto.setCountryCode("1256");
        phoneDto.setNumber("12568975");
        phoneDto.setPhoneType("Primary");
        AddressKey phoneKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        phoneDto.setPhoneKey(phoneKey);
        phoneDto.setModificationSeq("3");
        phoneList.add(phoneDto);

        List <AddressDto> addressList = new ArrayList <AddressDto>();
        AddressDto addressDto = new AddressDto();
        addressDto.setModificationSeq("2");
        addressDto.setAddressIdentifier("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        addressDto.setAddressType(ClientListDtoServiceImpl.RESIDENTIAL);
        addressDto.setGcmAddress(true);

        addressList.add(addressDto);

        List <EmailDto> emailList = new ArrayList <EmailDto>();
        EmailDto emailDto = new EmailDto();
        emailDto.setModificationSeq("2");
        emailDto.setEmail("a@a.com");
        emailDto.setEmailType("Primary");
        AddressKey emailKey = new AddressKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        emailDto.setEmailKey(emailKey);
        emailList.add(emailDto);

        trxDto.setPhones(phoneList);
        trxDto.setAddresses(addressList);
        trxDto.setEmails(emailList);
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("6FD859D7B816FA342D192636F91EB17699DDA972FD96A05F");
        trxDto.setKey(key);

        List <DomainApiErrorDto> warnings = new ArrayList <DomainApiErrorDto>();
        List <ValidationError> errorList = new ArrayList <>();
        warnings.add(new DomainApiErrorDto("errorId", "domain", "reason", "message", DomainApiErrorDto.ErrorType.WARNING));
        warnings.add(new DomainApiErrorDto("errorId2", "domain2", "reason2", "message2", DomainApiErrorDto.ErrorType.WARNING));

        InvestorDetail investorDetail;
        investorDetail = new IndividualDetailImpl();
        ((InvestorDetailImpl)investorDetail).setModificationSeq("1");
        List <Phone> phoneModelList = com.bt.nextgen.api.client.util.ClientDetailDtoConverter.setPhoneListForUpdate(phoneList);
        List <Email> emailModelList = com.bt.nextgen.api.client.util.ClientDetailDtoConverter.setEmailListForUpdate(emailList);
        List <Address> addressModelList = com.bt.nextgen.api.client.util.ClientDetailDtoConverter.setAddressListForUpdate(addressList);
        ((IndividualDetailImpl)investorDetail).setAddresses(addressModelList);
        ((IndividualDetailImpl)investorDetail).setPhones(phoneModelList);
        ((IndividualDetailImpl)investorDetail).setEmails(emailModelList);
        ((InvestorDetailImpl)investorDetail).setBrandSiloId("WPAC");
        when(clientService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(investorDetail);
        when(clientService.update(any(InvestorDetail.class),
            any(ClientUpdateCategory.class),
            any(ServiceErrors.class))).thenReturn(investorDetail);

        ((IndividualDetailImpl)investorDetail).setWarnings(errorList);

        when(customerContactDetailsManagementIntegrationService.updateCustomerInformation(any(CustomerData.class), any(ServiceErrors.class))).thenReturn(true);

        when(clientDetailsDtoErrorMapper.map(anyList())).thenReturn(warnings);

        ClientTxnDto responseDto = (ClientTxnDto)clientListDtoService.update(trxDto, serviceErrors);

        ArgumentCaptor<InvestorDetail> captor = ArgumentCaptor.forClass(InvestorDetail.class);
        verify(clientService).update(captor.capture(), any(ClientUpdateCategory.class),
            any(ServiceErrors.class));
        EmailImpl email = (EmailImpl) captor.getValue().getEmails().get(0);

        assertThat(email.getEmail() , is("a@a.com"));
        assertEquals(request.getAttribute(Constants.REQUEST_ATTR_KEY_BRAND_SILO), "WPAC");
    }

    @Test
    public void testDuplicateEmailsFromABS() {

        List<String> emailsList = new ArrayList<String>();
        clientListDtoService.removeDuplicateEmails(emails);

        for (EmailDto email : emails) {
            emailsList.add(email.getEmail());
        }
        assertEquals(emails.size(), emailsFinal.size());
        assertArrayEquals(emailsList.toArray(), emailsFinal.toArray());
    }


    @Test
    public void testDuplicatePhonesFromABS() {

        List<String> phonesList = new ArrayList<String>();
        clientListDtoService.removeDuplicatePhones(phones);

        for (PhoneDto phone : phones) {
            phonesList.add(phone.getNumber());
        }
        assertEquals(phonesList.size(), phonesFinal.size());
        assertArrayEquals(phonesList.toArray(), phonesFinal.toArray());
    }

    @Test
    public void testfind() {
        IndividualDetail individual = new IndividualDetailImpl();
        ((IndividualDetailImpl) individual).setClientKey(ClientKey.valueOf("54321"));
        ((IndividualDetailImpl) individual).setGcmId("12453");
        ((IndividualDetailImpl) individual).setTitle("Mr");
        ((IndividualDetailImpl) individual).setIdVerificationStatus(IdentityVerificationStatus.Completed);
        ((IndividualDetailImpl) individual).setExemptionReason(ExemptionReason.NO_EXEMPTION);

        when(clientService.loadClientMap(any(ServiceErrors.class))).thenReturn(getClientMap());
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(getAccountMap());
        when(clientService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(individual);
        when(customerLoginManagementIntegrationService.getCustomerUserName(anyString(), any(ServiceErrors.class))).thenReturn("test");
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("7251955D473E456434BD2A5EC24F79A0572C92A1171EA94D");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ClientDto dto = clientListDtoService.find(key, serviceErrors);

        /**
         * input - ClientKey1
         * output -  client1, client2, client5 - related client parent key
         * Accounts - wrapAccount1, wrapAccount2, wrapAccount3, wrapAccount7
         */
        List<AccountDto> accountDtos = dto.getAccounts();
        Assert.assertEquals(4, dto.getAccounts().size());


        Map<String, AccountDto> accountDtoMap = new HashMap<String, AccountDto>();

        if (accountDtos != null && !CollectionUtils.isEmpty(accountDtos)) {
            //accountDtoMap = Lambda.index(accountDtos, on(AccountDto.class).getKey().getAccountId());

            List<String> accountTypes = Lambda.collect(accountDtos, Lambda.on(AccountDto.class).getAccountType());
            Assert.assertTrue(accountTypes.contains("SMSF"));
            Assert.assertTrue(accountTypes.contains("Pension"));
            Assert.assertTrue(accountTypes.contains("Super"));

        }

    }

    @Test
    public void testfindActiveAccounts() {
        IndividualDetail individual = new IndividualDetailImpl();
        ((IndividualDetailImpl) individual).setClientKey(ClientKey.valueOf("54321"));
        ((IndividualDetailImpl) individual).setGcmId("12453");
        ((IndividualDetailImpl) individual).setTitle("Mr");
        ((IndividualDetailImpl) individual).setIdVerificationStatus(IdentityVerificationStatus.Completed);
        ((IndividualDetailImpl) individual).setExemptionReason(ExemptionReason.NO_EXEMPTION);

        when(clientService.loadClientMap(any(ServiceErrors.class))).thenReturn(getClientMap());

        // Setting accountkey7 SUPER account as closed
        Map<AccountKey, WrapAccount> wrapAccountMap = getAccountMap();
        AccountKey accountKey7 = AccountKey.valueOf("56687");
        WrapAccount superWrapAccount = wrapAccountMap.get(accountKey7);
        ((WrapAccountImpl) superWrapAccount).setAccountStatus(AccountStatus.CLOSE);

        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(wrapAccountMap);
        when(clientService.loadClientDetails(any(ClientKey.class), any(ServiceErrors.class))).thenReturn(individual);
        when(customerLoginManagementIntegrationService.getCustomerUserName(anyString(), any(ServiceErrors.class))).thenReturn("test");
        com.bt.nextgen.api.client.model.ClientKey key = new com.bt.nextgen.api.client.model.ClientKey("7251955D473E456434BD2A5EC24F79A0572C92A1171EA94D");
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ClientDto dto = clientListDtoService.find(key, serviceErrors);

        /**
         * input - ClientKey1
         * output -  client1, client2, client5 - related client parent key
         * Accounts - wrapAccount1, wrapAccount2, wrapAccount3, wrapAccount7
         */
        List<AccountDto> accountDtos = dto.getAccounts();
        Assert.assertEquals(3, dto.getAccounts().size());


        Map<String, AccountDto> accountDtoMap = new HashMap<String, AccountDto>();

        if (accountDtos != null && !CollectionUtils.isEmpty(accountDtos)) {
            List<String> accountTypes = Lambda.collect(accountDtos, Lambda.on(AccountDto.class).getAccountType());
            Assert.assertTrue(accountTypes.contains("SMSF"));
            Assert.assertTrue(accountTypes.contains("Pension"));
            Assert.assertFalse(accountTypes.contains("Super"));

        }

    }

    public Map<ClientKey, Client> getClientMap() {
        Map<ClientKey, Client> clientMap = new HashMap<>();
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        ClientKey clientKey1 = ClientKey.valueOf("54321");
        ClientKey clientKey2 = ClientKey.valueOf("67894");
        ClientKey clientKey3 = ClientKey.valueOf("65987");
        ClientKey clientKey4 = ClientKey.valueOf("23659");
        ClientKey clientKey5 = ClientKey.valueOf("36598");
        ClientKey clientKey6 = ClientKey.valueOf("45879");

        ClientImpl client1 = new IndividualImpl();
        ClientImpl client2 = new IndividualImpl();
        ClientImpl client3 = new IndividualImpl();
        ClientImpl client4 = new IndividualImpl();
        ClientImpl client5 = new IndividualImpl();
        ClientImpl client6 = new IndividualImpl();

        /**
         * client1(ClientKey1) -> associateClients1 (clientKey3, clientKey4, clientKey6)
         */
        List<ClientKey> associateClients1 = new ArrayList<>();
        associateClients1.add(clientKey3);
        associateClients1.add(clientKey4);
        associateClients1.add(clientKey6);
        client1.setClientKey(clientKey1);
        client1.setAssociatedPersonKeys(associateClients1);
        client1.setRegistrationOnline(false);
        client1.setLegalForm(InvestorType.INDIVIDUAL);

        /**
         * client2(ClientKey2) -> associateClients2 (clientKey3, clientKey1)
         */
        List<ClientKey> associateClients2 = new ArrayList<>();
        associateClients2.add(clientKey3);
        associateClients2.add(clientKey1);
        client2.setClientKey(clientKey2);
        client2.setAssociatedPersonKeys(associateClients2);
        client2.setRegistrationOnline(false);
        client2.setLegalForm(InvestorType.INDIVIDUAL);


        /**
         * client3(ClientKey3) -> associateClients3 (clientKey3, clientKey6)
         */
        List<ClientKey> associateClients3 = new ArrayList<>();
        associateClients3.add(clientKey6);
        client3.setClientKey(clientKey3);
        client3.setAssociatedPersonKeys(associateClients3);
        client3.setRegistrationOnline(false);
        client3.setLegalForm(InvestorType.INDIVIDUAL);

        /**
         * client4(ClientKey3) -> associateClients4 (none)
         */
        List<ClientKey> associateClients4 = new ArrayList<>();
        client4.setClientKey(clientKey4);
        client4.setRegistrationOnline(true);
        client4.setLegalForm(InvestorType.INDIVIDUAL);

        /**
         * client5(ClientKey5) -> associateClients5 (clientKey1)
         */
        List<ClientKey> associateClients5 = new ArrayList<>();
        associateClients5.add(clientKey1);
        client5.setClientKey(clientKey5);
        client5.setRegistrationOnline(false);
        client5.setAssociatedPersonKeys(associateClients5);
        client5.setLegalForm(InvestorType.INDIVIDUAL);

        /**
         * client6(ClientKey6) -> associateClients6 (clientKey5)
         */
        List<ClientKey> associateClients6 = new ArrayList<>();
        associateClients6.add(clientKey5);
        client6.setClientKey(clientKey6);
        client6.setRegistrationOnline(false);
        client6.setLegalForm(InvestorType.INDIVIDUAL);

        clientMap.put(clientKey1, client1);
        clientMap.put(clientKey2, client2);
        clientMap.put(clientKey3, client3);
        clientMap.put(clientKey4, client4);
        clientMap.put(clientKey5, client5);
        clientMap.put(clientKey6, client6);
        return clientMap;
    }

    public Map<AccountKey, WrapAccount> getAccountMap() {

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

        ClientKey clientKey1 = ClientKey.valueOf("54321");
        ClientKey clientKey2 = ClientKey.valueOf("67894");
        ClientKey clientKey3 = ClientKey.valueOf("65987");
        ClientKey clientKey4 = ClientKey.valueOf("23659");
        ClientKey clientKey5 = ClientKey.valueOf("36598");
        ClientKey clientKey6 = ClientKey.valueOf("45879");


        AccountKey accountKey1 = AccountKey.valueOf("58964");
        AccountKey accountKey2 = AccountKey.valueOf("45698");
        AccountKey accountKey3 = AccountKey.valueOf("25698");
        AccountKey accountKey4 = AccountKey.valueOf("46987");
        AccountKey accountKey5 = AccountKey.valueOf("12698");
        AccountKey accountKey6 = AccountKey.valueOf("56987");
        AccountKey accountKey7 = AccountKey.valueOf("56687");

        WrapAccountImpl wrapAccount1 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount2 = new PensionAccountDetailImpl();
        WrapAccountImpl wrapAccount3 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount4 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount5 = new PensionAccountDetailImpl();
        WrapAccountImpl wrapAccount6 = new WrapAccountImpl();
        WrapAccountImpl wrapAccount7 = new WrapAccountImpl();

        List<ClientKey> accountOwner1 = new ArrayList<>();
        accountOwner1.add(clientKey1);
        List<ClientKey> accountApprover1 = new ArrayList<>();
        accountApprover1.add(clientKey3);
        wrapAccount1.setAccountKey(accountKey1);
        wrapAccount1.setAccountOwners(accountOwner1);
        wrapAccount1.setAccountNumber("236598745");
        wrapAccount1.setApprovers(accountApprover1);
        wrapAccount1.setAccountStructureType(AccountStructureType.SMSF);

        List<ClientKey> accountOwner2 = new ArrayList<>();
        accountOwner2.add(clientKey2);
        wrapAccount2.setAccountKey(accountKey2); //Pension account
        wrapAccount2.setAccountNumber("659874563");
        wrapAccount2.setAccountOwners(accountOwner2);
        wrapAccount2.setAccountStructureType(AccountStructureType.SUPER);


        List<ClientKey> accountOwner3 = new ArrayList<>();
        accountOwner3.add(clientKey1);
        wrapAccount3.setAccountKey(accountKey3);
        wrapAccount3.setAccountNumber("569874521");
        wrapAccount3.setAccountOwners(accountOwner3);
        wrapAccount3.setAccountStructureType(AccountStructureType.SMSF);


        List<ClientKey> accountOwner4 = new ArrayList<>();
        accountOwner4.add(clientKey4);
        wrapAccount4.setAccountKey(accountKey4);
        wrapAccount4.setAccountNumber("986587456");
        wrapAccount4.setAccountOwners(accountOwner4);
        wrapAccount4.setAccountStructureType(AccountStructureType.Company);

        List<ClientKey> accountOwner5 = new ArrayList<>();
        accountOwner5.add(clientKey6);
        wrapAccount5.setAccountKey(accountKey5); //Pension account
        wrapAccount5.setAccountNumber("125547895");
        wrapAccount5.setAccountOwners(accountOwner5);
        wrapAccount5.setAccountStructureType(AccountStructureType.SUPER);


        List<ClientKey> accountOwner6 = new ArrayList<>();
        accountOwner6.add(clientKey3);
        wrapAccount6.setAccountKey(accountKey6);
        wrapAccount6.setAccountNumber("512569874");
        wrapAccount6.setAccountOwners(accountOwner6);
        wrapAccount6.setAccountStructureType(AccountStructureType.SUPER);

        List<ClientKey> accountOwner7 = new ArrayList<>();

        accountOwner7.add(clientKey5);
        wrapAccount7.setAccountKey(accountKey7);
        wrapAccount7.setAccountNumber("545698745");
        wrapAccount7.setAccountOwners(accountOwner7);
        wrapAccount7.setAccountStructureType(AccountStructureType.SUPER);

        accountMap.put(accountKey1, wrapAccount1); //Owner clientKey1
        accountMap.put(accountKey2, wrapAccount2); //Owner clientKey2
        accountMap.put(accountKey3, wrapAccount3); //Owner clientKey1
        accountMap.put(accountKey4, wrapAccount4); //Owner clientKey4
        accountMap.put(accountKey5, wrapAccount5); //Owner clientKey6
        accountMap.put(accountKey6, wrapAccount6); //Owner clientKey3
        accountMap.put(accountKey7, wrapAccount7); //Owner clientKey5

        return accountMap;
    }

}
