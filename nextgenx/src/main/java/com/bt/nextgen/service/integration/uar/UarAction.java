package com.bt.nextgen.service.integration.uar;

/**
 * Created by l069679 on 14/06/2016.
 */
public enum UarAction {
    GET_UAR_ORDER_ID("Get Uar Order Id"),
    GET_UAR_LIST("Get Uar list"),
    SUBMIT_UAR_LIST("Submit uar list");

    private final String uarActionValue;

    public String getUarActionValue() {
        return uarActionValue;
    }

    UarAction(String uarActionValue) {
        this.uarActionValue = uarActionValue;
    }

}
