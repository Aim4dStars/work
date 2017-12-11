package com.bt.nextgen.api.global.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.global.model.GlobalDetailsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GlobalDetailsDtoServiceTest {
    @InjectMocks
    GlobalDetailsDtoServiceImpl dtoService;

    @Mock
    NotificationIntegrationService notificationService;

    NotificationUnreadCountResponse response;
    ServiceErrors serviceErrors;

    @Before
    public void setup() {
        response = new NotificationUnreadCountResponse() {
            @Override
            public int getTotalUnreadClientNotifications() {
                return 5;
            }

            @Override
            public int getTotalUnreadMyNotifications() {
                return 2;
            }

            @Override
            public int getTotalPriorityClientNotifications() {
                return 0;
            }

            @Override
            public int getTotalPriorityMyNotifications() {
                return 0;
            }

            @Override
            public int getTotalPriorityNotifications() {
                return 0;
            }

            @Override
            public int getTotalNotifications() {
                return 0;
            }
        };
    }

    @Test
    public void testFindAllSuccess() {
        Mockito.when(notificationService.getUnReadNotification(Mockito.any(ServiceErrors.class))).thenReturn(response);
        GlobalDetailsDto result = dtoService.findOne(serviceErrors);
        assertEquals(result.getUnreadCount(), 7);
        assertEquals(result.getUnreadClientCount(), 5);
        assertEquals(result.getUnreadMyCount(), 2);
    }
}
