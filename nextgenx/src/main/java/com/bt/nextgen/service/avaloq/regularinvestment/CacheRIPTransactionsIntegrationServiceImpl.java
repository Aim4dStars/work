package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.regularinvestment.RIPTransactionsIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cachedAvaloqRIPTransactionsIntegrationService")
public class CacheRIPTransactionsIntegrationServiceImpl extends AvaloqRIPTransactionsIntegrationServiceImpl
{
    @Autowired
    public UserCacheService userCacheService;

    @Override
    @Cacheable(key = "{#accountKey, #root.target.getActiveProfileCacheKey()}", value = "com.bt.nextgen.service.avaloq.regularinvestment.RegularInvestmentPlans")
    public List<RegularInvestmentTransaction> loadRegularInvestments(AccountKey accountKey, ServiceErrors serviceErrors)
    {
        return super.loadRegularInvestments(accountKey, serviceErrors);
    }

    public String getActiveProfileCacheKey()
    {
        return userCacheService.getActiveProfileCacheKey();
    }
}
