package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.conversion.BrokerIntegrationServiceRestClient;
import com.btfin.panorama.service.client.asset.AssetIntegrationServiceRestClient;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import com.btfin.panorama.service.client.util.cache.StaticDataLoaderValue;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Loader for static data.
 * 
 * @author Albert Hirawan
 */
@Component
public class CacheSearch {

    private static final Logger logger = LoggerFactory.getLogger(CacheSearch.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private GenericCache genericCache;

    private AssetIntegrationServiceRestClient assetIntegrationServiceRestClient;
    private BrokerIntegrationServiceRestClient brokerIntegrationServiceRestClient;
    private boolean isOffThread;

    @PostConstruct
    public void init() {
        List<String> springProfiles = Arrays.asList(applicationContext.getEnvironment().getActiveProfiles());

        isOffThread = springProfiles.contains("OffThreadImplementation");
        logger.info("Nextgen is in Offthread Mode ? {} ", isOffThread);
        if (isOffThread) {
            assetIntegrationServiceRestClient = (AssetIntegrationServiceRestClient) applicationContext.getBean("avaloqAssetIntegrationService");
            brokerIntegrationServiceRestClient = (BrokerIntegrationServiceRestClient) applicationContext.getBean("BrokerIntegrationServiceRestClient");
        }
    }

    public List<StaticDataLoaderValue> searchElements(CacheType cacheType, String query) {
        ServiceErrors serviceErrors= new ServiceErrorsImpl();
        List<StaticDataLoaderValue> searchResults = null;
        if (!isOffThread) {
            searchResults=searchElementFromLocalCache(cacheType,query);
        } else {

            switch (cacheType.toString()) {

                case "JOB_USER_BROKER_CACHE":
                    searchResults=brokerIntegrationServiceRestClient.getCacheSearchElements(cacheType.toString(),query,serviceErrors);
                    break;
                case "STATIC_CODE_CACHE":
                case "BANK_DATE":
                case "TRANSACTION_FEES":
                    searchResults=searchElementFromLocalCache(cacheType,query);
                    break;
                default:
                    searchResults=assetIntegrationServiceRestClient.getCacheSearchElements(cacheType.toString(),query,serviceErrors);
                    break;
            }

        }

       return searchResults;
    }


    private List<StaticDataLoaderValue> searchElementFromLocalCache(CacheType cacheType, String query) {
        List<StaticDataLoaderValue> searchResults= new ArrayList<>();
        Map<String, Pair<List<Object>, String>> searchResultMap = genericCache.searchElements(cacheType, query);
        for (Map.Entry<String, Pair<List<Object>, String>> entry : searchResultMap.entrySet()) {
            Pair<List<Object>, String> searchResult = entry.getValue();
            searchResults.add(new StaticDataLoaderValueImpl(entry.getKey(), searchResult.getRight(), searchResult.getLeft()));
        }

        return searchResults;
    }
}
