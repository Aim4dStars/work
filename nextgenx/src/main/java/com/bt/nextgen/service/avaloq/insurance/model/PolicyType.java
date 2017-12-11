package com.bt.nextgen.service.avaloq.insurance.model;

import com.bt.nextgen.api.staticreference.model.StaticReference;

public enum PolicyType implements StaticReference {

    TERM_LIFE("TermLife", "Term Life"),
    TERM_LIFE_AS_SUPER("TermLifeAsSuper", "Term Life as Super"),
    FLEXIBLE_LINKING_PLUS("FlexibleLinkingPlus", "Flexible Linking Plus"),
    STAND_ALONE_TPD("StandAloneTPD", "Standalone TPD"),
    STAND_ALONE_LIVING("StandAloneLiving", "Standalone Living Insurance"),
    INCOME_PROTECTION_PLUS("IncomeProtectionPlus", "Income Protection Plus"),
    INCOME_PROTECTION("IncomeProtection", "Income Protection"),
    INCOME_PROTECTION_AS_SUPER("IncomeProtectionAsSuper", "Income Protection as Super"),
    INCOME_LINKING_PLUS("IncomeLinkingPlus", "Income Linking Plus"),
    KEY_PERSON_INCOME("KeyPersonIncome", "Key Person Income"),
    BUSINESS_OVERHEAD("BusinessOverhead", "Business Overheads"),
    CHILDREN_BENEFIT("ChildrenBenefit", "Children's Benefit"),
    NEEDLE_STICK("NeedleStick", "Needlestick Benefit"),
    ADVICE_SERVICE_FEE("AdviceServiceFee", "Advice Service Fee"),
    NOT_AVAILABLE("Not Available", "Not available");

    private String value;
    private String displayName;

    PolicyType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PolicyType forValue(String value) {
        for (PolicyType policy : PolicyType.values()) {
            if (policy.getValue().equals(value)) {
                return policy;
            }
        }
        return NOT_AVAILABLE;
    }

    public String getCode() {
        return this.name();
    }

    public String getLabel() {
        return displayName;
    }

    @Override
    public String toString() {
        return value;
    }
}
