package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;


public enum OrderSummaryParams implements AvaloqParameter {
    PARAM_BROKER_ID("oe_list_id", AvaloqType.PARAM_ID),
    PARAM_VAL_DATE_FROM("trade_date_from", AvaloqType.VAL_DATEVAL),
    PARAM_VAL_DATE_TO("trade_date_to", AvaloqType.VAL_DATEVAL);

    private String param;
    private AvaloqType type;

    private OrderSummaryParams(String param, AvaloqType type) {
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
