package com.bt.nextgen.core.cache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.bt.nextgen.core.util.ApplicationProperties;


/**
 * Monitor for cache status.
 * 
 * @author Albert Hirawan
 */
@EnableScheduling
@Configuration
public class CacheMonitor {
	/** Separator for values in a configuration property. */
	public static final String VALUE_SEPARATOR = ";";
	
	/**
	 * Default number of seconds elapsed since the last cache update before it is considered to contain stale data
	 * during daily cache check.
	 */
	public static final int DAILY_CHECK_STALE_THRESHOLD_DEFAULT = 86400;
	
	/** Names of caches to check daily for staleness. */
	public static final String CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT =
			CacheType.ADVISER_PRODUCT_LIST_CACHE.name()
			+ VALUE_SEPARATOR + CacheType.AVAILABLE_ASSET_LIST_CACHE.name()
			+ VALUE_SEPARATOR + CacheType.JOB_USER_BROKER_CACHE.name()
			+ VALUE_SEPARATOR + CacheType.STATIC_CODE_CACHE.name();
	
    /** Type names property for static data cache to check daily. */
	public static final String CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP = "cache.check.daily.stale.types";

	/**
	 * Property for number of seconds elapsed since the last cache update before it is considered to contain stale data
	 * during daily cache check.
	 */
	public static final String DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP = "cache.check.daily.stale.threshold";
	
	
	private static final Logger logger = LoggerFactory.getLogger(CacheMonitor.class);
	
	    
    /** Application properties. */
    @Autowired
    private ApplicationProperties applicationProperties;    
    
    /** GenericCache instance. */
    @Autowired
    private GenericCache genericCache;
    
    @Autowired
	@Qualifier("ehCacheInfo")
    private CacheInfo cacheInfo;
    
    /** Types for static data cache to check daily. */
    private List<CacheType> dailyCheckCacheTypes = new ArrayList<>();

	/**
	 * Number of seconds elapsed since the last cache update before it is considered to contain stale data
	 * during daily cache check.
	 */
    private int dailyCheckStaleCacheThreshold = DAILY_CHECK_STALE_THRESHOLD_DEFAULT;
    
    
    @PostConstruct
    public void init() {
		final String cacheTypeNamesAsString = applicationProperties.get(CACHE_TYPES_FOR_DAILY_STALE_CHECK_CONFIG_PROP,
							CACHE_TYPES_FOR_DAILY_STALE_CHECK_DEFAULT);
		String[] dailyCheckCacheTypeNames;
		
		dailyCheckStaleCacheThreshold = applicationProperties.getInteger(DAILY_CHECK_STALE_THRESHOLD_CONFIG_PROP,
							DAILY_CHECK_STALE_THRESHOLD_DEFAULT);
		dailyCheckCacheTypeNames = StringUtils.split(cacheTypeNamesAsString, VALUE_SEPARATOR);
		
		for (String name : dailyCheckCacheTypeNames) {
			final CacheType cacheType = CacheType.getCacheTypeByName(StringUtils.trim(name));
			
			if (cacheType != null) {
				dailyCheckCacheTypes.add(cacheType);
			}
		}

		logger.info("Static data cache type names (from config) to check daily for staleness: {}", cacheTypeNamesAsString);
		logger.info("Static data cache types to check daily for staleness: {}", dailyCheckCacheTypes);
		logger.info("Staleness threshold for daily check of static data cache: {} seconds", dailyCheckStaleCacheThreshold);
    }
    
    
    @Scheduled(cron = "${cache.check.daily.stale.cron}")
	public void dailyCheckStaleStaticDataCaches() {		
		logger.info("Checking for stale cache for static data (staleThreshold = {} seconds, cache types = {}) ...",
					dailyCheckStaleCacheThreshold, dailyCheckCacheTypes);
		for (CacheType cacheType : dailyCheckCacheTypes) {
			final String cacheName = cacheInfo.getName(cacheType);
			final Date lastRefreshed = genericCache.getLastRefreshed(cacheType);
			final Date lastUpdated = genericCache.getLastUpdated(cacheType);
			final Date now = new Date();
			
			if (lastRefreshed == null) {
				logger.warn("Cache type {} ({}) has not been initialised", cacheType, cacheName);
			}
			else if ((now.getTime() - lastRefreshed.getTime()) / 1000 > dailyCheckStaleCacheThreshold) {
				logger.warn("Cache type {} ({}) contains stale data (last refreshed on {}, last updated on {})",
							cacheType, cacheName, lastRefreshed, lastUpdated);
			}
		}
	}


	public List<CacheType> getDailyCheckCacheTypes() {
		return dailyCheckCacheTypes;
	}


	public int getDailyCheckStaleCacheThreshold() {
		return dailyCheckStaleCacheThreshold;
	}
}
