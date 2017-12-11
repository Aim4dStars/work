package com.bt.nextgen.service.integration.messages;

import java.util.List;

/**
 * @author L070354
 * 
 * Interface defined for the Notification response
 */

public interface NotificationResponse
{

	/**
	 * @return the Notification
	 */
	List <com.btfin.panorama.core.security.integration.messages.Notification> getNotification();

	/**
	 * @param notification the Notification to set
	 */
	void setNotification(List <com.btfin.panorama.core.security.integration.messages.Notification> notification);

}
