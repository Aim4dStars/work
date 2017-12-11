package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum IpsParams implements AvaloqParameter {

    PARAM_IPS_LIST("ips_list", AvaloqType.PARAM_ID_FIELD),
    PARAM_INVESTMENT_MANAGER_ID("im_id", AvaloqType.PARAM_ID);

    private String param;
    private AvaloqType type;

    private IpsParams(String param, AvaloqType type) {
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
