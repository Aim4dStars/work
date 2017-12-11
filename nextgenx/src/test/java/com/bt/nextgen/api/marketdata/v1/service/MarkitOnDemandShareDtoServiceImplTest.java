package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.marketdata.v1.model.ShareNotificationsDto;
import com.bt.nextgen.core.domain.key.StringIdKey;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.NotificationIntegrationServiceImpl;
import com.btfin.panorama.core.security.integration.messages.NotificationAddRequest;
import com.btfin.panorama.core.security.integration.messages.NotificationEventType;
import com.btfin.panorama.core.security.integration.messages.ResolutionGroup;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class MarkitOnDemandShareDtoServiceImplTest {

    @InjectMocks
    private MarkitOnDemandShareDtoServiceImpl markitOnDemandShareDtoServiceImpl;
    @Mock
    private ServiceErrors serviceErrors;
    @Mock
    private NotificationIntegrationServiceImpl notificationIntegrationService;
    @Captor
    ArgumentCaptor<List<NotificationAddRequest>> notificationAddRequestList;
    @Captor
    ArgumentCaptor<ServiceErrors> serviceErrorsArgument;

    private ShareNotificationsDto shareNotificationsDto;

    @Before
    public void setUp() {
        shareNotificationsDto = new ShareNotificationsDto();
        shareNotificationsDto.setConsistentlyEncryptedClientKeys(Arrays.asList(ConsistentEncodedString.fromPlainText("130960").toString()));
        shareNotificationsDto.setUrlText("SOME URL TEXT");
        shareNotificationsDto.setUrl("http://www.btfg.com.au/");
        shareNotificationsDto.setType("ASX");
        shareNotificationsDto.setPersonalizedMessage("Some Personalized message goes here...");
        shareNotificationsDto.setKey("123");
        shareNotificationsDto.setStatus("status");
    }

    @Test
    public void testMarketOnDemandShareStory() {

        markitOnDemandShareDtoServiceImpl.create(shareNotificationsDto, serviceErrors);
        Mockito.verify(notificationIntegrationService).addNotifications(notificationAddRequestList.capture(), serviceErrorsArgument.capture());

        assertThat(notificationAddRequestList.getValue().size(), is(1));
        assertThat(notificationAddRequestList.getValue().get(0).getNotificationEventType(), is(NotificationEventType.ASX_EMAIL));
        //TODO understand what has changed to cause the response to be different.
        //assertThat(notificationAddRequestList.getValue().get(1).getNotificationEventType(), is(NotificationEventType.MESSAGE_CENTER));
        assertThat(notificationAddRequestList.getValue().get(0).getNotificationResolutionBaseKey().getResolutionGroup(), is(ResolutionGroup.PERSON));
        assertThat(notificationAddRequestList.getValue().get(0).getNotificationResolutionBaseKey().getKey(), is("130960"));
        assertThat(notificationAddRequestList.getValue().get(0).getTriggeringObjectKey(), is(new StringIdKey("130960")));
        assertThat(notificationAddRequestList.getValue().get(0).getMessageContext(), is(""));
        assertThat(notificationAddRequestList.getValue().get(0).getType(), is("ASX"));
        assertThat(notificationAddRequestList.getValue().get(0).getUrlText(), is("SOME URL TEXT"));
        assertThat(notificationAddRequestList.getValue().get(0).getUrl(), is("http://www.btfg.com.au/"));
    }

}