package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum ModelSummaryTemplate implements AvaloqTemplate {

    MODEL_PORTFOLIOS_SUMMARY("BTFG$UI_POS_LIST.IM#IPS_AUM", new ArrayList<AvaloqParameter>() {
        {
            add(ModelSummaryParams.PARAM_INVESTMENT_MANAGER_ID);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    ModelSummaryTemplate(String templateName, List<AvaloqParameter> validParams) {
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
