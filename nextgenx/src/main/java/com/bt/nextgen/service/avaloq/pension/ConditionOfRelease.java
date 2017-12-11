package com.bt.nextgen.service.avaloq.pension;

/**
 * Created by m040398 on 10/08/2016.
 */
public enum ConditionOfRelease {

    LESS_200_LOST_FOUND("less_200_lost_found"),
    LESS_200_CEASE_EMPL("less_200_cease_empl"),
    PERM_INCAP("perm_incap"),
    RETIR("retir"),
    OTHER("oth"),
    TURN_AGE_65("turn_age_65"),
    TERM_EMPL("term_empl"),
    DEATH("death");

    private String intlId;

    ConditionOfRelease(String intlId) {
        this.intlId = intlId;
    }

    public String getIntlId() {
        return intlId;
    }

    public void setIntlId(String intlId) {
        this.intlId = intlId;
    }

    @Override
    public String toString() {
        return intlId;
    }

}

