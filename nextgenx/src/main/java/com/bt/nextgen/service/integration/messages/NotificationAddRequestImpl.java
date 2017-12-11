package com.bt.nextgen.service.integration.messages;

import com.bt.nextgen.core.domain.key.StringIdKey;
import com.btfin.panorama.core.security.integration.messages.NotificationEventType;
import com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey;

public class NotificationAddRequestImpl implements com.btfin.panorama.core.security.integration.messages.NotificationAddRequest {

    private com.btfin.panorama.core.security.integration.messages.NotificationEventType notificationEventType;
    private com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey notificationResolutionBaseKey;
    private StringIdKey triggeringObjectKey;
    private String messageContext;

    private String type;
    private String url;
    private String urlText;
    private String personalizedMessage;

    @Override
    public com.btfin.panorama.core.security.integration.messages.NotificationEventType getNotificationEventType() {
        return notificationEventType;
    }

    public void setNotificationEventType(NotificationEventType notificationEventType) {
        this.notificationEventType = notificationEventType;
    }

    @Override
    public com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey getNotificationResolutionBaseKey() {
        return notificationResolutionBaseKey;
    }

    public void setNotificationResolutionBaseKey(NotificationResolutionBaseKey notificationResolutionBaseKey) {
        this.notificationResolutionBaseKey = notificationResolutionBaseKey;
    }

    @Override
    public StringIdKey getTriggeringObjectKey() {
        return triggeringObjectKey;
    }

    public void setTriggeringObjectKey(StringIdKey triggeringObjectKey) {
        this.triggeringObjectKey = triggeringObjectKey;
    }

    @Override
    public String getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(String messageContext) {
        this.messageContext = messageContext;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getUrlText() {
        return urlText;
    }

    @Override
    public String getPersonalizedMessage() {
        return personalizedMessage;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUrlText(String urlText) {
        this.urlText = urlText;
    }

    public void setPersonalizedMessage(String personalizedMessage) {
        this.personalizedMessage = personalizedMessage;
    }
}
