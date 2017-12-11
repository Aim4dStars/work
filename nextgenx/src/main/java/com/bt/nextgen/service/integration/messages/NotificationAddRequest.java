package com.bt.nextgen.service.integration.messages;

import com.bt.nextgen.core.domain.key.StringIdKey;
import com.btfin.panorama.core.security.integration.messages.NotificationResolutionBaseKey;

public interface NotificationAddRequest {

    public com.btfin.panorama.core.security.integration.messages.NotificationEventType getNotificationEventType();
    public NotificationResolutionBaseKey getNotificationResolutionBaseKey();
    public StringIdKey getTriggeringObjectKey();
    public String getMessageContext();

    public String getType();
    public String getUrl();
    public String getUrlText();
    public String getPersonalizedMessage();

}
