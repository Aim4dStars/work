package com.bt.nextgen.service.integration.messages;

import java.util.HashMap;
import java.util.Map;

//NOTIFICATION_EVENT_TYPE
public enum NotificationEventType {

    MESSAGE_CENTER("btfg$msg00200","UI Share Content -  ASX Announcement / News Article - Message Centre",null),
    NEWS_EMAIL("btfg$msg00201","UI Share Content -  News Article - Email","Panorama - Shared News article"),
    ASX_EMAIL("btfg$msg00202","UI Share Content -  ASX announcement - Email","Panorama - Shared ASX announcement");

    private String id;
    private String name;
    private String subject;

    NotificationEventType(String id, String name, String subject) {
        this.id = id;
        this.name = name;
        this.subject = subject;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    private static final Map<String, com.btfin.panorama.core.security.integration.messages.NotificationEventType> notificationEventsByInternalIds = new HashMap();
    static {
        for (com.btfin.panorama.core.security.integration.messages.NotificationEventType notificationEventType : com.btfin.panorama.core.security.integration.messages.NotificationEventType.values())
            notificationEventsByInternalIds.put(notificationEventType.getId(), notificationEventType);
    }

    public static com.btfin.panorama.core.security.integration.messages.NotificationEventType getNotificationEvent(String id) {
        return notificationEventsByInternalIds.get(id);
    }
}
