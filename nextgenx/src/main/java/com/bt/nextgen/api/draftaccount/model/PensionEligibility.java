package com.bt.nextgen.api.draftaccount.model;

/**
 * Created by F030695 on 29/07/2016.
 */
public class PensionEligibility {
    private String eligibilityCriteria;

    private String conditionRelease;

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public String getConditionRelease() {
        return conditionRelease;
    }

    public void setConditionRelease(String conditionRelease) {
        this.conditionRelease = conditionRelease;
    }
}
