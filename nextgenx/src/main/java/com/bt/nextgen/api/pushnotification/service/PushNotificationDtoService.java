package com.bt.nextgen.api.pushnotification.service;

import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDto;
import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDtoKey;
import com.bt.nextgen.core.api.dto.UpdateDtoService;

/**
 * Service interface to update push notification details in the table
 */
public interface PushNotificationDtoService extends UpdateDtoService<PushSubscriptionDtoKey, PushSubscriptionDto> {
}
