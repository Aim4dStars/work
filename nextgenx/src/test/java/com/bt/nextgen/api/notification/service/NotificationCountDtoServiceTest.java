package com.bt.nextgen.api.notification.service;

import static org.junit.Assert.*;

import com.btfin.panorama.core.security.profile.UserProfileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.notification.model.NotificationCountDto;
import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountResponseImpl;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;

@RunWith(MockitoJUnitRunner.class)
public class NotificationCountDtoServiceTest
{
	@InjectMocks
	NotificationCountDtoServiceImpl dtoService;

	@Mock
    UserProfileService userProfileService;

    @Mock
    NotificationIntegrationService notificationService;

	NotificationUnreadCountResponse response;
	ServiceErrors serviceErrors;

	@Before
	public void setup()
	{
		response = new NotificationUnreadCountResponseImpl();
	}

	@Test
	public void testFindAllSuccess()
	{
		Mockito.when(notificationService.getUnReadNotification(Mockito.any(ServiceErrors.class))).thenReturn(response);
		NotificationCountDto result = dtoService.find(new NotificationDtoKey(true), serviceErrors);
		assertEquals(result.getPriorityCount(), 0);
		assertEquals(result.getUnreadCount(), 0);
	}
}
