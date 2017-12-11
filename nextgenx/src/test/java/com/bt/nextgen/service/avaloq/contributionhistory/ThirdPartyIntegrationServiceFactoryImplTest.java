package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationService;
import com.bt.nextgen.service.integration.base.MigrationAttribute;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ThirdPartyIntegrationServiceFactoryImplTest {

    @InjectMocks
    ThirdPartyIntegrationServiceFactoryImpl wrapContributionHistoryIntegrationServiceFactoryImpl;

    @Mock
    @Qualifier("CacheContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService cacheContributionHistoryIntegrationServiceImpl;

    @Mock
    @Qualifier("ContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService contributionHistoryIntegrationServiceImpl;

    @Mock
    @Qualifier("WrapContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService wrapContributionHistoryIntegrationService;

    @Mock
    @Qualifier("ThirdPartyAvaloqAccountIntegrationService")
    private AvaloqAccountIntegrationService avaloqAccountIntegrationService;

    @Mock
    Map<MigrationAttribute, Map<String, Object>> attributeMap;

    @Before
    public void setup()
    {
        Map<String, Object> contributionHistoryObjectMap = new HashMap<>();
        contributionHistoryObjectMap.put(Attribute.CACHE, cacheContributionHistoryIntegrationServiceImpl);
        contributionHistoryObjectMap.put(Attribute.DEFAULT, contributionHistoryIntegrationServiceImpl);
        contributionHistoryObjectMap.put(SystemType.WRAP.getName(), wrapContributionHistoryIntegrationService);

        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        when(avaloqAccountIntegrationService.getThirdPartySystemDetails(any(AccountKey.class),
                any(ServiceErrors.class))).thenReturn(thirdPartyDetails);
        when(attributeMap.get(any(MigrationAttribute.class))).thenReturn(contributionHistoryObjectMap);
    }

    @Test
    public void getContributionHistoryIntegrationServiceInstance() {
        ContributionHistoryIntegrationService instance = wrapContributionHistoryIntegrationServiceFactoryImpl.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, "", null);
        Assert.assertEquals(instance,contributionHistoryIntegrationServiceImpl);

    }

    @Test
    public void getWrapContributionHistoryIntegrationServiceInstance() {
        ContributionHistoryIntegrationService instance = wrapContributionHistoryIntegrationServiceFactoryImpl.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, "External", AccountKey.valueOf("12345678"));
        Assert.assertEquals(instance,wrapContributionHistoryIntegrationService);

    }

    @Test
    public void getCacheContributionHistoryIntegrationServiceInstance() {
        ContributionHistoryIntegrationService instance = wrapContributionHistoryIntegrationServiceFactoryImpl.getInstance(ContributionHistoryIntegrationService.class, MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, "CACHE", null);
        Assert.assertEquals(instance,cacheContributionHistoryIntegrationServiceImpl);

    }

}
