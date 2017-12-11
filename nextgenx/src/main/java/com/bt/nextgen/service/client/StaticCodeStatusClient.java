package com.bt.nextgen.service.client;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.EhCacheInfo;
import com.bt.nextgen.core.cache.GenericCache;
import com.btfin.panorama.service.client.status.CacheStatus;
import com.btfin.panorama.service.client.status.IServiceStatusClient;
import com.btfin.panorama.service.client.status.IServiceStatusRegistry;
import com.btfin.panorama.service.client.status.CacheServiceStatus;
import net.sf.ehcache.Cache;
import net.sf.ehcache.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by M041612 on 13/01/2017.
 *
 * This class gets communicates whether the STATIC_CODE_CACHE has loaded locally in Nextgen,
 * so that the @RestServiceMonitor can log STATIC_CODE_CACHE readiness along with the offthread service's
 * readiness.
 */

@Service("StaticCodeStatusClient")
@Profile({"OffThreadImplementation"})
public class StaticCodeStatusClient implements IServiceStatusClient {

    private static final Logger logger = LoggerFactory.getLogger(StaticCodeStatusClient.class);

    private static final String cacheType = "STATIC_CODE_CACHE";

    @Autowired
    private GenericCache genericCache;

    @Autowired
    private IServiceStatusRegistry serviceStatusRegistry;

    private Map<Object, Object> staticCacheMap;

    @Override
    public CacheServiceStatus getServiceStatus() {
        int elementsInMemory = 0;

        staticCacheMap = (Map<Object, Object>) genericCache.getAll(CacheType.valueOf(cacheType));
        elementsInMemory = staticCacheMap.size();

        //as we cannot make a judgement on the number of Static Codes loaded, Any number greater than zero is
        //considered loaded
        if (elementsInMemory > 0) {
            List<CacheStatus> cacheStatuses = new ArrayList<>();
            cacheStatuses.add(new CacheStatus(cacheType, elementsInMemory, true));
            return new CacheServiceStatus("NEXTGEN.STATIC_CODE_CACHE", "Started", cacheStatuses);
        } else {
            List<CacheStatus> cacheStatuses = new ArrayList<>();
            cacheStatuses.add(new CacheStatus(cacheType, elementsInMemory, false));
            return new CacheServiceStatus("NEXTGEN.STATIC_CODE_CACHE", "Starting", cacheStatuses);
        }
    }

    /**
     * Registering the StaticCodeStatusClient lets the IServiceStatus know that this client exists,
     * so that its startup status can be checked
     */
    @PostConstruct
    public void postConstruct() {
        serviceStatusRegistry.register(this);
    }
}
