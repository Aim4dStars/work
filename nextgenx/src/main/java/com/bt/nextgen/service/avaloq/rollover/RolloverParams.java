package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum RolloverParams implements AvaloqParameter {
    PARAM_ACCOUNT_ID("bp_list_id", AvaloqType.PARAM_ID),
    ACCOUNT_ID_LIST("incl_f1_list_id", AvaloqType.PARAM_ID);

    private String param;
    private AvaloqType type;

    private RolloverParams(String param, AvaloqType type) {
        this.param = param;
        this.type = type;
    }

    public String getName() {
        return param;
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
