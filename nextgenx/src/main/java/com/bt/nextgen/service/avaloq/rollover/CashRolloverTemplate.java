package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum CashRolloverTemplate implements AvaloqTemplate {

    CASH_ROLLOVER("BTFG$UI_SA_SSTREAM_FUND.CASH_ROLLOVER", new ArrayList<AvaloqParameter>() {
    });

    private String templateName;
    private List<AvaloqParameter> validParams;

    CashRolloverTemplate(String templateName, List<AvaloqParameter> validParams) {
        this.templateName = templateName;
        this.validParams = validParams;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return validParams;
    }
}
