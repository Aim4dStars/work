package com.bt.nextgen.service.avaloq.collection;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.collection.Collection;

import java.util.List;
/**
 * This is a mapping class for Collection object
 */
@ServiceBean(xpath = "collect")
public class CollectionImpl implements Collection {

    @ServiceElement(xpath = "collect_head_list/collect_head/collect/annot/ctx/id")
    private String collectionId;

    @ServiceElement(xpath = "collect_head_list/collect_head/collect/val")
    private String collectionSymId;

    @ServiceElementList(xpath = "asset_list/asset/asset_head_list/asset_head/asset_id/val", type = String.class)
    private List<String> assetIds;

    @Override
    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    @Override
    public String getCollectionSymId() {
        return collectionSymId;
    }

    public void setCollectionSymId(String collectionSymId) {
        this.collectionSymId = collectionSymId;
    }

    @Override
    public List<String> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(List<String> assetIds) {
        this.assetIds = assetIds;
    }
}
