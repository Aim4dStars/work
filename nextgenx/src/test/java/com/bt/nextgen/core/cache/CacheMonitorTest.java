package com.bt.nextgen.core.cache;

import static com.bt.nextgen.core.cache.CacheMonitor.CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP;
import static com.bt.nextgen.core.cache.CacheMonitor.CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT;
import static com.bt.nextgen.core.cache.CacheMonitor.DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP;
import static com.bt.nextgen.core.cache.CacheMonitor.DAILY_CHECK_STALE_THRESHOLD_DEFAULT;
import static com.bt.nextgen.core.cache.CacheMonitor.VALUE_SEPARATOR;
import static com.bt.nextgen.core.cache.CacheType.ADVISER_PRODUCT_LIST_CACHE;
import static com.bt.nextgen.core.cache.CacheType.AVAILABLE_ASSET_LIST_CACHE;
import static com.bt.nextgen.core.cache.CacheType.JOB_USER_BROKER_CACHE;
import static com.bt.nextgen.core.cache.CacheType.STATIC_CODE_CACHE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.core.util.ApplicationProperties;


/**
 * Tests {@link CacheMonitor}.
 * 
 * @author Albert Hirawan
 */
@RunWith(MockitoJUnitRunner.class)
public class CacheMonitorTest {
	@Mock
    private ApplicationProperties applicationProperties;

	@Mock
    private GenericCache genericCache;

	@Mock
    private CacheInfo cacheInfo;
	
	@InjectMocks
	private CacheMonitor monitor;
	
    
	@Test
	public void noInit() {
		final List<CacheType> dailyCheckCacheTypeNames = monitor.getDailyCheckCacheTypes();
		
		assertThat("number of cache types", dailyCheckCacheTypeNames.size(), equalTo(0));
		assertThat("stale threshold", monitor.getDailyCheckStaleCacheThreshold(),
					equalTo(DAILY_CHECK_STALE_THRESHOLD_DEFAULT));
	}
	
	
	@Test
	public void initWithDefaults() {
		final String[] defaultCacheTypesForDailyStaleCheckArray = StringUtils.split(
							CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT,
							VALUE_SEPARATOR);
		final Set<CacheType> dailyCheckCacheTypeNameSet = new HashSet<>();
		
		when(applicationProperties.get(CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP,
					CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT)).thenReturn(CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT);
		when(applicationProperties.getInteger(DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP,
					DAILY_CHECK_STALE_THRESHOLD_DEFAULT)).thenReturn(DAILY_CHECK_STALE_THRESHOLD_DEFAULT);
		
		monitor.init();
		dailyCheckCacheTypeNameSet.addAll(monitor.getDailyCheckCacheTypes());
		
		assertThat("number of cache types", dailyCheckCacheTypeNameSet.size(),
					equalTo(defaultCacheTypesForDailyStaleCheckArray.length));
		
		for (CacheType typeName : dailyCheckCacheTypeNameSet) {
			assertThat("cache type", typeName.name(), Matchers.isOneOf(defaultCacheTypesForDailyStaleCheckArray));
		}
		
		assertThat("stale threshold", monitor.getDailyCheckStaleCacheThreshold(),
					equalTo(DAILY_CHECK_STALE_THRESHOLD_DEFAULT));
	}
	

	@Test
	public void init() {
		init(123, STATIC_CODE_CACHE.name() + VALUE_SEPARATOR + JOB_USER_BROKER_CACHE.name());
	}
	

