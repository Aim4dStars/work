package com.bt.nextgen.service.avaloq.rules;

import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;

import java.util.List;

/**
 * Created by M041926 on 29/09/2016.
 */
public enum RuleTemplate implements AvaloqTemplate {
    AVALOQ_RULE("BTFG$TASK_2FA_RULE.FILTER#RULE");

    private String templateName;

    RuleTemplate(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public List<AvaloqParameter> getValidParamters() {
        return null;
    }
}
