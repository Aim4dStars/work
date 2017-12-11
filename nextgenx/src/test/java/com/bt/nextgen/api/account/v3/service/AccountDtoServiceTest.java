/**
 * 
 */
package com.bt.nextgen.api.account.v3.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bt.nextgen.service.avaloq.userinformation.UserExperience;
import com.btfin.panorama.service.integration.broker.BrokerType;
import com.btfin.panorama.service.integration.broker.ExternalBrokerKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.broker.BrokerRole;
import com.bt.nextgen.service.integration.broker.BrokerUser;
import com.bt.nextgen.service.integration.broker.BrokerWrapper;
import com.bt.nextgen.service.integration.broker.BrokerWrapperImpl;
import com.bt.nextgen.service.integration.domain.Address;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Gender;
import com.bt.nextgen.service.integration.domain.InvestorType;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;


@RunWith(MockitoJUnitRunner.class)
public class AccountDtoServiceTest {

    @InjectMocks
    AccountDtoService accountDtoService = new AccountDtoServiceImpl();

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Mock
    private BrokerHelperService brokerHelperService;

    @Mock
    private UserProfileService userProfileService;

    ServiceErrors serviceErrors;
    UserProfile activeProfile;
    BrokerUser brokerUser;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        serviceErrors = new FailFastErrorsImpl();
        activeProfile = getProfile(JobRole.ADVISER, "job id 1", "client1");
        final List<Address> addresses = new ArrayList<>();
        final List<Phone> phones = new ArrayList<>();
        final List<Email> emails = new ArrayList<>();

