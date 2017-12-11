package com.bt.nextgen.api.pushnotification.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDto;
import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDtoKey;
import com.bt.nextgen.core.repository.PushSubscriptionDetails;
import com.bt.nextgen.core.repository.PushSubscriptionKey;
import com.bt.nextgen.core.repository.PushSubscriptionRepository;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Service to update push notification details in the table
 */
@Service
@Transactional(value = "springJpaTransactionManager")
public class PushNotificationDtoServiceImpl implements PushNotificationDtoService {

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private PushSubscriptionRepository pushSubscriptionRepository;

    /**
     * Updates the table 'PUSH_NTFCN_SUBSCRIPTION_DETAILS' with deviceId, userId, platform, active_flag
     * It enables/disables the user+device combination for receiving PUSH notifications
     *
     * @param pushSubscriptionDto
     * @param serviceErrors
     * @return
     */
    @Override
    public PushSubscriptionDto update(PushSubscriptionDto pushSubscriptionDto, ServiceErrors serviceErrors) {
        final PushSubscriptionDetails details = new PushSubscriptionDetails();
        if (pushSubscriptionDto.getKey() != null && StringUtils.isNotBlank(pushSubscriptionDto.getKey().getDeviceUid())) {

            final String userId = profileService.getActiveProfile().getBankReferenceId();
            if (StringUtils.isNotBlank(userId)) {
                final PushSubscriptionKey key = new PushSubscriptionKey(pushSubscriptionDto.getKey().getDeviceUid(), userId);
                details.setKey(key);
                details.setPlatform(pushSubscriptionDto.getPlatform());
                details.setActive(StringUtils.equalsIgnoreCase(pushSubscriptionDto.getSubscriptionAction(), "subscribe"));

                final PushSubscriptionDetails updatedDetails = pushSubscriptionRepository.update(details);
                if (updatedDetails != null) {
                    final PushSubscriptionDtoKey detailsDtoKey = new PushSubscriptionDtoKey(updatedDetails.getKey().getDeviceUid());
                    final String subscriptionAction = updatedDetails.isActive() ? "subscribed" : "unsubscribed";
                    return new PushSubscriptionDto(detailsDtoKey, updatedDetails.getPlatform(), subscriptionAction);
                }
            }
        }
        return null;
    }
}
