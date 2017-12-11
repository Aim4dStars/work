package com.bt.nextgen.api.draftaccount.model;

public class SuperPensionApplicationDetailsDto extends IndividualOrJointApplicationDetailsDto {

    private PensionEligibility pensionEligibility;

    public SuperPensionApplicationDetailsDto withPensionEligibility(PensionEligibility pensionEligibility){
        this.pensionEligibility = pensionEligibility;
        return this;
    }

    public PensionEligibility getPensionEligibility() {
        return pensionEligibility;
    }
}
