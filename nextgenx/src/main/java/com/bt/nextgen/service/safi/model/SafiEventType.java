package com.bt.nextgen.service.safi.model;

/**
 * Enum for the Event types UI sends to SAFI
 */
public enum SafiEventType {
    ADD_PAYEE,
    CHANGE_DAILY_LIMIT,
    CLIENT_DEFINED, // Default
    FORGOTTEN_PASSWORD,
    PAYMENT,
    SUPER_SEARCH_CONSENT,
    USER_DETAILS;

    public static SafiEventType forEventTypeCode(String eventTypeCode) {
        for (SafiEventType eventType : SafiEventType.values()) {
            if (eventType.name().equalsIgnoreCase(eventTypeCode)) {
                return eventType;
            }
        }
        return CLIENT_DEFINED;
    }
}
