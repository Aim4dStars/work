package com.bt.nextgen.service.integration.accountingsoftware.model;

/**
 * Created by L062329 on 12/06/2015.
 */
public enum AccountingSoftwareType {

    CLASS("class"),
    UNKNOWN("unknown"),
    BGL("bgl360");

    AccountingSoftwareType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }

    public static AccountingSoftwareType fromValue(String value)
    {
        for (AccountingSoftwareType softwareType : AccountingSoftwareType.values())
        {
            if (softwareType.value.equals(value))
            {
                return softwareType;
            }
        }
        return UNKNOWN;
    }
}
