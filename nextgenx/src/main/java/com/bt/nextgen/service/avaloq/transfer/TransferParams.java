package com.bt.nextgen.service.avaloq.transfer;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum TransferParams implements AvaloqParameter {
    PARAM_ACCOUNT_ID("bp_id", AvaloqType.PARAM_ID);

    private String param;
    private AvaloqType type;

    private TransferParams(String param, AvaloqType type) {
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