        brokerUser = new BrokerUser() {

            @Override
            public Collection<BrokerRole> getRoles() {
                return null;
            }

            @Override
            public boolean isRegisteredOnline() {
                return false;
            }

            @Deprecated
            public String getPracticeName() {
                return null;
            }

            @Override
            public String getEntityId() {
                return null;
            }

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
            public String getFirstName() {
                return "person-120_505";
            }

            @Override
            public String getMiddleName() {
                return "person-120_505";
            }

            @Override
            public String getLastName() {
                return "person-120_505";
            }

            @Override
            public String getBankReferenceId() {
                return null;
            }

            @Override
            public UserKey getBankReferenceKey() {
                return null;
            }

            @Override
            public Collection<com.bt.nextgen.service.integration.account.AccountKey> getWrapAccounts() {
                return null;
            }

            @Override
            public Collection<ClientDetail> getRelatedPersons() {
                return null;
            }

            @Override
            public List<Email> getEmails() {
                return emails;
            }

            @Override
            public List<Phone> getPhones() {
                return phones;
            }

            @Override
            public int getAge() {
                return 0;
            }

            @Override
            public Gender getGender() {
                return null;
            }

            @Override
            public DateTime getDateOfBirth() {
                return null;
            }

            @Override
            public boolean isRegistrationOnline() {
                return false;
            }

            @Override
            public String getTitle() {
                return null;
            }

            @Override
            public String getSafiDeviceId() {
                return null;
            }

            @Override
            public String getModificationSeq() {
                return null;
            }

            @Override
            public String getGcmId() {
                return null;
            }

            @Override
            public DateTime getOpenDate() {
                return null;
            }

            @Override
            public DateTime getCloseDate() {
                return null;
            }

            @Override
            public String getFullName() {
                return null;
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                return addresses;
            }

            @Override
            public InvestorType getLegalForm() {
                return null;
            }

            @Override
            public ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(ClientKey personId) {

            }

            @Override
            public JobKey getJob() {
                return null;
            }

            @Override
            public String getProfileId() {
                return null;
            }

            @Override
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public List<TaxResidenceCountry> getTaxResidenceCountries() {
                return null;
            }

            @Override
            public String getBrandSiloId() {
                return null;
            }

            @Override
            public String getCorporateName() {
                return null;
            }
        };
    }

    @Test
    public void searchAccountTest() {

        String accountSearchQueryString = "[{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"}]";
        Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap = new HashMap<>();
        WrapAccountImpl account1 = new WrapAccountImpl();
        WrapAccountImpl account2 = new WrapAccountImpl();
        WrapAccountImpl account3 = new WrapAccountImpl();
        com.bt.nextgen.service.integration.account.AccountKey accountKey1 = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf("74611");
        com.bt.nextgen.service.integration.account.AccountKey accountKey2 = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf("74643");
        com.bt.nextgen.service.integration.account.AccountKey accountKey3 = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf("11263");
        account1.setAccountKey(accountKey1);
        account1.setProductKey(ProductKey.valueOf("1234"));
        account1.setAccountName("Michael Tonini");
        account1.setAccountNumber("120011366");
        account1.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account1.setAccountStructureType(AccountStructureType.Individual);
        account1.setAccountStatus(AccountStatus.ACTIVE);

        account3.setAccountKey(accountKey2);
        account3.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account3.setAccountName("John Cooper");
        account3.setAccountNumber("120011366");
        account3.setProductKey(ProductKey.valueOf("1234"));
        account3.setAccountStructureType(AccountStructureType.Individual);
        account3.setAccountStatus(AccountStatus.ACTIVE);

        account2.setAccountKey(accountKey3);
        account2.setAccountName("Oniston Pty Limited - 01");
        account2.setAdviserPersonId(ClientKey.valueOf("1234"));
        account2.setAdviserPositionId(BrokerKey.valueOf("66773"));
        account2.setAccountNumber("120000005");
        account2.setProductKey(ProductKey.valueOf("1234"));
        account2.setAccountStructureType(AccountStructureType.Individual);
        account2.setAccountStatus(AccountStatus.ACTIVE);

        accountMap.put(accountKey1, account1);
        accountMap.put(accountKey2, account2);
        accountMap.put(accountKey3, account3);

        List<ApiSearchCriteria> criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION,
                accountSearchQueryString);

        Map<BrokerKey, BrokerWrapper> brokerWrapperMap = new HashMap<>();
        brokerWrapperMap.put(BrokerKey.valueOf("66773"),new BrokerWrapperImpl(BrokerKey.valueOf("66773"), brokerUser, true, ""));
        when(accountService.loadWrapAccountWithoutContainers(serviceErrors)).thenReturn(accountMap);
        when(userProfileService.getActiveProfile()).thenReturn(activeProfile);
        when(brokerIntegrationService.getAdviserBrokerUser(anyListOf(BrokerKey.class), (ServiceErrors) anyObject())).thenReturn(
                brokerWrapperMap);
        //when(brokerHelperService.isDirectInvestor((WrapAccount) anyObject(), (ServiceErrors) anyObject())).thenReturn(false);
        ProductImpl product = new ProductImpl();
        product.setProductKey(ProductKey.valueOf("1234"));
        product.setProductName("productName");
        Map<ProductKey, Product> mapOfProducts = new HashMap<>();
        mapOfProducts.put(ProductKey.valueOf("1234"), product);
        Mockito.when(productIntegrationService.getProductDetail(any(ProductKey.class), any(ServiceErrors.class)))
                .thenReturn(product);
        Mockito.when(productIntegrationService.loadProductsMap( any(ServiceErrors.class))).thenReturn(mapOfProducts);

        List<AccountDto> accounts = accountDtoService.getFilteredValue("Mi", criteriaList, serviceErrors);
        Assert.assertEquals(1, accounts.size());

        AccountDto wrapAccountDto = accounts.get(0);

        Assert.assertEquals(wrapAccountDto.getAccountName(), "Michael Tonini");
        Assert.assertEquals("74611", new ConsistentEncodedString(wrapAccountDto.getKey().getAccountId()).plainText());
    }

    @Test
    public void testFindAll() {
        Map<AccountKey, WrapAccount> wrapAccountMap = getWrapAccountMap();
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(wrapAccountMap);
        Map<ProductKey, Product> productsMap = getProductsMap();
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(productsMap);
        Map<BrokerKey, BrokerWrapper> brokerMap = getBrokerMap();
        when(brokerIntegrationService.getAdviserBrokerUser(anyList(), any(ServiceErrors.class))).thenReturn(brokerMap);
        List<AccountDto> response = accountDtoService.findAll(serviceErrors);
        Assert.assertEquals(response.size(), 1);
    }

    @Test
    public void testSearch() {
        Map<AccountKey, WrapAccount> wrapAccountMap = getWrapAccountMap();
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(wrapAccountMap);
        Map<ProductKey, Product> productsMap = getProductsMap();
        when(productIntegrationService.loadProductsMap(any(ServiceErrors.class))).thenReturn(productsMap);
        Map<BrokerKey, BrokerWrapper> brokerMap = getBrokerMap();
        when(brokerIntegrationService.getAdviserBrokerUser(anyList(), any(ServiceErrors.class))).thenReturn(brokerMap);
        List<ApiSearchCriteria> apiCriteria = Arrays.asList(new ApiSearchCriteria("accountStatus", "Active"));
        List<AccountDto> response = accountDtoService.search(apiCriteria, serviceErrors);
        Assert.assertEquals(response.size(), 1);
    }

    private Map<BrokerKey, BrokerWrapper> getBrokerMap() {
        Map<BrokerKey, BrokerWrapper> brokerMap = new HashMap<>();
        brokerMap.put(BrokerKey.valueOf("brok1"), getBrokerWrapper());
        return brokerMap;
    }

    private BrokerWrapper getBrokerWrapper() {
    	Broker broker = mock(Broker.class);
        BrokerWrapper brokerWrapper = mock(BrokerWrapper.class);
        when(brokerWrapper.getBrokerUser()).thenReturn(null);
        when(brokerWrapper.isDirectInvestment()).thenReturn(false);
        return brokerWrapper;
    }

    private Map<ProductKey, Product> getProductsMap() {
        Map<ProductKey, Product> productsMap = new HashMap<>();
        productsMap.put(ProductKey.valueOf("prod1"), getProduct("prod1"));
        return productsMap;
    }

    private Product getProduct(String productId) {
        Product product = mock(Product.class);
        when(product.getProductKey()).thenReturn(ProductKey.valueOf(productId));
        when(product.getProductName()).thenReturn("Product 1");
        return product;
    }

    private Map<AccountKey, WrapAccount> getWrapAccountMap() {
        Map<AccountKey, WrapAccount> wrapAccountMap = new HashMap<>();
        wrapAccountMap.put(AccountKey.valueOf("123"), getWrapAccount("123"));
        return wrapAccountMap;
    }

    private WrapAccount getWrapAccount(String accountId) {
        WrapAccount wrapAccount = mock(WrapAccount.class);
        when(wrapAccount.getProductKey()).thenReturn(ProductKey.valueOf("prod1"));
        when(wrapAccount.getAccountKey()).thenReturn(AccountKey.valueOf(accountId));
        when(wrapAccount.getAccountStructureType()).thenReturn(AccountStructureType.Individual);
        when(wrapAccount.getAccountStatus()).thenReturn(AccountStatus.ACTIVE);
        when(wrapAccount.getAdviserPositionId()).thenReturn(BrokerKey.valueOf("brok1"));
        return wrapAccount;
    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

}
