package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;

public enum BenefitOptionType implements StaticReference{

    DEATH_BUSINESS_COVER("DeathBusinessCover", "Business Cover", "Help-IP-0183"),
    TPD_BUSINESS_COVER("TPDBusinessCover", "Business Cover", "Help-IP-0183"),
    LIVING_BUSINESS_COVER("LivingBusinessCover", "Business Cover", "Help-IP-0183"),
    TPD_BUY_BACK("TPDBuyBack", "TPD Buy Back", "Help-IP-0186"),
    TPD_DOUBLE("TPDDouble", "Double TPD", "Help-IP-0189"),
    LIVING_DOUBLE("LivingDouble", "Double Living", "Help-IP-0185"),
    LIVING_REINSTATEMENT("LivingReinstatement", "Living Reinstatement", "Help-IP-0184"),
    ACCIDENT("Accident", "Accident Benefit", "Help-IP-0190"),
    WAIVER_LIFE_PREMIUM("WaiverLifePremium", "Waiver of Life Premium", "Help-IP-0192"),
    NOT_AVAILABLE("Not Available", "Not Available",""),
    SUPER_CONTRIBUTION("SuperContribution", "Super Contribution", "Help-IP-0191");

    private String value;
    private String label;
    private boolean status;
    private String helpId;

    BenefitOptionType(String value, String label, String helpId) {
        this.value = value;
        this.label = label;
        this.status = false;
        this.helpId = helpId;
    }

    public String getValue() {
        return value;
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

    public static BenefitOptionType forValue(String value) {
        for (BenefitOptionType benefitOption : BenefitOptionType.values()) {
            if (benefitOption.getValue().equals(value)) {
                return benefitOption;
            }
        }
        return NOT_AVAILABLE;
    }

    @Override
    public String toString() {
        return value;
    }


    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getLabel() {
        return label;
    }
}
