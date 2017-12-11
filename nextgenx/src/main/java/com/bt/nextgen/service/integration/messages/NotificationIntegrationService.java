package com.bt.nextgen.service.integration.messages;

import com.bt.nextgen.service.ServiceErrors;

import com.btfin.panorama.core.security.integration.messages.NotificationAddRequest;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;
import org.joda.time.DateTime;

import java.util.List;

/*
 * Interface to return all types of notification for a GCM user id.
 * */
public interface NotificationIntegrationService
{
	/*
	 * Method to return notifications for a GCM user id
	 * */

	List <com.btfin.panorama.core.security.integration.messages.Notification> loadNotifications(List job_profile_id, DateTime startDate, DateTime endDate, ServiceErrors serviceErrors);

	/*
	 * Method to return total unread notifications for a GCM user id.
	 * This method does not return detailed counts (High priority/non priority)
	 * */
    NotificationUnreadCount getUnReadNotification(ServiceErrors serviceErrors);

    /*
     * Method to return detailed unread notifications for a GCM user id.
     * */
    NotificationUnreadCountResponse getDetailedUnReadNotification(ServiceErrors serviceErrors);

    /**
     * This method will update a single Notification Instance.
     *
     * @param notificationUpdateRequest
     * @param serviceErrors
     * @return
     */
    String updateNotification(com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest notificationUpdateRequest, ServiceErrors serviceErrors);

    /**
     * This method will update a list of notifications to given status.
     *
     * @param notificationUpdateRequest
     * @param serviceErrors
     * @return
     */
    String updateNotifications(List<NotificationUpdateRequest> notificationUpdateRequest, ServiceErrors serviceErrors);

    /**
     * This method is used to add a new notification
     *
     * @param notificationAddRequest
     * @param serviceErrors
     * @return
     */
    String addNotification(com.btfin.panorama.core.security.integration.messages.NotificationAddRequest notificationAddRequest, ServiceErrors serviceErrors);

    /**
     *  Used to add a list of notifications
     *
     * @param notificationAddRequest
     * @param serviceErrors
     * @return
     */
    String addNotifications(List<NotificationAddRequest> notificationAddRequest, ServiceErrors serviceErrors);
}
