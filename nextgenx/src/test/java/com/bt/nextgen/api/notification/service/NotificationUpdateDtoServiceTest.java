package com.bt.nextgen.api.notification.service;

import static org.junit.Assert.*;

import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.integration.messages.NotificationUpdateRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.notification.model.NotificationUpdateDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;

@RunWith(MockitoJUnitRunner.class)
public class NotificationUpdateDtoServiceTest {
    @InjectMocks
    NotificationUpdateDtoServiceImpl dtoService;

    @Mock
    NotificationIntegrationService notificationService;

    @Mock
    private UserProfileService userProfileService;

    NotificationUpdateDto updateDto;
    ServiceErrors serviceErrors;

    @Before
    public void setup() {
        updateDto = new NotificationUpdateDto("123,456", NotificationStatus.READ.toString());
    }

    @Test
    public void testFindAllSuccess() {
        Mockito.when(notificationService.updateNotifications(Mockito.anyListOf(NotificationUpdateRequest.class),
                Mockito.any(ServiceErrors.class))).thenReturn("Success");
        Mockito.when(userProfileService.isAdviser()).thenReturn(true);
        NotificationUpdateDto result = dtoService.update(updateDto, serviceErrors);
        assertEquals(result.getStatus(), "Success");
        assertNull(result.getKey());
    }
}
