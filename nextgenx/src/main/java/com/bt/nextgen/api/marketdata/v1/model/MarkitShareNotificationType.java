package com.bt.nextgen.api.marketdata.v1.model;

public enum MarkitShareNotificationType {

    ASX("ASX"),NEWS("NEWS");

    private String code;

    MarkitShareNotificationType(String code) {
        this.code = code;
    }

    public static MarkitShareNotificationType fromCode(String code) {
        for (MarkitShareNotificationType notificationType : MarkitShareNotificationType.values()) {
            if (notificationType.code.equalsIgnoreCase(code)) {
                return notificationType;
            }
        }
        throw new IllegalArgumentException("can not find the notification for code : " + code);
    }

}
