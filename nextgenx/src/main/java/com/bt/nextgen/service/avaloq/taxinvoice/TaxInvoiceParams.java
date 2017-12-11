package com.bt.nextgen.service.avaloq.taxinvoice;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqType;

public enum TaxInvoiceParams implements AvaloqParameter {
    PARAM_ACCOUNT_ID("bp_id", AvaloqType.PARAM_ID),
    PARAM_VERI_START_DATE("veri_date_from", AvaloqType.VAL_DATEVAL),
    PARAM_VERI_END_DATE("veri_date_to", AvaloqType.VAL_DATEVAL);

    private String param;
    private AvaloqType type;

    private TaxInvoiceParams(String param, AvaloqType type) {
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
