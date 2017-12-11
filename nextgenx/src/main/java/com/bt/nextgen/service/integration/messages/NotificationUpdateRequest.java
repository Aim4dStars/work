package com.bt.nextgen.service.integration.messages;

import com.btfin.panorama.core.security.integration.messages.NotificationStatus;

/**
 * @author L070589
 *
 * Interface to define methods for Notification Update Request
 */

public interface NotificationUpdateRequest
{
    public com.btfin.panorama.core.security.integration.messages.NotificationIdentifier getNotificationId();
    public NotificationStatus getStatus();
}
