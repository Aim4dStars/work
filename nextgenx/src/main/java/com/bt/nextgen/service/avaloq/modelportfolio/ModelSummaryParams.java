package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum ModelSummaryParams implements AvaloqParameter {

    PARAM_INVESTMENT_MANAGER_ID("im_id", AvaloqType.PARAM_ID);

    private String param;
    private AvaloqType type;

    private ModelSummaryParams(String param, AvaloqType type) {
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
