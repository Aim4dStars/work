package com.bt.nextgen.service.avaloq.rules;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

/**
 * Created by M041926 on 29/09/2016.
 */
public enum RuleGetParams implements AvaloqParameter {

    TYPE_ID("type_list_id", AvaloqType.PARAM_ID_FIELD),
    CONDITION_ID("cond_id", AvaloqType.PARAM_ID_FIELD),
    CONDITION_2_ID("cond_2_id", AvaloqType.PARAM_ID_FIELD),
    CONDITION_3_ID("cond_3_id", AvaloqType.PARAM_ID_FIELD),
    CONDITION_VALUE("cond_val", AvaloqType.VAL_NUMERIC),
    CONDITION_2_VALUE("cond_2_val", AvaloqType.VAL_NUMERIC),
    CONDITION_3_VALUE("cond_3_val", AvaloqType.VAL_NUMERIC);

    private String param;
    private AvaloqType type;

    RuleGetParams(String param, AvaloqType type) {
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
