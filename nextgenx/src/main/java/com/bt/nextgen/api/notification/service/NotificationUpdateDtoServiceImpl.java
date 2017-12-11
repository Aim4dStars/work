package com.bt.nextgen.api.notification.service;

import com.bt.nextgen.api.notification.model.NotificationUpdateDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.NotificationImpl;
import com.bt.nextgen.service.avaloq.NotificationUpdateRequestImpl;
import com.btfin.panorama.core.security.integration.messages.NotificationIdentifier;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This service updates the status for the given notification id's from 
 * Unread to Read and returns a "Success" or "Fail" response
 *
 */
@Service
public class NotificationUpdateDtoServiceImpl implements NotificationUpdateDtoService
{
	@Autowired
	private NotificationIntegrationService notificationService;

    @Autowired
    private UserProfileService userProfileService;

	@Override
	public NotificationUpdateDto update(NotificationUpdateDto updateDto, ServiceErrors serviceErrors) {
        List<NotificationUpdateRequest> notificationUpdateRequests = new ArrayList<>();
        if (userProfileService.isAdviser() || userProfileService.isAccountant() || userProfileService.isInvestor()) {
            String[] notifications = updateDto.getKey().split(",");
            for (String id : notifications) {
                NotificationUpdateRequestImpl request = new NotificationUpdateRequestImpl();
                NotificationIdentifier notificationId = new NotificationImpl();
                notificationId.setNotificationId(id);
                request.setNotificationIdentifier(notificationId);
                request.setStatus(NotificationStatus.valueOf(updateDto.getStatus()));
                notificationUpdateRequests.add(request);
            }
            return new NotificationUpdateDto(null, notificationService.updateNotifications(notificationUpdateRequests, serviceErrors));
        }
        return new NotificationUpdateDto(null, Constants.FALSE);
    }
}