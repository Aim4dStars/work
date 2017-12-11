package com.bt.nextgen.api.policy.model;

public class BenefitOptionDto {

    private String benefitOptionType;
    private String helpId;
    private boolean status;

    public String getBenefitOptionType() {
        return benefitOptionType;
    }

    public void setBenefitOptionType(String benefitOptionType) {
        this.benefitOptionType = benefitOptionType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getHelpId() {
        return helpId;
    }

    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }
}
