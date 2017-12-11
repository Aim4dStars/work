package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum DrawdownStrategyParams implements AvaloqParameter {
    CONT_LIST_ID("cont_list_id", AvaloqType.PARAM_ID);

    private String param;
    private AvaloqType type;

    private DrawdownStrategyParams(String param, AvaloqType type) {
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
