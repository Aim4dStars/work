package com.bt.nextgen.core;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.EhCacheInfo;
import com.btfin.panorama.service.client.status.ServiceStatus;
import net.sf.ehcache.Cache;
import net.sf.ehcache.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepshikha Singh on 12/01/2015.
 * This health indicator will analyse the cache population status.
 * A session level flag will be updated which can be used across the application for checking the status of cache
 */
@Component
@Profile({"default", "OnThreadImplementation"})
public class ServiceStatusImpl implements IServiceStatus {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceStatusImpl.class);

    @Autowired
    private EhCacheInfo cacheInfo;

    @Value("#{'${cache.enabled.services}'.split(',')}")
    private List<String> dataInitializationCacheTypes;

    /**
     * This method will evaluate the combined status of all the required caches.
     * Once all the dataInitialization is completed the application will be open for login.
     * Session level flag is updated upon completion with the status of cache.
     */
    @Override
    public boolean checkCacheStatus() {
        LOGGER.info("Started checking cache status of Data Initialization services");
        List<Boolean> cacheStatuses = new ArrayList<>();
        for (String cacheType : dataInitializationCacheTypes) {
            cacheStatuses.add(checkCachePopulatedStatus(cacheType));
        }
        if (!cacheStatuses.contains(Boolean.valueOf(false))) {
            LOGGER.info("Startup Completed Successfully: All the caches are populated.");
            return true;
        }
        LOGGER.info("Startup Incomplete: caches still loading");
        LOGGER.info("Is Cache Populated .. : {}", false);
        return false;
    }

    @Override
    public List<ServiceStatus> getServiceStatus() {
        LOGGER.info("OnThreadImplementation no need to check external services status");
        return null;
    }

    private boolean checkCachePopulatedStatus(String cacheType) {
        LOGGER.debug("Started checking cache status for {}", cacheType);
        Cache cache = cacheInfo.getCache(CacheType.valueOf(cacheType));
        final TransactionController transactionController = getTransactionController(cache);
        boolean cachePopulated = false;
        int elementsInMemory = 0;
        try {
            transactionController.begin();
            elementsInMemory = cache.getSize();
            LOGGER.info("Cached elements in memory for {} are {}", cacheType, elementsInMemory);
            if (elementsInMemory > 0) {
                cachePopulated = true;
                LOGGER.info("OnThread Cache Check: Cache Name:{} Status: Started", cacheType);
            } else {
                LOGGER.info("OnThread Cache Check: Cache Name:{} Status: Starting", cacheType);
            }
        } finally {
            transactionController.rollback();
        }
        LOGGER.debug("Completed checking cache status for {} ", cacheType);
        return cachePopulated;
    }

    private TransactionController getTransactionController(Cache cache) {
        return cache.getCacheManager().getTransactionController();
    }
}
