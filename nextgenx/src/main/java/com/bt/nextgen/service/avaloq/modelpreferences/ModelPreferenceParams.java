package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum ModelPreferenceParams implements AvaloqParameter {
    PARAM_SUBACCOUNT_LIST_ID("cont_list_id", AvaloqType.PARAM_TYPE_TEXT_FIELD),
    PARAM_ACCOUNT_LIST_ID("bp_list_id", AvaloqType.PARAM_TYPE_TEXT_FIELD);

    private String param;
    private AvaloqType type;

    private ModelPreferenceParams(String param, AvaloqType type) {
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
