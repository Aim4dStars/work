package com.bt.nextgen.api.draftaccount.util;

import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.EligibilityTypeType;

/**
 * Created by F058391 on 9/08/2016.
 */
public enum EligibilityCriteria {
    OVER_65 ("OVER_65", EligibilityTypeType.AGE_65),
    RETIRE_COND_OF_RELEASE("RETIRE_COND_OF_RELEASE",EligibilityTypeType.RETIREMENT),
    PSV_AGE_TTR ("PSV_AGE_TTR",EligibilityTypeType.PRESERVATION_AGE_TRANSITION_TO_RETIREMENT),
    UNPSV("UNPSV",EligibilityTypeType.UNRESTRICTED_NON_PRESERVE);

    private String eligibilityCriteriaVal;
    private EligibilityTypeType eligibilityTypeType;

    EligibilityCriteria(String eligibilityCriteria, EligibilityTypeType eligibilityTypeType) {
        this.eligibilityCriteriaVal = eligibilityCriteria;
        this.eligibilityTypeType = eligibilityTypeType;
    }

    public static EligibilityTypeType getEligibilityTypeType(String value)
    {
        for (EligibilityCriteria eligibilityCriteria : EligibilityCriteria.values())
        {
            if (eligibilityCriteria.eligibilityCriteriaVal.equals(value))
            {
                return eligibilityCriteria.eligibilityTypeType;
            }
        }
        return null;
    }
}