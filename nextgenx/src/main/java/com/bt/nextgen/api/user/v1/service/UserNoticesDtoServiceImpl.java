package com.bt.nextgen.api.user.v1.service;

import com.bt.nextgen.api.user.v1.model.UserNoticesDto;
import com.bt.nextgen.api.user.v1.model.UserNoticesDtoKey;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.user.notices.model.*;
import com.bt.nextgen.service.integration.user.notices.repository.NoticesRepository;
import com.bt.nextgen.service.integration.user.notices.repository.UserNoticesRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("UserUpdatesDtoServiceV1")

/**
 * This service finds all the updates available to the user
 */
public class UserNoticesDtoServiceImpl implements UserNoticesDtoService {

    @Autowired
    private UserNoticesRepository userNoticesRepository;

    @Autowired
    private NoticesRepository noticesRepository;

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Returns all the updates available to the user
     *
     * @param serviceErrors
     * @return
     */
    @Override
    public List<UserNoticesDto> findAll(ServiceErrors serviceErrors) {
        final UserKey userKey = UserKey.valueOf(userProfileService.getGcmId());
        return buildUserUpdatesDto(userNoticesRepository.search(userKey), serviceErrors);
    }

    /**
     * This methods updates the userUpdate in the repository
     *
     * @param userNoticesDto - UserNoticesDto to be updated
     * @param serviceErrors
     * @return
     */
    @Override
    public UserNoticesDto update(UserNoticesDto userNoticesDto, ServiceErrors serviceErrors) {
        final UserNoticesKey userNoticesKey = new UserNoticesKey(userNoticesDto.getKey().getUserId(),
                NoticeType.forNoticeId(userNoticesDto.getKey().getNoticeId()), userNoticesDto.getKey().getVersion());
        userNoticesRepository.save(new UserNotices(userNoticesKey));
        final Notices latestUpdate = noticesRepository.find(new NoticesKey(userNoticesKey.getNoticeTypeId(), userNoticesKey.getVersion()));
        return createUserUpdatesDto(latestUpdate);
    }

    private List<UserNoticesDto> buildUserUpdatesDto(List<UserNotices> userNoticesList, ServiceErrors serviceErrors) {
        List<UserNoticesDto> userNoticesDtoList = new ArrayList<>();
        final Map<NoticeType, Notices> updatesMap = noticesRepository.getLatestUpdatesMap();
        // Remove duplicates with older versions
        final List<UserNotices> latestUserNotices = getLatestUserNotices(userNoticesList);
        if (CollectionUtils.isNotEmpty(latestUserNotices)) {
            for (UserNotices userNotices : latestUserNotices) {
                final UserNoticesKey userNoticesKey = userNotices.getUserNoticesKey();
                if (userNoticesKey != null) {
                    final Notices latestUpdate = updatesMap.get(userNoticesKey.getNoticeTypeId());
                    if (latestUpdate != null && isUpdateAvailable(userNoticesKey, latestUpdate)) {
                        // Add to the list of updates for the user
                        userNoticesDtoList.add(createUserUpdatesDto(latestUpdate));
                    }
                }
            }
        } else {
            //add all the latest entries for the user
            for (Map.Entry<NoticeType, Notices> entry : updatesMap.entrySet()) {
                update(createUserUpdatesDto(entry.getValue()), serviceErrors);
            }
        }
        return userNoticesDtoList;
    }

    private List<UserNotices> getLatestUserNotices(List<UserNotices> userNoticesList) {
        Map<NoticeType, UserNotices> userNoticesMap = new HashMap<>();
        UserNotices existing;
        for (UserNotices notice : userNoticesList) {
            existing = userNoticesMap.get(notice.getUserNoticesKey().getNoticeTypeId());
            if (existing == null || notice.getUserNoticesKey().getVersion() > existing.getUserNoticesKey().getVersion()) {
                userNoticesMap.put(notice.getUserNoticesKey().getNoticeTypeId(), notice);
            }
        }
        return new ArrayList<>(userNoticesMap.values());
    }

    private boolean isUpdateAvailable(UserNoticesKey userNoticesKey, Notices latestUpdate) {
        return latestUpdate.getNoticesKey().getVersion() > userNoticesKey.getVersion();
    }

    private UserNoticesDto createUserUpdatesDto(Notices latestUpdate) {
        final UserNoticesDtoKey key = new UserNoticesDtoKey(userProfileService.getGcmId().toString(),
                latestUpdate.getNoticesKey().getNoticeTypeId().getId(), latestUpdate.getNoticesKey().getVersion());

        return new UserNoticesDto(key, latestUpdate.getNoticesKey().getNoticeTypeId().getDisplayText(),
                latestUpdate.getDescription(), latestUpdate.getLastUpdatedOn());
    }
}