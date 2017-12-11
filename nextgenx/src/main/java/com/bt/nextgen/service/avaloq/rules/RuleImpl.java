package com.bt.nextgen.service.avaloq.rules;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

/**
 * Created by M041926 on 26/09/2016.
 */
@ServiceBean(xpath = "//dt_list/dt[1]")
public class RuleImpl extends AvaloqBaseResponseImpl {

    @ServiceElement(xpath = "//dt_head_list/dt_head/rule_id/val")
    private String ruleId;

    @ServiceElement(xpath = "//dt_head_list/dt_head/type_id/val", staticCodeCategory = "RULE_TYPE", converter = RuleTypeConverter.class)
    private RuleType type;

    @ServiceElement(xpath = "//dt_head_list/dt_head/cond_id/val", staticCodeCategory = "RULE_CONDITION", converter = RuleCondConverter.class)
    private RuleCond condition;

    @ServiceElement(xpath = "//dt_head_list/dt_head/action_id/val", staticCodeCategory = "RULE_ACTION", converter = RuleActionConverter.class)
    private RuleAction action;

    public RuleType getType() {
        return type;
    }

    public RuleCond getCondition() {
        return condition;
    }

    public RuleAction getAction() {
        return action;
    }

    public String getRuleId() {
        return ruleId;
    }

    @Override
    public String toString() {
        return "RuleImpl{" +
                "ruleId='" + ruleId + '\'' +
                ", type=" + type +
                ", condition=" + condition +
                ", action=" + action +
                '}';
    }
}
