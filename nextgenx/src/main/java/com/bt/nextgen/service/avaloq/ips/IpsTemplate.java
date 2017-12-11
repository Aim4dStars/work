package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum IpsTemplate implements AvaloqTemplate {

    IPS_INVST_OPT("BTFG$UI_PROD_LIST.USER#IPS_INVST_OPT_DET", new ArrayList<AvaloqParameter>()),
    IPS_LIST("BTFG$UI_IPS_LIST.ALL#IPS_DET", new ArrayList<AvaloqParameter>()),
    IPS_LIST_SELECTIVE("BTFG$UI_IPS_LIST.IPS#IPS_DET", new ArrayList<AvaloqParameter>()),
    IPS_SUMMARY_LIST("BTFG$UI_IPS_LIST.IPS_DET", new ArrayList<AvaloqParameter>());

    private List<AvaloqParameter> validParams;
    private String templateName;

    IpsTemplate(String templateName, List<AvaloqParameter> validParams) {
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
