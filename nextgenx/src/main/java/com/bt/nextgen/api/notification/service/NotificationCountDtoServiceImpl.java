package com.bt.nextgen.api.notification.service;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.notification.model.NotificationCountDto;
import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;

/**
 * This service retrieves the number of unread and priority notifications for either the client or adviser/investor
 *
 */
@Service
public class NotificationCountDtoServiceImpl implements NotificationCountDtoService
{
	@Autowired
	private NotificationIntegrationService notificationService;

    @Autowired
    private UserProfileService userProfileService;

	@Override
	public NotificationCountDto find(NotificationDtoKey key, ServiceErrors serviceErrors)
	{
		NotificationUnreadCountResponse responseList = notificationService.getDetailedUnReadNotification(serviceErrors);
		return toNotificationListDto(key, responseList, serviceErrors);
	}

	protected NotificationCountDto toNotificationListDto(NotificationDtoKey key, NotificationUnreadCountResponse response,
		ServiceErrors serviceErrors)
	{

        if (userProfileService.isAdviser() || userProfileService.isInvestor() || userProfileService.isAccountant()) {
            if (key.isClientNotification()) {
                return new NotificationCountDto(key,
                        response.getTotalUnreadClientNotifications(),
                        response.getTotalPriorityClientNotifications());
            }
            return new NotificationCountDto(key, response.getTotalUnreadMyNotifications(), response.getTotalPriorityMyNotifications());
        }
        return new NotificationCountDto(key, Constants.ZERO_INTEGER,Constants.ZERO_INTEGER);
	}
}