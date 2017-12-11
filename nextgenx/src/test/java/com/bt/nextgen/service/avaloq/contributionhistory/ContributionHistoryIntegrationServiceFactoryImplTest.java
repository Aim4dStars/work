package com.bt.nextgen.service.avaloq.contributionhistory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

@RunWith(MockitoJUnitRunner.class)
public class ContributionHistoryIntegrationServiceFactoryImplTest {

    @InjectMocks
    ContributionHistoryIntegrationServiceFactoryImpl contributionHistoryIntegrationServiceFactoryImpl;

    @Mock
    @Qualifier("CacheContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService cacheContributionHistoryIntegrationServiceImpl;

    @Mock
    @Qualifier("ContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService contributionHistoryIntegrationServiceImpl;


    @Test
    public void getContributionHistoryIntegrationServiceInstance() {
        ContributionHistoryIntegrationService instance = contributionHistoryIntegrationServiceFactoryImpl.getInstance("");
        Assert.assertEquals(instance,contributionHistoryIntegrationServiceImpl);

    }

    @Test
    public void getCacheContributionHistoryIntegrationServiceInstance() {
        ContributionHistoryIntegrationService instance = contributionHistoryIntegrationServiceFactoryImpl.getInstance("CACHE");
        Assert.assertEquals(instance,cacheContributionHistoryIntegrationServiceImpl);

    }
}
