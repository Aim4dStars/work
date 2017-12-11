package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.btfin.panorama.core.security.avaloq.AvaloqBankingAuthorityService;
import com.bt.nextgen.service.avaloq.UserCacheService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by L067218 on 12/08/2016.
 */
@Service("CacheContributionHistoryIntegrationServiceImpl")
public class CacheContributionHistoryIntegrationServiceImpl extends ContributionHistoryIntegrationServiceImpl {
    @Resource(name = "userDetailsService")
    public AvaloqBankingAuthorityService userProfileService;

    @Autowired
    public UserCacheService userCacheService;

    @Override
    @Cacheable(key = "{#accountKey, #root.target.getActiveProfileCacheKey()}", value = "com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory")
    public ContributionHistory getContributionHistory(AccountKey accountKey, DateTime financialYearStartDate,
                                                      DateTime financialYearEndDate) {
        return super.getContributionHistory(accountKey, financialYearStartDate, financialYearEndDate);
    }

    public String getActiveProfileCacheKey() {
        return userCacheService.getActiveProfileCacheKey();
    }

}
