package com.bt.nextgen.service.integration.collection;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Interface to load collection details
 */
public interface AvaloqCollectionIntegrationService {

    /**
     * Loads the list of Asset Ids based on the collection (symbol) key
     *
     * @param collectionSymKey - Symbolic Key (not ID) for the collection that includes the excluded asset list
     * @param serviceErrors
     * @return
     */
    List<String> loadAssetsForCollection(String collectionSymKey, ServiceErrors serviceErrors);
}
