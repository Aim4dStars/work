package com.bt.nextgen.api.pushnotification.service;

import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDto;
import com.bt.nextgen.api.pushnotification.model.PushSubscriptionDtoKey;
import com.bt.nextgen.core.repository.PushSubscriptionDetails;
import com.bt.nextgen.core.repository.PushSubscriptionKey;
import com.bt.nextgen.core.repository.PushSubscriptionRepository;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PushSubscriptionDtoServiceTest {

    @InjectMocks
    PushNotificationDtoServiceImpl pushNotificationDtoService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private PushSubscriptionRepository pushSubscriptionRepository;

    @Mock
    private UserProfile activeProfile;

    private ServiceErrors serviceErrors;
    private PushSubscriptionDto pushSubscriptionDto;
    private PushSubscriptionDetails pushSubscriptionDetails;


    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        pushSubscriptionDto = new PushSubscriptionDto(new PushSubscriptionDtoKey("device1"), "android", "subscribe");
        pushSubscriptionDetails = new PushSubscriptionDetails();
        when(activeProfile.getBankReferenceId()).thenReturn("gcm1");
        when(activeProfile.getJob()).thenReturn(JobKey.valueOf("job1"));

    }

    @Test
    public void testUpdateSuccess() {
        pushSubscriptionDetails.setKey(new PushSubscriptionKey("device1", "user1"));
        pushSubscriptionDetails.setPlatform("ios");
        pushSubscriptionDetails.setActive(true);

        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(pushSubscriptionRepository.update(any(PushSubscriptionDetails.class))).thenReturn(pushSubscriptionDetails);

        PushSubscriptionDto result = pushNotificationDtoService.update(pushSubscriptionDto, serviceErrors);
        Assert.assertEquals(result.getKey().getDeviceUid(), "device1");
        Assert.assertEquals(result.getPlatform(), "ios");
        Assert.assertEquals(result.getSubscriptionAction(), "subscribed");
    }

    @Test
    public void testUpdateFailed() {
        when(profileService.getActiveProfile()).thenReturn(activeProfile);
        when(pushSubscriptionRepository.update(any(PushSubscriptionDetails.class))).thenReturn(null);

        PushSubscriptionDto result = pushNotificationDtoService.update(pushSubscriptionDto, serviceErrors);
        Assert.assertNull(result);
    }

    @Test
    public void testUpdateNoValues() {
        pushSubscriptionDto = new PushSubscriptionDto();
        PushSubscriptionDto result = pushNotificationDtoService.update(pushSubscriptionDto, serviceErrors);
        Assert.assertNull(result);
    }
}

