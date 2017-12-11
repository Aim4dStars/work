package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountBalanceImpl;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.integration.account.AccountBalance;
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
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.broker.Broker;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountFilterUtilTest {

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ProductIntegrationService productIntegrationService;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    private AccountFilterUtil accountFilterUtil;

    private List<ApiSearchCriteria> criteriaList;

    ServiceErrors serviceErrors;
    Broker broker;

    private String queryString = "[{\"prop\":\"portfolioValue\",\"op\":\"~<\",\"val\":\"10000\",\"type\":\"number\"}," +
            "{\"prop\":\"portfolioValue\",\"op\":\"<\",\"val\":\"30000\",\"type\":\"number\"}," +
            "{\"prop\":\"availableCash\",\"op\":\"~<\",\"val\":\"10000\",\"type\":\"number\"}," +
            "{\"prop\":\"availableCash\",\"op\":\"<\",\"val\":\"40000\",\"type\":\"number\"}," +
            "{\"prop\":\"product\",\"op\":\"=\",\"val\":\"White Label 1 35d1b65704184ae3b87799400f7ab93c\",\"type\":\"string\"}," +
            "{\"prop\":\"accountStatus\",\"op\":\"=\",\"val\":\"Active\",\"type\":\"string\"}," +
            "{\"op\":\"c\",\"prop\":\"accountName\",\"type\":\"string\",\"val\":\"Mi\"}]";

    @Before
    public void setup(){
        serviceErrors = new FailFastErrorsImpl();
        criteriaList = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, queryString);
        broker = Mockito.mock(Broker.class);
        Mockito.when(broker.isDirectInvestment()).thenReturn(true);
        BrokerKey brokerKey1 = BrokerKey.valueOf("68747");

        BrokerUser brokerUser = new BrokerUser()
        {
            @Override
            public Collection<BrokerRole> getRoles()
            {
                return null;
            }

            @Override
            public boolean isRegisteredOnline()
            {
                return false;
            }

            @Override
            public String getPracticeName()
            {
                return null;
            }

            @Override
            public String getEntityId()
            {
                return null;
            }

            @Override
            public DateTime getReferenceStartDate() {
                return null;
            }

            @Override
            public String getFirstName()
            {
                return "Fperson-120_2682";
            }


            @Override
            public String getMiddleName()
            {
                return null;
            }

            @Override
            public String getLastName()
            {
                return "Lperson-120_2682";
            }

            public String getCustomerId()
            {
                return null;
            }

            public UserKey getCustomerKey()
            {
                return null;
            }


            @Override
            public Collection <AccountKey> getWrapAccounts()
            {
                return null;
            }

            @Override
            public Collection <ClientDetail> getRelatedPersons()
            {
                return null;
            }

            @Override
            public List <Email> getEmails()
            {
                return null;
            }

            @Override
            public List <Phone> getPhones()
            {
                return null;
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
            public DateTime getCloseDate()
            {
                return null;
            }

            @Override
            public String getFullName()
            {
                return null;
            }

            @Override
            public ClientType getClientType()
            {
                return null;
            }

            @Override
            public List <Address> getAddresses()
            {
                return null;
            }

            @Override
            public InvestorType getLegalForm()
            {
                return null;
            }

            @Override
            public ClientKey getClientKey()
            {
                return null;
            }

            @Override
            public void setClientKey(ClientKey personId)
            {
            }

            @Override
            public JobKey getJob()
            {
                return null;
            }
            @Override
            public String getProfileId()
            {
                return null;
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

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();
        Map<ProductKey, Product> productMap = new HashMap<>();

        ProductKey productKey1 = ProductKey.valueOf("41422");
        ProductImpl product2 = new ProductImpl();
        product2.setProductKey(productKey1);
        product2.setProductName("White Label 1 35d1b65704184ae3b87799400f7ab93c");
        productMap.put(productKey1, product2);

        WrapAccountImpl account1 = new WrapAccountImpl();
        WrapAccountImpl account2 = new WrapAccountImpl();
        WrapAccountImpl account3 = new WrapAccountImpl();
        AccountKey accountKey1 = AccountKey.valueOf("74611");
        AccountKey accountKey2 = AccountKey.valueOf("74643");
        AccountKey accountKey3 = AccountKey.valueOf("11263");
        account1.setAccountKey(accountKey1);
        account1.setProductKey(productKey1);
        account1.setAccountName("Michael Tonini");
        account1.setAccountNumber("120011366");
        account1.setAdviserPositionId(brokerKey1);
        account1.setAccountStructureType(AccountStructureType.Individual);
        account1.setAccountStatus(AccountStatus.ACTIVE);

        account3.setAccountKey(accountKey2);
        account3.setAdviserPositionId(brokerKey1);
        account3.setAccountName("John Cooper");
        account3.setAccountNumber("120011366");
        account3.setProductKey(productKey1);
        account3.setAccountStructureType(AccountStructureType.Individual);
        account3.setAccountStatus(AccountStatus.ACTIVE);

        account2.setAccountKey(accountKey3);
        account2.setAccountName("Oniston Pty Limited - 01");
        account2.setAdviserPersonId(ClientKey.valueOf("1234"));
        account2.setAdviserPositionId(brokerKey1);
        account2.setAccountNumber("120000005");
        account2.setProductKey(productKey1);
        account2.setAccountStructureType(AccountStructureType.Individual);
        account2.setAccountStatus(AccountStatus.ACTIVE);

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

        List <AccountBalance> accountBalanceList = new ArrayList <>();
        accountBalanceList.add(accountBalance1);
        accountBalanceList.add(accountBalance2);
        accountBalanceList.add(accountBalance3);

        accountBalanceMap.put(accountKey1, accountBalance1);
        accountBalanceMap.put(accountKey2, accountBalance2);
        accountBalanceMap.put(accountKey3, accountBalance3);

        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(brokerKey1, brokerUser, true, "");
        HashMap brokerWrapperMap = new HashMap<>();
        brokerWrapperMap.put(brokerKey1, brokerWrapper);

        accountFilterUtil = new AccountFilterUtil(accountMap, accountBalanceMap, productMap, brokerIntegrationService);

        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);
        Mockito.when(accountService.loadAccountBalancesMap(Mockito.any(ServiceErrors.class))).thenReturn(accountBalanceMap);
        when(brokerIntegrationService.getAdviserBrokerUser(anyListOf(BrokerKey.class), any(ServiceErrors.class)))
                .thenReturn(brokerWrapperMap);
        Mockito.when(productIntegrationService.loadProductsMap(Mockito.any(ServiceErrors.class)))
                .thenReturn(productMap);
    }

    @Test
    public void searchAccountDataTest(){
        List<AccountDto> accounts = accountFilterUtil.search(criteriaList, serviceErrors);
        Assert.assertEquals(1, accounts.size());

        AccountDto wrapAccountDto = accounts.get(0);

        Assert.assertEquals(wrapAccountDto.getAccountName(),"Michael Tonini");
        Assert.assertEquals("74611", new EncodedString(wrapAccountDto.getKey().getAccountId()).plainText());
    }
}
