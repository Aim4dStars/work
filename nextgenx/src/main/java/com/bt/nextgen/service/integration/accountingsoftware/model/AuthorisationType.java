package com.bt.nextgen.service.integration.accountingsoftware.model;

/**
 * Created by L067218 on 14/01/2016.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of the authorisation type corresponding to the software name
 *
 */
public enum AuthorisationType {
    // First value is Software name
    // Second value is the authorisation type
    BGL("bgl", "oauth2"),
    CLASS("class", "none");

    AuthorisationType(String value, String displayValue)
    {
        this.value = value;
        this.displayValue = displayValue;
    }

    private String value;
    // UI Display value
    private String displayValue;

    public String getValue() {
        return value;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
