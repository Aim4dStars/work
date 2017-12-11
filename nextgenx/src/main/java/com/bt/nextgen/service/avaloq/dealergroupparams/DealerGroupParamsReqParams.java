package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum DealerGroupParamsReqParams implements AvaloqParameter {
    PARAM_DEALER_GROUP_OE_ID("dg_oe_id", AvaloqType.PARAM_ID),
    PARAM_IPS_TYPE("ips_class_id", AvaloqType.PARAM_ID_FIELD);

    private String param;
    private AvaloqType type;

    private DealerGroupParamsReqParams(String param, AvaloqType type) {
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