package com.bt.nextgen.service.avaloq.contributionhistory;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by L067218 on 16/08/2016.
 */
@Service("ContributionHistoryIntegrationServiceFactoryImpl")
public class ContributionHistoryIntegrationServiceFactoryImpl implements ContributionHistoryIntegrationServiceFactory {
    @Autowired
    @Qualifier("CacheContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService cacheContributionHistoryIntegrationServiceImpl;

    @Autowired
    @Qualifier("ContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService contributionHistoryIntegrationServiceImpl;


    public ContributionHistoryIntegrationService getInstance(String type)
    {
        if (!StringUtils.isEmpty(type) && "CACHE".equalsIgnoreCase(type))
        {
            return cacheContributionHistoryIntegrationServiceImpl;
        }
        else
        {
            return contributionHistoryIntegrationServiceImpl;
        }
    }
}
