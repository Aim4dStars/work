package com.bt.nextgen.service.avaloq.collection;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum CollectionParams implements AvaloqParameter {

    COLLECTION_LIST_ID("collect_list_id", AvaloqType.PARAM_COLLECTION_SYM);

    private String param;
    private AvaloqType type;

    CollectionParams(String param, AvaloqType type) {
        this.param = param;
        this.type = type;
    }

    @Override
    public String getParamName() {
        return param;
    }

    @Override
    public AvaloqType getParamType() {
        return type;
    }
}
