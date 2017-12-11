package com.bt.nextgen.api.policy.model;

import com.bt.nextgen.service.avaloq.insurance.model.BenefitType;
import com.bt.nextgen.service.avaloq.insurance.model.PolicySubType;

import java.util.List;

public class BenefitsDto {
    private BenefitType benefitType;
    private PolicySubType policySubType;
    private String benefitPeriodFactor;
    private String benefitPeriodTerm;
    private String waitingPeriod;
    private List<BenefitsTypeDto> benefits;
    private List<BenefitOptionDto> benefitOptions;

    public BenefitType getBenefitType() {
        return benefitType;
    }

    public void setBenefitType(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    public PolicySubType getPolicySubType() {
        return policySubType;
    }

    public void setPolicySubType(PolicySubType policySubType) {
        this.policySubType = policySubType;
    }

    public String getBenefitPeriodFactor() {
        return benefitPeriodFactor;
    }

    public void setBenefitPeriodFactor(String benefitPeriodFactor) {
        this.benefitPeriodFactor = benefitPeriodFactor;
    }

    public String getBenefitPeriodTerm() {
        return benefitPeriodTerm;
    }

    public void setBenefitPeriodTerm(String benefitPeriodTerm) {
        this.benefitPeriodTerm = benefitPeriodTerm;
    }

    public String getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(String waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }

    public List<BenefitsTypeDto> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<BenefitsTypeDto> benefits) {
        this.benefits = benefits;
    }

    public List<BenefitOptionDto> getBenefitOptions() {
        return benefitOptions;
    }

    public void setBenefitOptions(List<BenefitOptionDto> benefitOptions) {
        this.benefitOptions = benefitOptions;
    }
}
