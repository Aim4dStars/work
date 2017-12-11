package com.bt.nextgen.service.integration.messages;

/**
 * @author L070589
 * 
 * Interface to define  for Notification Unread Count Response
 */
public interface NotificationUnreadCountResponse extends com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount
{

	/**
	 * @return the Count for Priority Notifications for clients
	 */
	int getTotalPriorityClientNotifications();

	/**
	 * @return the Count for Priority Notifications for logged GCM User.
	 */
	int getTotalPriorityMyNotifications();

	/**
	 * @return the Total Priority Notifications
	 */
	int getTotalPriorityNotifications();

}
