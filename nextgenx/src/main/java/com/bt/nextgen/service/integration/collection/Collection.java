package com.bt.nextgen.service.integration.collection;

import java.io.Serializable;
import java.util.List;

/**
 * Interface for collection details
 */
public interface Collection extends Serializable {

    String getCollectionId();

    String getCollectionSymId();

    List<String> getAssetIds();
}
