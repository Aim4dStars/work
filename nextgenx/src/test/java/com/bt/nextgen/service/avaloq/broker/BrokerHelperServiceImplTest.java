package com.bt.nextgen.service.avaloq.broker;


import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.AccountStatus;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by l078480 on 21/07/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class BrokerHelperServiceImplTest {

    @InjectMocks
    BrokerHelperServiceImpl brokerHelperServiceImpl = new BrokerHelperServiceImpl();

    @Mock
    private AccountIntegrationService avaloqAccountIntegrationService;
    ServiceErrors serviceErrors;

    @Mock
    private BrokerIntegrationService brokerIntegrationService;






    @Test
    public void testBrokerlist(){

        Map<AccountKey, WrapAccount> accountMaps = getAccountKeyWrapAccountMap();

        serviceErrors = new FailFastErrorsImpl();
        when(avaloqAccountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMaps);
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        Set<Broker> brokerList=brokerHelperServiceImpl.getDealerGroupsforInvestor(serviceErrors);
        Assert.assertEquals(1, brokerList.size());

    }

    @Test
    public void testBrokerBranSilo(){

        Map<AccountKey, WrapAccount> accountMaps = getAccountKeyWrapAccountMap();
        serviceErrors = new FailFastErrorsImpl();
        when(avaloqAccountIntegrationService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMaps);
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));
        broker.setBrandSilo("WPAC");
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        String brandsilo=brokerHelperServiceImpl.getBrandSiloForInvestor(serviceErrors);
        assertThat(brandsilo,is("WPAC"));

    }

   @Test
    public void getBrandSiloForIntermediary(){
        BrokerImpl broker = new BrokerImpl(BrokerKey.valueOf("66773"), BrokerType.ADVISER);
        broker.setDealerKey(BrokerKey.valueOf("45677"));
        broker.setBrandSilo("WPAC");
        broker.setDealerKey(BrokerKey.valueOf("1234"));
        List<Broker> brokerList = new ArrayList<Broker>();
        brokerList.add(broker);
        JobProfileIdentifier jobProfileIdentifier= new BrokerUserImpl(UserKey.valueOf("201601509"), JobKey.valueOf("85062"));
        when(brokerIntegrationService.getBrokersForJob(any(JobProfileIdentifier.class), any(ServiceErrors.class))).thenReturn(brokerList);
        when(brokerIntegrationService.getBroker(any(BrokerKey.class), any(ServiceErrors.class))).thenReturn(broker);
        String brandsilo=brokerHelperServiceImpl.getBrandSiloForIntermediary(jobProfileIdentifier,serviceErrors);
        assertThat(brandsilo,is("WPAC"));
    }

    private Map<AccountKey, WrapAccount> getAccountKeyWrapAccountMap() {
        Map<AccountKey, WrapAccount> accountMaps = new HashMap<>();
        WrapAccountImpl account1 = new WrapAccountImpl();
        WrapAccountImpl account2 = new WrapAccountImpl();
        WrapAccountImpl account3 = new WrapAccountImpl();
        AccountKey accountKey1 = AccountKey
                .valueOf("74611");
        AccountKey accountKey2 = AccountKey
                .valueOf("74643");
        AccountKey accountKey3 = AccountKey
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

        accountMaps.put(accountKey1, account1);
        accountMaps.put(accountKey2, account2);
        accountMaps.put(accountKey3, account3);
        return accountMaps;
    }


}
