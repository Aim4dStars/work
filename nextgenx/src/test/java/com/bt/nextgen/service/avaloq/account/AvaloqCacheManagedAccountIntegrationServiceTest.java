package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;

/**
 * Created by L062329 on 7/04/2015.
 */
public class AvaloqCacheManagedAccountIntegrationServiceTest extends BaseSecureIntegrationTest {

    @Autowired
    private AvaloqCacheManagedAccountIntegrationService avaloqCacheAccountIntegrationService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @SecureTestContext
    public void testLoadWrapAccounts() throws Exception {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map <AccountKey, WrapAccount>  accountMap = avaloqCacheAccountIntegrationService.loadWrapAccounts(serviceErrors);
        Assert.assertThat(accountMap.size(), Is.is(16));
        WrapAccount account = accountMap.get(AccountKey.valueOf("74611"));
        Collection<ClientKey> approvers = account.getApprovers();
        Assert.assertThat(approvers.size(), Is.is(1));
        Assert.assertThat(approvers.contains(ClientKey.valueOf("44489")) ,Is.is(true));
    }

    @Ignore // I am a bad test case that fails to set up the state for my execution.
    @SecureTestContext
    public void testClearAccountListCache() throws Exception {
        avaloqCacheAccountIntegrationService.clearAccountListCache();
        Cache cache = cacheManager.getCache(AvaloqCacheManagedAccountIntegrationService.ACCOUNT_LIST_CACHE);
        Assert.assertThat(cache.getSize(), Is.is(0));
    }
}