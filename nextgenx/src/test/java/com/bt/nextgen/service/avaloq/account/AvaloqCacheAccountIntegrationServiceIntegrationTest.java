package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.bt.nextgen.service.integration.ips.IpsKey;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

public class AvaloqCacheAccountIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	AvaloqCacheManagedAccountIntegrationService avaloqCacheAccountIntegrationService;

	@Autowired
	private CacheManager cacheManager;

	@Test
	@SecureTestContext
	public void testloadSubAccountsDetails() throws Exception
	{

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Map <AccountKey, List <SubAccount>> response = avaloqCacheAccountIntegrationService.loadSubAccounts(serviceErrors);
		verifySubAccountResponse(response, 5);

	}

	protected Map <AccountKey, List <SubAccount>> verifySubAccountResponse(Map <AccountKey, List <SubAccount>> response,
		int totalUnreadClientNotifications)
	{
		int i = 0;
		assertThat(response, is(notNullValue()));
		assertThat(response.size(), is(totalUnreadClientNotifications));
		AccountKey key = AccountKey.valueOf("74611");
		List <SubAccount> list1 = response.get(key);
		assertThat(list1, is(notNullValue()));
		assertThat(list1.size(), is(2));
		for (SubAccount subAccount : list1)
		{
			if (i == 0)
				assertThat(subAccount.getSubAccountKey().getId(), is("60517"));
			if (i == 1)
			{
				assertThat(subAccount.getSubAccountKey().getId(), is("62204"));
				assertThat(subAccount.getSubAccountType(), is(ContainerType.MANAGED_PORTFOLIO));
			}
			i++;

			if (null != subAccount.getProductIdentifier())
				assertThat(subAccount.getProductIdentifier().getProductKey().getId(), is("84973"));
			if (null != subAccount.getInvPolicySchemId())
                assertThat(subAccount.getInvPolicySchemId().getIpsKey(), is(IpsKey.valueOf("61545")));

		}

		key = AccountKey.valueOf("11263");
		list1 = response.get(key);
		assertThat(list1.size(), is(2));

		return response;
	}

	@Test
	@SecureTestContext
    @Ignore
    public void testClearContainerListCache() throws Exception {
		avaloqCacheAccountIntegrationService.clearContainerListCache();
		Cache cache = cacheManager.getCache(AvaloqCacheManagedAccountIntegrationService.CONTAINER_LIST_CACHE);
		Assert.assertThat(cache.getSize(), Is.is(0));
    }

}

