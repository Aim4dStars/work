/**
 *
 */
package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;

import java.io.Serializable;

/**
 * Implementation class for Notification Unread Count
 * This class contains totals of unread messages
 * Instantiation occurs on receiving Cache Invalidation message
 */

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class NotificationUnreadCountImpl extends AvaloqBaseResponseImpl implements NotificationUnreadCount, Serializable {

    private int totalClientUnReadMessages;
    private int totalMyUnReadMessages;

    public NotificationUnreadCountImpl(int totalClientUnReadMessages, int totalMyUnReadMessages) {
        this.totalClientUnReadMessages = totalClientUnReadMessages;
        this.totalMyUnReadMessages = totalMyUnReadMessages;
    }

    @Override
    public int getTotalUnreadClientNotifications() {
        return totalClientUnReadMessages;
    }

    @Override
    public int getTotalUnreadMyNotifications() {
        return totalMyUnReadMessages;
    }

    @Override
    public int getTotalNotifications() {
        return getTotalUnreadClientNotifications() + getTotalUnreadMyNotifications();
    }

}
