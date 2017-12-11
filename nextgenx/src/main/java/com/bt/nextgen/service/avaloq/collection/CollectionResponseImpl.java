package com.bt.nextgen.service.avaloq.collection;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.collection.Collection;
import com.bt.nextgen.service.integration.collection.CollectionResponse;

import java.util.List;

/**
 * This is a mapping class for Collection response (collection list)
 */
@ServiceBean(xpath = "/")
public class CollectionResponseImpl extends AvaloqBaseResponseImpl implements CollectionResponse {

    @ServiceElementList(xpath = "//data/collect_list/collect", type = CollectionImpl.class)
    private List <Collection> collectionList;

    public List<Collection> getCollectionList() {
        return collectionList;
    }
}
