package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.core.security.integration.messages.NotificationIdentifier;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;

/**
 * @author L070589
 *         <p/>
 *         Implementation class for Notification Update Request.
 */
public class NotificationUpdateRequestImpl implements NotificationUpdateRequest {

    private NotificationIdentifier notificationIdentifier;
    private NotificationStatus status;

    @Override
    public NotificationIdentifier getNotificationId() {
        return notificationIdentifier;
    }

    public void setNotificationIdentifier(NotificationIdentifier notificationIdentifier) {
        this.notificationIdentifier = notificationIdentifier;
    }

    @Override
    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;

    }
}
