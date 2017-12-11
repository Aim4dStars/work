package com.bt.nextgen.service.avaloq.modelportfolio.orderstatus;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"squid:S1171","squid:S1948"})
public enum OrderSummaryTemplate implements AvaloqTemplate {

    ORDER_STATUS_SUMMARY("BTFG$UI_MP_DOC_LIST.MP_DOC_STATUS", new ArrayList<AvaloqParameter>() {
        {
            add(OrderSummaryParams.PARAM_BROKER_ID);
            add(OrderSummaryParams.PARAM_VAL_DATE_FROM);
        }
    });
    
    private List<AvaloqParameter> validParams;
    private String templateName;

    OrderSummaryTemplate(String templateName, List<AvaloqParameter> validParams) {
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
