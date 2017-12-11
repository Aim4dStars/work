package com.bt.nextgen.api.draftaccount.util;

import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.ConditionsOfReleaseType;

/**
 * Created by F058391 on 9/08/2016.
 */
public enum ConditionOfRelease {
    TURN_AGE_65("TURN_AGE_65", ConditionsOfReleaseType.AGE_65),
    RETIREMENT("RETIR",ConditionsOfReleaseType.RETIREMENT),
    PERMANENT_INCAPACITY("PERM_INCAP",ConditionsOfReleaseType.PERMANENT_INCAPACITY),
    DEATH("DEATH",ConditionsOfReleaseType.DEATH),
    TERMINATING_GAINFUL_EMPLOYMENT("TERM_EMPL",ConditionsOfReleaseType.TERMINATING_GAINFUL_EMPLOYMENT),
    LOSS_AND_FOUND_200_TERMINATION("LESS_200_CEASE_EMPL",ConditionsOfReleaseType.TERMINATING_GAINFUL_EMPLOYMENT_LESS_THAN_200),
    LESS_200_LOST_FOUND("LESS_200_LOST_FOUND",ConditionsOfReleaseType.LOST_AND_FOUND_LESS_THAN_200),
    OTHER("OTH",ConditionsOfReleaseType.OTHER);

    private String conditionOfReleaseVal;
    private ConditionsOfReleaseType conditionsOfReleaseType;

    ConditionOfRelease(String conditionOfRelease, ConditionsOfReleaseType conditionsOfReleaseType) {
        this.conditionOfReleaseVal = conditionOfRelease;
        this.conditionsOfReleaseType = conditionsOfReleaseType;
    }

    public static ConditionsOfReleaseType getConditionsOfReleaseType(String value)
    {
        for (ConditionOfRelease conditionOfRelease : ConditionOfRelease.values())
        {
            if (conditionOfRelease.conditionOfReleaseVal.equals(value))
            {
                return conditionOfRelease.conditionsOfReleaseType;
            }
        }
        return null;
    }
}
