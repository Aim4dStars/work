package com.bt.nextgen.service.avaloq.rules;

import static com.bt.nextgen.service.avaloq.rules.RuleGetParams.*;

/**
 * Created by M041926 on 27/09/2016.
 */
public enum RuleCond {
    LINK_ACC_NR("LINK_ACC_NR", CONDITION_ID, CONDITION_VALUE),
    GCM_ID("GCM_ID", CONDITION_ID, CONDITION_VALUE),
    BP_ID("BP_ID", CONDITION_2_ID, CONDITION_2_VALUE),
    LINK_BSB("LINK_BSB", CONDITION_3_ID, CONDITION_3_VALUE);

    private String userId;
    private RuleGetParams coditionId;
    private RuleGetParams conditionVal;

    RuleCond(String userId, RuleGetParams conditionId, RuleGetParams conditionVal) {
        this.userId = userId;
        this.coditionId = conditionId;
        this.conditionVal = conditionVal;
    }

    public String getUserId() {
        return userId;
    }

    public RuleGetParams getCoditionId() {
        return coditionId;
    }

    public RuleGetParams getConditionVal() {
        return conditionVal;
    }

    public static RuleCond fromIntlCode(String id) {

        for(RuleCond cond : values()) {
            if (cond.getUserId().equalsIgnoreCase(id)) {
                return cond;
            }
        }

        return null;
    }
}

