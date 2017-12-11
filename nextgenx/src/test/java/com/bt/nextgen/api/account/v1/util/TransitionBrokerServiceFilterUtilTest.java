package com.bt.nextgen.api.account.v1.util;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
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
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.CISKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientDetail;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.ClientType;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userinformation.TaxResidenceCountry;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransitionBrokerServiceFilterUtilTest {

    private  WrapAccountImpl account1;
    private WrapAccountImpl account2;
    private WrapAccountImpl account3;
    private AccountKey accountKey1;
    private AccountKey accountKey2;
    private AccountKey accountKey3;
    private Map<AccountKey, WrapAccount> accountMap;
    private HashMap brokerWrapperMap;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;

    @Before
    public void setUp()
    {
        account1 = new WrapAccountImpl();
        account2 = new WrapAccountImpl();
        account3 = new WrapAccountImpl();
        accountKey1 = AccountKey.valueOf("34523654");
        accountKey2 = AccountKey.valueOf("64433");
        accountKey3 = AccountKey.valueOf("56734");
        account1.setAccountKey(accountKey1);
        account1.setProductKey(ProductKey.valueOf("1234"));
        account1.setAccountName("Tom Peters");
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
        accountMap =  new HashMap<>();
        accountMap.put(accountKey1, account1);
        accountMap.put(accountKey2, account2);
        accountMap.put(accountKey3, null);

        BrokerUser brokerUser = getBrokerUser();
        BrokerWrapper brokerWrapper = new BrokerWrapperImpl(BrokerKey.valueOf("66773"), brokerUser, true, "");
        brokerWrapperMap =  new HashMap();
        brokerWrapperMap.put(BrokerKey.valueOf("66773"), brokerWrapper);
    }

    @Test
    public void getAdviserList() throws Exception {
        when(brokerIntegrationService.getAdviserBrokerUser(anyListOf(BrokerKey.class), any(ServiceErrors.class))).thenReturn(brokerWrapperMap);
        ServiceErrors serviceErrors =  new ServiceErrorsImpl();
        TransitionBrokerServiceFilterUtil transitionBrokerServiceFilterUtil =  new TransitionBrokerServiceFilterUtil(brokerIntegrationService);
        Set<String> adviserList = transitionBrokerServiceFilterUtil.getAdviserList(accountMap, serviceErrors);
        assertThat(adviserList, is(notNullValue()));
        assertThat(adviserList.iterator().next(), is("FirstName LastName"));
    }

    private BrokerUser getBrokerUser() {
        BrokerUser brokerUser = new BrokerUser() {
            @Override
            public Collection<BrokerRole> getRoles() {
                return null;
            }

            @Override
            public boolean isRegisteredOnline() {
                return false;
            }

            @Override
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
            public CISKey getCISKey() {
                return null;
            }

            @Override
            public String getFirstName() {
                return null;
            }

            @Override
            public String getMiddleName() {
                return null;
            }

            @Override
            public String getLastName() {
                return null;
            }

            @Override
            public String getCorporateName() {
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
            public Collection<AccountKey> getWrapAccounts() {
                return null;
            }

            @Override
            public Collection<ClientDetail> getRelatedPersons() {
                return null;
            }

            @Override
            public DateTime getCloseDate() {
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
            public InvestorType getLegalForm() {
                return null;
            }

            @Override
            public ClientType getClientType() {
                return null;
            }

            @Override
            public List<Address> getAddresses() {
                return null;
            }

            @Override
            public List<Email> getEmails() {
                return null;
            }

            @Override
            public List<Phone> getPhones() {
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
            public String getFullName() {
                return "FirstName LastName";
            }

            @Override
            public ClientKey getClientKey() {
                return null;
            }

            @Override
            public void setClientKey(ClientKey clientKey) {

            }

            @Override
            public JobKey getJob() {
                return null;
            }

            @Override
            public String getProfileId() {
                return null;
            }
        };

        return brokerUser;
    }

}