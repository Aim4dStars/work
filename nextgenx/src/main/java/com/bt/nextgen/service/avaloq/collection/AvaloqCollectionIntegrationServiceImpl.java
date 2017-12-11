package com.bt.nextgen.service.avaloq.collection;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.collection.AvaloqCollectionIntegrationService;
import com.bt.nextgen.service.integration.collection.Collection;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvaloqCollectionIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements AvaloqCollectionIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(AvaloqCollectionIntegrationServiceImpl.class);

    /**
     * Loads the list of Asset Ids based on the collection (symbol) key
     *
     * @param collectionSymKey - Symbolic Key (not ID) for the collection that includes the excluded asset list
     * @return - List of asset Ids
     */
    @Cacheable(key = "#collectionSymKey", value = "com.bt.nextgen.service.avaloq.asset.AvaloqCollectionIntegrationService.collectionAssets")
    @Override
    public List<String> loadAssetsForCollection(final String collectionSymKey, final ServiceErrors serviceErrors) {
        final List<String> assetIds = new ArrayList<>();
        new IntegrationOperation("loadAssetIdsForCollection", serviceErrors) {
            @Override
            public void performOperation() {
                final Map<String, Collection> collectionMap = loadCollectionAssetsMap(Collections.singletonList(collectionSymKey), serviceErrors);
                final Collection collection = collectionMap.get(collectionSymKey);
                if (collection != null) {
                    assetIds.addAll(collection.getAssetIds());
                }
            }
        }.run();

        logger.info("No. of assets: {} for collection {}", assetIds.size(), collectionSymKey);
        return assetIds;
    }

    /**
     * This method returns the Map of collections based on collection sym keys.
     *
     * @param collectionSymKeys - List of Symbolic Keys (not IDs) for the collections that includes the excluded asset list
     * @param serviceErrors     - Service errors
     * @return - Collection
     */
    public Map<String, Collection> loadCollectionAssetsMap(final List<String> collectionSymKeys, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<Map<String, Collection>>("loadCollectionAssetMap", serviceErrors) {
            @Override
            public Map<String, Collection> performOperation() {
                Map<String, Collection> results = new HashMap<>();
                final AvaloqRequest avaloqReportRequest = new AvaloqReportRequestImpl(CollectionTemplate.COLLECTION_ASSETS)
                        .forParam(CollectionParams.COLLECTION_LIST_ID, collectionSymKeys);

                final CollectionResponseImpl response = getResponse(serviceErrors, CollectionResponseImpl.class, avaloqReportRequest);

                if (response != null && CollectionUtils.isNotEmpty(response.getCollectionList())) {
                    logger.info("Size of collection list received: {}", response.getCollectionList().size());
                    for (Collection collection : response.getCollectionList()) {
                        results.put(collection.getCollectionSymId(), collection);
                    }
                    return results;
                }

                return new HashMap<>();
            }
        }.run();
    }
}
