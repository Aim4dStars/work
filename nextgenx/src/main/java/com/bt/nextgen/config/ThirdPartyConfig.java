package com.bt.nextgen.config;

import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistoryIntegrationService;
import com.bt.nextgen.service.integration.base.MigrationAttribute;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by M044576 on 10/10/2017.
 */
@Configuration
@ComponentScan("com.bt.nextgen.service")
//@DependsOn(value = {"CacheContributionHistoryIntegrationServiceImpl", "ContributionHistoryIntegrationServiceImpl", "WrapContributionHistoryIntegrationServiceImpl"})
@Profile("WrapOffThreadImplementation")
public class ThirdPartyConfig {

    @Autowired
    @Qualifier("CacheContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService cacheContributionHistoryIntegrationServiceImpl;

    @Autowired
    @Qualifier("ContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService contributionHistoryIntegrationServiceImpl;

    @Autowired
    @Qualifier("WrapContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService wrapContributionHistoryIntegrationService;

    /**
     * Method that will hold all the objects for the particular calling attribute.
     * @return
     */
    @Bean
    public Map<MigrationAttribute, Map<String, Object>> attributeMap()
    {
        Map<MigrationAttribute, Map<String, Object>> attributeMap = new HashMap<>();

        Map<String, Object> contributionHistoryObjectMap = new HashMap<>();
        contributionHistoryObjectMap.put(Attribute.CACHE, cacheContributionHistoryIntegrationServiceImpl);
        contributionHistoryObjectMap.put(Attribute.DEFAULT, contributionHistoryIntegrationServiceImpl);
        contributionHistoryObjectMap.put(SystemType.WRAP.getName(), wrapContributionHistoryIntegrationService);
        attributeMap.put(MigrationAttribute.WRAP_CONTRIBUTION_HISTORY, contributionHistoryObjectMap);

        return attributeMap;
    }

}
