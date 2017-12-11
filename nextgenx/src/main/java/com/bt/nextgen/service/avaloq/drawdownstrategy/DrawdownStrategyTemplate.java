package com.bt.nextgen.service.avaloq.drawdownstrategy;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum DrawdownStrategyTemplate implements AvaloqTemplate {

    ASSET_PRIORITY_LIST("BTFG$UI_CONT_LIST.CONT#DD_PREF", new ArrayList<AvaloqParameter>() {
        {
            add(DrawdownStrategyParams.CONT_LIST_ID);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    DrawdownStrategyTemplate(String templateName, List<AvaloqParameter> validParams) {
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
