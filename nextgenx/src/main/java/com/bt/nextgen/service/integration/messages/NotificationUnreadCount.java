package com.bt.nextgen.service.integration.messages;

/**
 *
 * Interface to define totals from Notification Unread Count Response
 */
public interface NotificationUnreadCount
{

	/**
	 * @return the Count for UnRead Notifications for clients .
	 */
	int getTotalUnreadClientNotifications();

	/**
	 * @return the Count for UnRead Notifications for logged GCM User.
	 */
	int getTotalUnreadMyNotifications();


	int getTotalNotifications();

}
