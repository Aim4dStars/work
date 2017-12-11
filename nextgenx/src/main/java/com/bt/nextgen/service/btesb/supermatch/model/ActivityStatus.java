package com.bt.nextgen.service.btesb.supermatch.model;

/**
 * Enumeration for activity status for the super fund
 */
public enum ActivityStatus {

    ACTIVE("Active"),
    CLOSED("Closed"),
    LOST_INACTIVE("Lost Inactive"),
    LOST_UNCONTACTABLE("Lost Uncontactable"),
    OPEN_AND_LOST("Open And Lost"),
    OPEN_AND_NOT_LOST("Open And Not Lost");

    private String value;

    ActivityStatus(String activityStatus) {
        value = activityStatus;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
