package com.bt.nextgen.api.overview.service;


import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.overview.model.AccountOverviewDetailsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.UserCacheService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import net.sf.ehcache.Ehcache;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountOverviewDetailsDtoServiceImpl implements AccountOverviewDetailsDtoService {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserCacheService userCacheService;

    @Override
    public AccountOverviewDetailsDto find(AccountKey key, ServiceErrors serviceErrors) {
        final Cache cache = cacheManager.getCache("com.bt.nextgen.service.avaloq.account.WrapAccountValuation");
        return getCachePopulationDatetime(cache, key);
    }

    @SuppressWarnings("unchecked")
    private AccountOverviewDetailsDto getCachePopulationDatetime(Cache cache, AccountKey key) {
        final Object nativeCache = cache.getNativeCache();
        final AccountOverviewDetailsDto accountOverviewDetailsDto = new AccountOverviewDetailsDto();

        if (nativeCache instanceof net.sf.ehcache.Ehcache) {
            final Ehcache ehCache = (Ehcache) cache.getNativeCache();
            accountOverviewDetailsDto.setCacheLastRefreshedDatetime(DateTime.now());

            if (ehCache != null && ehCache.getSize() > 0) {
                for (Object cacheKeys : ehCache.getKeys()) {
                    setLastCacheRefreshDetails((List<Object>) cacheKeys, key.getAccountId(), ehCache, accountOverviewDetailsDto);
                }
            }
        }
        return accountOverviewDetailsDto;
    }

    private void setLastCacheRefreshDetails(List<Object> cacheKeyList, String accountId, Ehcache ehCache, AccountOverviewDetailsDto accountOverviewDetailsDto) {
        boolean matchesAccountKey = false;
        boolean matchesProfileKey = false;

        for (Object cacheKey : cacheKeyList) {
            if (cacheKey instanceof com.bt.nextgen.service.integration.account.AccountKey) {
                com.bt.nextgen.service.integration.account.AccountKey cacheAccountKey = (com.bt.nextgen.service.integration.account.AccountKey) cacheKey;
                matchesAccountKey = cacheAccountKey.getId().equalsIgnoreCase(EncodedString.toPlainText(accountId));
            }
            if (cacheKey instanceof String) {
                matchesProfileKey = userCacheService.getActiveProfileCacheKey().equalsIgnoreCase((String) cacheKey);
            }
        }
        if (matchesAccountKey && matchesProfileKey) {
            accountOverviewDetailsDto.setCacheLastRefreshedDatetime(new DateTime(ehCache.get(cacheKeyList).getCreationTime()));
        }
    }
}