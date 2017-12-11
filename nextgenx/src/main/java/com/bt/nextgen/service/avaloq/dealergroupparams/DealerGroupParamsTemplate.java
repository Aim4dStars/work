package com.bt.nextgen.service.avaloq.dealergroupparams;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum DealerGroupParamsTemplate implements AvaloqTemplate {

    CUSTOMER_ACCOUNTING_FOR_DEALER_GROUP("BTFG$UI_MP_REBAL.OE#DFLT_REBAL_DET", new ArrayList<AvaloqParameter>() {
        {
            add(DealerGroupParamsReqParams.PARAM_DEALER_GROUP_OE_ID);
            add(DealerGroupParamsReqParams.PARAM_IPS_TYPE);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    DealerGroupParamsTemplate(String templateName, List<AvaloqParameter> validParams) {
        this.templateName = templateName;
        this.validParams = validParams;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return this.validParams;
    }
}