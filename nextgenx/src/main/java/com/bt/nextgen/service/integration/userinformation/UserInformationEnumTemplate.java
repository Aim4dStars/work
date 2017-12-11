package com.bt.nextgen.service.integration.userinformation;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.List;

/**
 * Created by L070589 on 12/06/2015.
 */
@SuppressWarnings("squid:S1948")
public enum UserInformationEnumTemplate implements AvaloqTemplate {

    USER_INFORMATION("BTFG$UI_USER.USER#USER_DET"),
    JOB_PROFILE_LIST("BTFG$UI_SEC_USER_LIST.MY#JOB_USER"),
    JOB_PROFILE_LIST_FOR_USER("BTFG$UI_SEC_USER_LIST.LOOKUP#JOB_USER");

    private List<AvaloqParameter> validParams;
    private String templateName;

    UserInformationEnumTemplate(String templateName) {

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
