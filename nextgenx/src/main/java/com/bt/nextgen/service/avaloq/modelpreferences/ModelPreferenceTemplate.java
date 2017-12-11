package com.bt.nextgen.service.avaloq.modelpreferences;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Suppress warning added as the fields are not supposed to be serialized and the valid parameters list provide information about
 * the parameters that can be supplied to a particular template.
 */
@SuppressWarnings({ "squid:S1171", "squid:S1948" })
public enum ModelPreferenceTemplate implements AvaloqTemplate {

    SUBACCOUNT_PREFERENCES("BTFG$UI_CONT_LIST.CONT#MP_PREF", new ArrayList<AvaloqParameter>() {
        {
            add(ModelPreferenceParams.PARAM_SUBACCOUNT_LIST_ID);
        }
    }),

    ACCOUNT_PREFERENCES("BTFG$UI_CONT_LIST.BP#MP_PREF", new ArrayList<AvaloqParameter>() {
        {
            add(ModelPreferenceParams.PARAM_ACCOUNT_LIST_ID);
        }
    });

    private List<AvaloqParameter> validParams;
    private String templateName;

    ModelPreferenceTemplate(String templateName, List<AvaloqParameter> validParams) {
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
