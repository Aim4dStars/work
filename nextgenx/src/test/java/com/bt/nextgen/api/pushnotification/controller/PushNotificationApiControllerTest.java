package com.bt.nextgen.api.pushnotification.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDto;
import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDtoKey;
import com.bt.nextgen.api.pushnotification.service.PushNotificationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PushNotificationApiControllerTest {

    @InjectMocks
    private PushNotificationApiController pushNotificationApiController;

    @Mock
    private PushNotificationDtoService pushNotificationDtoService;

    @Test
    public void testSubscribeSuccess() throws Exception {
        PushSubscriptionDto dto = new PushSubscriptionDto(new PushSubscriptionDtoKey("device1"), "ios", "subscribed");
        Mockito.when(pushNotificationDtoService.update(Mockito.any(PushSubscriptionDto.class),
                Mockito.any(ServiceErrors.class))).thenReturn(dto);

        ApiResponse response = pushNotificationApiController.updatePushSubscription("device1", "ios", "subscribe");
        assertNotNull(response);
        assertEquals(response.getApiVersion(), ApiVersion.CURRENT_MOBILE_VERSION);
        assertEquals(((PushSubscriptionDto) response.getData()).getKey().getDeviceUid(), "device1");
        assertEquals(((PushSubscriptionDto) response.getData()).getPlatform(), "ios");
        assertEquals(((PushSubscriptionDto) response.getData()).getSubscriptionAction(), "subscribed");
    }

    @Test
    public void testUnsubscribeSuccess() throws Exception {
        PushSubscriptionDto dto = new PushSubscriptionDto(new PushSubscriptionDtoKey("device1"), "ios", "unsubscribed");
        Mockito.when(pushNotificationDtoService.update(Mockito.any(PushSubscriptionDto.class),
                Mockito.any(ServiceErrors.class))).thenReturn(dto);
        ApiResponse response = pushNotificationApiController.updatePushSubscription("device1", "ios", "unsubscribe");
        assertNotNull(response);
        assertEquals(response.getApiVersion(), ApiVersion.CURRENT_MOBILE_VERSION);
        assertEquals(((PushSubscriptionDto) response.getData()).getKey().getDeviceUid(), "device1");
        assertEquals(((PushSubscriptionDto) response.getData()).getPlatform(), "ios");
        assertEquals(((PushSubscriptionDto) response.getData()).getSubscriptionAction(), "unsubscribed");
    }

    @Test
    public void testUpdatePushSubscriptionFail() {
        PushSubscriptionDto dto = new PushSubscriptionDto();
        Mockito.when(pushNotificationDtoService.update(Mockito.any(PushSubscriptionDto.class),
                Mockito.any(ServiceErrors.class))).thenReturn(dto);
        try {
            pushNotificationApiController.updatePushSubscription("device1", "ios", "subscribe");
        } catch (Exception e) {
            assertNotNull(e);
            assertTrue(e instanceof BadRequestException);
        }
    }

}