	@Test
	public void initWithValueSeparatorAndSpace() {
		init(456, " " + STATIC_CODE_CACHE.name() + VALUE_SEPARATOR + " " + JOB_USER_BROKER_CACHE.name() + " ");
	}
	
	
	@Test
	public void dailyCheckStaleStaticDataCaches() {
		final int staleThreshold = 3600;
		final String cacheTypesForDailyStaleCheck = STATIC_CODE_CACHE.name() + VALUE_SEPARATOR
						+ JOB_USER_BROKER_CACHE.name() + VALUE_SEPARATOR
						+ AVAILABLE_ASSET_LIST_CACHE.name() + VALUE_SEPARATOR
						+ ADVISER_PRODUCT_LIST_CACHE.name();
		
		when(applicationProperties.get(CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP,
					CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT)).thenReturn(cacheTypesForDailyStaleCheck);
		when(applicationProperties.getInteger(DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP,
					DAILY_CHECK_STALE_THRESHOLD_DEFAULT)).thenReturn(staleThreshold);
		when(cacheInfo.getName(STATIC_CODE_CACHE)).thenReturn("Static_code_cache");
		when(cacheInfo.getName(JOB_USER_BROKER_CACHE)).thenReturn("Job_user_broker_cache");
		when(cacheInfo.getName(ADVISER_PRODUCT_LIST_CACHE)).thenReturn("APL_cache");
		when(cacheInfo.getName(AVAILABLE_ASSET_LIST_CACHE)).thenReturn("AAL_cache");
		
		when(genericCache.getLastRefreshed(STATIC_CODE_CACHE)).thenReturn(null);
		when(genericCache.getLastUpdated(STATIC_CODE_CACHE)).thenReturn(null);
		
		when(genericCache.getLastRefreshed(JOB_USER_BROKER_CACHE)).thenReturn(makeDateInPastRelativeToNow(staleThreshold + 2));
		when(genericCache.getLastUpdated(JOB_USER_BROKER_CACHE)).thenReturn(makeDateInPastRelativeToNow(staleThreshold + 2));
		
		when(genericCache.getLastRefreshed(ADVISER_PRODUCT_LIST_CACHE)).thenReturn(makeDateInPastRelativeToNow(staleThreshold + 10));		
		when(genericCache.getLastUpdated(ADVISER_PRODUCT_LIST_CACHE)).thenReturn(makeDateInPastRelativeToNow(2));
		
		when(genericCache.getLastRefreshed(AVAILABLE_ASSET_LIST_CACHE)).thenReturn(makeDateInPastRelativeToNow(20));		
		when(genericCache.getLastUpdated(AVAILABLE_ASSET_LIST_CACHE)).thenReturn(makeDateInPastRelativeToNow(2));
		
		monitor.init();
		monitor.dailyCheckStaleStaticDataCaches();

		verify(applicationProperties).get(CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP,
					CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT);
		verify(applicationProperties).getInteger(DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP,
					DAILY_CHECK_STALE_THRESHOLD_DEFAULT);
		verify(cacheInfo).getName(STATIC_CODE_CACHE);
		verify(cacheInfo).getName(JOB_USER_BROKER_CACHE);
		verify(cacheInfo).getName(ADVISER_PRODUCT_LIST_CACHE);
		verify(cacheInfo).getName(AVAILABLE_ASSET_LIST_CACHE);

		verify(genericCache).getLastRefreshed(STATIC_CODE_CACHE);
		verify(genericCache).getLastRefreshed(JOB_USER_BROKER_CACHE);
		verify(genericCache).getLastRefreshed(ADVISER_PRODUCT_LIST_CACHE);
		
		verify(genericCache).getLastUpdated(STATIC_CODE_CACHE);
		verify(genericCache).getLastUpdated(JOB_USER_BROKER_CACHE);
		verify(genericCache).getLastUpdated(ADVISER_PRODUCT_LIST_CACHE);
	}
	
	
	private Date makeDateInPastRelativeToNow(int secondsInPast) {
		final Date now = new Date();
		
		return new Date(now.getTime() - (secondsInPast * 1000));
	}


	private void init(int staleThreshold, String cacheTypesForDailyStaleCheck) {
		final String[] cacheTypesForDailyStaleCheckArray = trimElements(StringUtils.split(
						cacheTypesForDailyStaleCheck,
						VALUE_SEPARATOR));
		final Set<CacheType> dailyCheckCacheTypeNameSet = new HashSet<>();
		
		when(applicationProperties.get(CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP,
					CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT)).thenReturn(cacheTypesForDailyStaleCheck);
		when(applicationProperties.getInteger(DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP,
					DAILY_CHECK_STALE_THRESHOLD_DEFAULT)).thenReturn(staleThreshold);
		
		monitor.init();		
		dailyCheckCacheTypeNameSet.addAll(monitor.getDailyCheckCacheTypes());
		
		assertThat("number of cache types", dailyCheckCacheTypeNameSet.size(),
					equalTo(cacheTypesForDailyStaleCheckArray.length));

		for (CacheType typeName : dailyCheckCacheTypeNameSet) {
			assertThat("cache type", typeName.name(), Matchers.isOneOf(cacheTypesForDailyStaleCheckArray));
		}
		
		assertThat("stale threshold", monitor.getDailyCheckStaleCacheThreshold(),
					equalTo(staleThreshold));
	}
	
	
	private String[] trimElements(String[] strArray) {
		final List<String> retval = new ArrayList<>();
		
		for (String str : strArray) {
			retval.add(str.trim());
		}
		
		return retval.toArray(new String[retval.size()]);
	}
}
