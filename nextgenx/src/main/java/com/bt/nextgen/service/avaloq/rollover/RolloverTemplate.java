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
public enum RolloverTemplate implements AvaloqTemplate {

    RECEIVED_ROLLOVER("BTFG$UI_RCVD_RLOV_LIST.DOC", new ArrayList<AvaloqParameter>() {
        {
            add(RolloverParams.PARAM_ACCOUNT_ID);
        }
    }),
    RECEIVED_CONTRIBUTION("BTFG$UI_RCVD_CONTRI_LIST.DOC", new ArrayList<AvaloqParameter>() {
        {
            add(RolloverParams.PARAM_ACCOUNT_ID);
        }
    }),
    ROLLOVER_HISTORY("BTFG$UI_RLOV_IN_LIST.DOC", new ArrayList<AvaloqParameter>() {
        {
            add(RolloverParams.ACCOUNT_ID_LIST);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    RolloverTemplate(String templateName, List<AvaloqParameter> validParams) {
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
