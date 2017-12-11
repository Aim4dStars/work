package com.bt.nextgen.api.pushnotification.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDto;
import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDtoKey;
import com.bt.nextgen.api.pushnotification.service.PushNotificationDtoService;
import com.bt.nextgen.api.pushnotification.validation.PushNotificationDetailsErrorMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Update;

/**
 * This API is used to add/update/remove push notification details for a device/user
 * <br/>
 * Update: secure/api/mobile/v1_0/push-notification/subscription/update
 */

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_MOBILE_VERSION_API, produces = "application/json")

public class PushNotificationApiController {

    @Autowired
    private PushNotificationDtoService pushNotificationDtoService;

    private PushNotificationDetailsErrorMapper errorMapper;

    /**
     * Updates subscription information for the logged in user for PUSH notifications
     *
     * @param deviceUid - device unique identifier
     * @param platform  - mobile platform/OS
     * @param subscriptionAction - subscribe/unsubscribe
     * @return ApiResponse with PushNotificationDto
     */
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.PUSH_SUBSCRIPTION_UPDATE)
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse updatePushSubscription(@RequestParam("deviceUid") String deviceUid,
                                     @RequestParam("platform") String platform,
                                     @RequestParam("subscriptionAction") String subscriptionAction) {
        PushSubscriptionDto pushSubscriptionDto = null;
        if (StringUtils.isNotBlank(deviceUid) && StringUtils.isNotBlank(platform)) {
            pushSubscriptionDto = new PushSubscriptionDto(new PushSubscriptionDtoKey(deviceUid), platform, subscriptionAction);
        }
        return new Update<>(ApiVersion.CURRENT_MOBILE_VERSION, pushNotificationDtoService,
                errorMapper, pushSubscriptionDto).performOperation();
    }
}
