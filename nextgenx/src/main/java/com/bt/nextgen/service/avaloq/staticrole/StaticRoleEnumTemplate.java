package com.bt.nextgen.service.avaloq.staticrole;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L070589 on 12/06/2015.
 */
@SuppressWarnings("squid:S1948")
public enum StaticRoleEnumTemplate implements AvaloqTemplate {

    STATIC_FUNCTIONAL_ROLE("BTFG$UI_SEC_USER_LIST.USER_ROLE#FUNCT_ROLE");

    private List<AvaloqParameter> validParams;
    private String templateName;

    StaticRoleEnumTemplate(String templateName) {

        this.templateName = templateName;
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
