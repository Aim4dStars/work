package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

public class AccountCacheManagerIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private CacheManager cacheManager;

	@Autowired
	private AccountCacheManager accountCacheManager;

	private HashMap <AccountKey, WrapAccount> user1Map = new HashMap <>();
	private HashMap <AccountKey, WrapAccount> user2Map = new HashMap <>();
	private HashMap <AccountKey, WrapAccount> user3Map = new HashMap <>();

	private Cache cache;

	private WrapAccountImpl wrapAccount1;
	private WrapAccountImpl wrapAccount2;
	private WrapAccountImpl wrapAccount3;
	private WrapAccountImpl wrapAccount4;

	@Before
	public void setup()
	{
		//TEST DATA SETUP:
		// user 1
		//		account 1
		//		account 2
		//		account 3		
		// user 2
		//		account 1
		//		account 3
		// user 3
		//		account 1
		wrapAccount1 = new WrapAccountImpl();
		wrapAccount1.setAccountKey(AccountKey.valueOf("account1"));

		wrapAccount2 = new WrapAccountImpl();
		wrapAccount2.setAccountKey(AccountKey.valueOf("account2"));

		wrapAccount3 = new WrapAccountImpl();
		wrapAccount3.setAccountKey(AccountKey.valueOf("account3"));

		wrapAccount4 = new WrapAccountImpl();
		wrapAccount4.setAccountKey(AccountKey.valueOf("account4"));

		user1Map.put(wrapAccount1.getAccountKey(), wrapAccount1);
		user1Map.put(wrapAccount2.getAccountKey(), wrapAccount2);
		user1Map.put(wrapAccount3.getAccountKey(), wrapAccount3);

		user2Map.put(wrapAccount1.getAccountKey(), wrapAccount1);
		user2Map.put(wrapAccount3.getAccountKey(), wrapAccount3);

		user3Map.put(wrapAccount1.getAccountKey(), wrapAccount1);

		cache = cacheManager.getCache(AvaloqCacheManagedAccountIntegrationService.ACCOUNT_LIST_CACHE);
		cache.put(new Element("user1", user1Map));
		cache.put(new Element("user2", user2Map));
		cache.put(new Element("user3", user3Map));
	}

	@Test
	public void testInvalidateCache_whenAccount1IsCleared_thenAllUserCachesCleared() throws Exception
	{
		Assert.assertNotNull(cache.get("user1"));
		Assert.assertNotNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
		accountCacheManager.invalidateCache(wrapAccount1.getAccountKey(), new DateTime());
		Assert.assertNull(cache.get("user1"));
		Assert.assertNull(cache.get("user2"));
		Assert.assertNull(cache.get("user3"));
	}

	@Test
	public void testInvalidateCache_whenAccount2IsCleared_thenUser1IsCleared() throws Exception
	{
		Assert.assertNotNull(cache.get("user1"));
		Assert.assertNotNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
		accountCacheManager.invalidateCache(wrapAccount2.getAccountKey(), new DateTime());
		Assert.assertNull(cache.get("user1"));
		Assert.assertNotNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
	}

	@Test
	public void testInvalidateCache_whenAccount3IsCleared_thenUser1AndUser2IsCleared() throws Exception
	{
		Assert.assertNotNull(cache.get("user1"));
		Assert.assertNotNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
		accountCacheManager.invalidateCache(wrapAccount3.getAccountKey(), new DateTime());
		Assert.assertNull(cache.get("user1"));
		Assert.assertNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
	}

	@Test
	public void testInvalidateCache_whenAccount4IsCleared_thenNoUserCachesAreCleared() throws Exception
	{
		Assert.assertNotNull(cache.get("user1"));
		Assert.assertNotNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
		accountCacheManager.invalidateCache(wrapAccount4.getAccountKey(), new DateTime());
		Assert.assertNotNull(cache.get("user1"));
		Assert.assertNotNull(cache.get("user2"));
		Assert.assertNotNull(cache.get("user3"));
	}
}
