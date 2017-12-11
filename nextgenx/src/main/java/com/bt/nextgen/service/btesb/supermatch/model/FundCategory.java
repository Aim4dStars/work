package com.bt.nextgen.service.btesb.supermatch.model;

/**
 * Enumeration for fund category to identify if the fund can be rolled-over
 */
public enum FundCategory {

    ROLLOVERABLE("Rolloverable"),
    NON_ROLLOVERABLE("NonRolloverable"),
    ROLLOVERED("Rollovered"),
    PARTIALLY_ROLLOVERED("PartiallyRollovered"),
    ATO_MONIES("ATOMonies");

    private String value;

    FundCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
