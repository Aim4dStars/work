package com.bt.nextgen.service.integration.user.notices.repository;

import com.bt.nextgen.service.integration.user.notices.model.Notices;
import com.bt.nextgen.service.integration.user.notices.model.NoticesKey;
import com.bt.nextgen.service.integration.user.notices.model.NoticeType;

import java.util.List;
import java.util.Map;

public interface NoticesRepository {

    List<Notices> findAll();

    Notices find(NoticesKey noticesKey);

    Map<NoticeType, Notices> getLatestUpdatesMap();

    Notices save(Notices update);
}
