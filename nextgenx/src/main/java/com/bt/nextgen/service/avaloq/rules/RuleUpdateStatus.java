package com.bt.nextgen.service.avaloq.rules;

/**
 * Created by M041926 on 5/10/2016.
 */
public class RuleUpdateStatus {

    public RuleUpdateStatus(String ruleId, boolean status) {
        this.ruleId = ruleId;
        this.status = status;
    }

    public RuleUpdateStatus(String ruleId, boolean status, boolean isRuleFound) {
        this.ruleId = ruleId;
        this.status = status;
        this.ruleFound = isRuleFound;
    }

    private String ruleId;

    private boolean status;

    private boolean ruleFound;

    public boolean isRuleFound() {
        return ruleFound;
    }

    public String getRuleId() {
        return ruleId;
    }

    public boolean getStatus() {
        return status;
    }

}
