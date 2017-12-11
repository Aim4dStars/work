package com.bt.nextgen.service.avaloq.pension;

/**
 * Created by m040398 on 10/08/2016.
 */
public enum EligibilityCriteria {

    OVER_65("over_65"),
    TTR("retire_cond_of_release"),
    PSV_AGE_TTR("psv_age_ttr"),
    UNPSV("unpsv");

    private String intlId;

    EligibilityCriteria(String intlId) {
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

