package com.bt.nextgen.service.integration.user.notices.repository;

import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.user.notices.model.UserNotices;
import com.bt.nextgen.service.integration.user.notices.model.UserNoticesKey;

import java.util.List;

public interface UserNoticesRepository {

    List<UserNotices> search(UserKey userKey);

    UserNotices find(UserNoticesKey key);

    UserNotices save(UserNotices userNotices);
}
