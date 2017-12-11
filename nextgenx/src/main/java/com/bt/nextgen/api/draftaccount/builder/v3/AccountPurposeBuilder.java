package com.bt.nextgen.api.draftaccount.builder.v3;


import com.bt.nextgen.api.draftaccount.model.form.IClientApplicationForm;
import com.bt.nextgen.api.draftaccount.model.form.IPensionEligibilityForm;
import com.bt.nextgen.api.draftaccount.util.ConditionOfRelease;
import com.bt.nextgen.api.draftaccount.util.EligibilityCriteria;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.AccountTypeType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.ConditionsOfReleaseType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.EligibilityTypeType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.PensionFundOrdinaryType;
import ns.btfin_com.product.superannuationretirement.superannuationaccount.v3_0.SuperAccountType;

import javax.imageio.metadata.IIOMetadataNode;

public class AccountPurposeBuilder {

    private AccountPurposeBuilder(){}

    public static SuperAccountType getAccountPurpose(IClientApplicationForm form){
        switch(form.getAccountType()){
            case SUPER_ACCUMULATION:
                return getAccountPurposeForAccumulation();
            case SUPER_PENSION:
                return getAccountPurposeForPension(form.getPensionEligibility());
            default:
                return null;
        }
    }

    private static SuperAccountType getAccountPurposeForAccumulation() {
        AccountTypeType accountTypeType = new AccountTypeType();
        //Using this since the ICC schema has no data-type defined
        accountTypeType.setRetirementScheme(new IIOMetadataNode().toString());
        SuperAccountType superAccountType = new SuperAccountType();
        superAccountType.setAccountType(accountTypeType);
        return superAccountType;
    }

    private static SuperAccountType getAccountPurposeForPension(IPensionEligibilityForm pensionEligibilityForm){
        AccountTypeType accountTypeType = new AccountTypeType();
        PensionFundOrdinaryType pensionFundOrdinaryType = new PensionFundOrdinaryType();
        setEligibilityOptions(pensionFundOrdinaryType,pensionEligibilityForm);
        accountTypeType.setPensionFundOrdinary(pensionFundOrdinaryType);
        SuperAccountType superAccountType = new SuperAccountType();
        superAccountType.setAccountType(accountTypeType);
        return superAccountType;
    }

    private static void setEligibilityOptions(PensionFundOrdinaryType pensionFundOrdinaryType,IPensionEligibilityForm pensionEligibilityForm) {

        String eligibilityCriteria = pensionEligibilityForm.getEligibilityCriteria();
        EligibilityTypeType eligibilityTypeType = EligibilityCriteria.getEligibilityTypeType(eligibilityCriteria);
        if(eligibilityTypeType != null){
            pensionFundOrdinaryType.setEligibility(eligibilityTypeType);
            if(eligibilityTypeType.equals(EligibilityTypeType.UNRESTRICTED_NON_PRESERVE)) {
                setConditionsOfRelease(pensionFundOrdinaryType,pensionEligibilityForm.getConditionRelease());
            }
        } else {
            throw new UnsupportedOperationException("Invalid Eligibility Criteria : " + pensionEligibilityForm.getEligibilityCriteria());
        }
    }

    private static void setConditionsOfRelease(PensionFundOrdinaryType pensionFundOrdinaryType,String conditionOfRelease) {
        ConditionsOfReleaseType conditionsOfReleaseType = ConditionOfRelease.getConditionsOfReleaseType(conditionOfRelease);
        if(conditionsOfReleaseType != null) {
            pensionFundOrdinaryType.setConditionsOfRelease(conditionsOfReleaseType);
        }else {
            throw new UnsupportedOperationException("Invalid Condition of release : " +  conditionOfRelease);
        }
    }
}
