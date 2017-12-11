package com.bt.nextgen.service.avaloq.notification;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GeneralEhCacheImpl;
import com.bt.nextgen.core.cache.KeyGetter;
import com.bt.nextgen.core.cache.ValueGetter;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountImpl;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountResponseImpl;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.avaloq.userinformation.UserInformationImpl;
import com.bt.nextgen.service.avaloq.userprofile.JobProfileImpl;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Test case for NotificationCountCacheService
 */
@RunWith(MockitoJUnitRunner.class)
public class NotificationCountCacheServiceTest {

    @InjectMocks
    private NotificationCountCacheService notificationCountCacheService = new NotificationCountCacheService();

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private GeneralEhCacheImpl cache;

    private NotificationUnreadCountResponse response;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @Before
    public void init() throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource("/webservices/response/BTFG$UI_NTFCN_LIST.PRIO_CAT_CTR.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));
        DefaultResponseExtractor<NotificationUnreadCountResponseImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(NotificationUnreadCountResponseImpl.class);

        response = defaultResponseExtractor.extractData(content);
    }


    @Test
    //Get notification count when cache is empty
    public void testGetNotificationCount_Cache_Null() {
        when(cache.get(any(CacheType.class), anyObject(), any(ValueGetter.class))).thenReturn(null);
        when(userProfileService.getActiveProfile()).thenReturn(getProfile(JobRole.ADVISER, "job id 1", "client1"));
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);
        notificationCountCacheService.getNotificationCount(serviceErrors);
        verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        verify(cache, times(1)).put(anyObject(), any(CacheType.class), any(KeyGetter.class));
    }

    @Test
    //Get notification count when cache is not empty
    public void testGetNotificationCount_Cache_NotNull() {
        when(cache.get(any(CacheType.class), anyObject(), any(ValueGetter.class))).thenReturn(new NotificationUnreadCountImpl(10, 10));
        NotificationUnreadCount notificationUnreadCount = notificationCountCacheService.getNotificationCount(serviceErrors);
        assertThat(notificationUnreadCount.getTotalUnreadClientNotifications(), is(Integer.valueOf("10")));
        assertThat(notificationUnreadCount.getTotalUnreadMyNotifications(), is(Integer.valueOf("10")));
        assertThat(notificationUnreadCount.getTotalNotifications(), is(Integer.valueOf("20")));
    }


    @Test
    public void testGetDetailedNotificationCount() {
        when(userProfileService.getActiveProfile()).thenReturn(getProfile(JobRole.ADVISER, "job id 1", "client1"));
        Mockito.when(avaloqExecute.executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class),
                Mockito.any(Class.class), Mockito.any(ServiceErrors.class))).thenReturn(response);
        notificationCountCacheService.getDetailedNotificationCount(serviceErrors);
        verify(avaloqExecute).executeReportRequestToDomain(Mockito.any(AvaloqReportRequest.class), Mockito.any(Class.class), Mockito.any(ServiceErrors.class));
        verify(cache, times(1)).put(anyObject(), any(CacheType.class), any(KeyGetter.class));
    }

    private UserProfile getProfile(final JobRole role, final String jobId, final String customerId) {
        UserInformationImpl user = new UserInformationImpl();
        user.setClientKey(ClientKey.valueOf(customerId));

        JobProfileImpl job = new JobProfileImpl();
        job.setJobRole(role);
        job.setJob(JobKey.valueOf(jobId));

        UserProfile profile = new UserProfileAdapterImpl(user, job);
        return profile;
    }

}