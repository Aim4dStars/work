package com.bt.nextgen.service.avaloq.matchtfn;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum MatchTFNParams implements AvaloqParameter{
    PARAM_PERSON_ID("person_id", AvaloqType.VAL_NUMERIC),
    PARAM_TFN("tfn", AvaloqType.VAL_NUMERIC);


    private String param;
    private AvaloqType type;

    private MatchTFNParams(String param, AvaloqType type)
    {
        this.param = param;
        this.type = type;
    }

    public String getName()
    {
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
