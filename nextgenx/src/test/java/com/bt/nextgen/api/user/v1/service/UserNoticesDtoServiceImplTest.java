package com.bt.nextgen.api.user.v1.service;

import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.model.UserNoticesDtoKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.user.notices.model.*;
import com.bt.nextgen.service.integration.user.notices.repository.NoticesRepository;
import com.bt.nextgen.service.integration.user.notices.repository.UserNoticesRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class UserNoticesDtoServiceImplTest {

    @InjectMocks
    private UserNoticesDtoServiceImpl userUpdatesDtoService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserNoticesRepository userNoticesRepository;

    @Mock
    private NoticesRepository noticesRepository;

    private static final ServiceErrors serviceErrors = new ServiceErrorsImpl();
    private List<UserNotices> userNoticesList = new ArrayList<>();
    Map<NoticeType, Notices> latestUpdateMap;
    private List<Notices> latestUpdateList = new ArrayList<>();

    @Before
    public void setUp() {
        userNoticesList = new ArrayList<>();
        userNoticesList.add(new UserNotices(new UserNoticesKey("201608544", NoticeType.PDS, Integer.valueOf(1))));
        userNoticesList.add(new UserNotices(new UserNoticesKey("201608544", NoticeType.TERMS_OF_USE, Integer.valueOf(1))));
        userNoticesList.add(new UserNotices(new UserNoticesKey("201608545", NoticeType.PDS, Integer.valueOf(1))));
        userNoticesList.add(new UserNotices(new UserNoticesKey("201608545", NoticeType.TERMS_OF_USE, Integer.valueOf(1))));

        Mockito.when(userNoticesRepository.search(UserKey.valueOf("201608544"))).thenReturn(userNoticesList.subList(0, 2));
        Mockito.when(userNoticesRepository.search(UserKey.valueOf("201608545"))).thenReturn(userNoticesList.subList(2, 4));
    }

    @Test
    public void testFindAllWithOneUpdate() {
        latestUpdateList.add(new Notices(new NoticesKey(NoticeType.TERMS_OF_USE, Integer.valueOf(1)), "Some description"));
        latestUpdateList.add(new Notices(new NoticesKey(NoticeType.PDS, Integer.valueOf(1)), "Some description"));

        latestUpdateMap = new HashMap<>();
        latestUpdateMap.put(NoticeType.PDS, new Notices(new NoticesKey(NoticeType.PDS, Integer.valueOf(2)), "Some new description"));
        latestUpdateMap.put(NoticeType.TERMS_OF_USE, new Notices(new NoticesKey(NoticeType.TERMS_OF_USE, Integer.valueOf(1)), "Some new description"));

        Mockito.when(noticesRepository.findAll()).thenReturn(latestUpdateList);
        Mockito.when(userProfileService.getGcmId()).thenReturn("201608544");
        Mockito.when(noticesRepository.getLatestUpdatesMap()).thenReturn(latestUpdateMap);

        List<UserNoticesDto> result = userUpdatesDtoService.findAll(serviceErrors);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getKey().getNoticeId(), NoticeType.PDS.getId());
        assertEquals(result.get(0).getNoticeTypeName(), NoticeType.PDS.getDisplayText());
        assertEquals(result.get(0).getDescription(), latestUpdateMap.get(NoticeType.PDS).getDescription());
        assertEquals(result.get(0).getLastUpdatedOn().toDate(), latestUpdateMap.get(NoticeType.PDS).getLastUpdatedOn().toDate());
    }

    @Test
    public void testFindAllWithMultipleUpdates() {
        latestUpdateList.add(new Notices(new NoticesKey(NoticeType.TERMS_OF_USE, Integer.valueOf(1)), "Some description"));
        latestUpdateList.add(new Notices(new NoticesKey(NoticeType.PDS, Integer.valueOf(1)), "Some description"));

        latestUpdateMap = new HashMap<>();
        latestUpdateMap.put(NoticeType.PDS, new Notices(new NoticesKey(NoticeType.PDS, Integer.valueOf(2)), "Some new PDS description"));
        latestUpdateMap.put(NoticeType.TERMS_OF_USE, new Notices(new NoticesKey(NoticeType.TERMS_OF_USE, Integer.valueOf(2)), "Some new Terms description"));

        Mockito.when(noticesRepository.findAll()).thenReturn(latestUpdateList);
        Mockito.when(userProfileService.getGcmId()).thenReturn("201608545");
        Mockito.when(noticesRepository.getLatestUpdatesMap()).thenReturn(latestUpdateMap);

        List<UserNoticesDto> result = userUpdatesDtoService.findAll(serviceErrors);

        assertNotNull(result);
        assertEquals(result.size(), 2);

        for (UserNoticesDto userNoticesDto : result) {
            NoticeType noticeType = NoticeType.forNoticeId(userNoticesDto.getKey().getNoticeId());
            assertEquals(userNoticesDto.getKey().getNoticeId(), noticeType.getId());
            assertEquals(userNoticesDto.getNoticeTypeName(), noticeType.getDisplayText());
            assertEquals(userNoticesDto.getDescription(), latestUpdateMap.get(noticeType).getDescription());
            assertEquals(userNoticesDto.getLastUpdatedOn().toDate(), latestUpdateMap.get(noticeType).getLastUpdatedOn().toDate());
        }
    }

    @Test
    public void testUpdate() {
        latestUpdateList.add(new Notices(new NoticesKey(NoticeType.TERMS_OF_USE, Integer.valueOf(2)), "Some description"));
        latestUpdateList.add(new Notices(new NoticesKey(NoticeType.PDS, Integer.valueOf(2)), "Some description"));

        Mockito.when(userProfileService.getGcmId()).thenReturn("201608545");
        Mockito.when(noticesRepository.find(Mockito.any(NoticesKey.class))).thenReturn(latestUpdateList.get(1));

        UserNoticesDto userNoticesDto = new UserNoticesDto(new UserNoticesDtoKey("201608545", NoticeType.PDS.getId(), Integer.valueOf(1)));
        UserNoticesDto result = userUpdatesDtoService.update(userNoticesDto, serviceErrors);

        assertNotNull(result);
        assertEquals(result.getKey().getNoticeId(), NoticeType.PDS.getId());
        assertEquals(result.getNoticeTypeName(), NoticeType.PDS.getDisplayText());
    }
}
