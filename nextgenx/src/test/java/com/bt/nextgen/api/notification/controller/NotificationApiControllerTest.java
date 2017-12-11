package com.bt.nextgen.api.notification.controller;

import com.bt.nextgen.api.notification.model.NotificationCountDto;
import com.bt.nextgen.api.notification.model.NotificationDto;
import com.bt.nextgen.api.notification.model.NotificationDtoKey;
import com.bt.nextgen.api.notification.model.NotificationListDto;
import com.bt.nextgen.api.notification.model.NotificationUpdateDto;
import com.bt.nextgen.api.notification.service.NotificationCountDtoService;
import com.bt.nextgen.api.notification.service.NotificationSearchDtoService;
import com.bt.nextgen.api.notification.service.NotificationUpdateDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.service.ServiceErrors;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class NotificationApiControllerTest {
    @InjectMocks
    private NotificationApiController notificationApiController;

    @Mock
    private NotificationSearchDtoService notificationSearchService;

    @Mock
    private NotificationCountDtoService notificationCountService;

    @Mock
    private NotificationUpdateDtoService notificationUpdateService;

    private String paging = "{\"startIndex\":0,\"maxResults\":\"50\"}";
    private List<NotificationDto> notificationList = getNotificationList();
    private NotificationCountDto notificationCount = new NotificationCountDto(new NotificationDtoKey(true), 10, 5);
    private NotificationUpdateDto updated = new NotificationUpdateDto("id1", "Success");

    @Test
    public void testWithAllFilters() throws Exception {
        Mockito.when(notificationSearchService.search(Mockito.any(NotificationDtoKey.class), Mockito.anyList(),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationList);
        Mockito.when(notificationCountService.find(Mockito.any(NotificationDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationCount);
        ApiResponse results = notificationApiController.getNotifications("01 Jan 2015", "30 Jan 2015", "clt_act," +
            "conf_trx,new_clt", "account", "acc1", "date,desc", paging, true);
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().size(), 3);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(0).getAccountId(),
            "acc1");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(1).getAccountId(),
            "acc3");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(2).getAccountId(),
            "acc2");
        Assert.assertEquals(((NotificationListDto) results.getData()).getPriorityCount(), 5);
        Assert.assertEquals(((NotificationListDto) results.getData()).getUnreadCount(), 10);
    }

    @Test
    public void testWithNullSearchFilters() throws Exception {
        Mockito.when(notificationSearchService.search(Mockito.any(NotificationDtoKey.class), Mockito.anyList(),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationList);
        Mockito.when(notificationCountService.find(Mockito.any(NotificationDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationCount);
        ApiResponse results = notificationApiController.getNotifications("01 Jan 2015", "30 Jan 2015", "clt_act," +
            "conf_trx,new_clt", null, null, "date,asc", paging, true);
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().size(), 3);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(2).getAccountId(),
            "acc1");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(1).getAccountId(),
            "acc3");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(0).getAccountId(),
            "acc2");
        Assert.assertEquals(((NotificationListDto) results.getData()).getPriorityCount(), 5);
        Assert.assertEquals(((NotificationListDto) results.getData()).getUnreadCount(), 10);
    }

    @Test
    public void testWithBlankSearchFilters() throws Exception {
        Mockito.when(notificationSearchService.search(Mockito.any(NotificationDtoKey.class), Mockito.anyList(),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationList);
        Mockito.when(notificationCountService.find(Mockito.any(NotificationDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationCount);
        ApiResponse results = notificationApiController.getNotifications("01 Jan 2015", "30 Jan 2015", "clt_act," +
            "conf_trx,new_clt", "", "", "date,asc", paging, true);
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().size(), 3);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(2).getAccountId(),
            "acc1");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(1).getAccountId(),
            "acc3");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(0).getAccountId(),
            "acc2");
        Assert.assertEquals(((NotificationListDto) results.getData()).getPriorityCount(), 5);
        Assert.assertEquals(((NotificationListDto) results.getData()).getUnreadCount(), 10);
    }

    @Test
    public void testWithAllNullFilters() throws Exception {
        Mockito.when(notificationSearchService.search(Mockito.any(NotificationDtoKey.class), Mockito.anyList(),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationList);
        Mockito.when(notificationCountService.find(Mockito.any(NotificationDtoKey.class),
            Mockito.any(ServiceErrors.class))).thenReturn(notificationCount);
        ApiResponse results = notificationApiController.getNotifications(null, null, null, null, null, null, null,
            false);
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().size(), 3);
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(0).getAccountId(),
            "acc1");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(1).getAccountId(),
            "acc2");
        Assert.assertEquals(((NotificationListDto) results.getData()).getNotificationList().get(2).getAccountId(),
            "acc3");
        Assert.assertEquals(((NotificationListDto) results.getData()).getPriorityCount(), 5);
        Assert.assertEquals(((NotificationListDto) results.getData()).getUnreadCount(), 10);
    }

    @Test
    public void testUpdateReadStatus() throws Exception {
        Mockito.when(notificationUpdateService.update(Mockito.any(NotificationUpdateDto.class),
            Mockito.any(ServiceErrors.class))).thenReturn(updated);
        ApiResponse results = notificationApiController.markNotificationsRead("id1");
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((NotificationUpdateDto) results.getData()).getStatus(), "Success");
    }

    @Test
    public void testUpdateUnreadStatus() {
        Mockito.when(notificationUpdateService.update(Mockito.any(NotificationUpdateDto.class),
                Mockito.any(ServiceErrors.class))).thenReturn(updated);
        ApiResponse results = notificationApiController.markNotificationsUnread("id1");
        Assert.assertNotNull(results);
        Assert.assertEquals(results.getApiVersion(), ApiVersion.CURRENT_VERSION);
        Assert.assertEquals(((NotificationUpdateDto) results.getData()).getStatus(), "Success");
    }

    private List<NotificationDto> getNotificationList() {
        List<NotificationDto> list = new ArrayList<>();
        list.add(getNotificationDto("acc1", 100));
        list.add(getNotificationDto("acc2", 50000));
        list.add(getNotificationDto("acc3", 10000));
        return list;
    }

    private NotificationDto getNotificationDto(String accountId, int minusBy) {
        NotificationDto dto = new NotificationDto();
        dto.setAccountId(accountId);
        dto.setDate(DateTime.now().minus(minusBy));
        return dto;
    }
}
