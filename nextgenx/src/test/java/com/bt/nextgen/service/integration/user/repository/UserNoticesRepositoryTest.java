package com.bt.nextgen.service.integration.user.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.user.UserKey;
import com.bt.nextgen.service.integration.user.notices.model.NoticeType;
import com.bt.nextgen.service.integration.user.notices.model.UserNotices;
import com.bt.nextgen.service.integration.user.notices.model.UserNoticesKey;
import com.bt.nextgen.service.integration.user.notices.repository.UserNoticesRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@TransactionConfiguration
public class UserNoticesRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    private UserNoticesRepository userNoticesRepository;
    private Integer version = new Integer(1);

    @Test
    @Transactional
    @Rollback(true)
    public void testSearchUserUpdates() throws Exception {
        List<UserNotices> userUpdates = userNoticesRepository.search(UserKey.valueOf("201608544"));
        Assert.assertTrue(userUpdates.isEmpty());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testSaveUserUpdate() {
        UserNotices update = new UserNotices(new UserNoticesKey("201608544", NoticeType.PDS, version));
        userNoticesRepository.save(update);
        UserNotices userNotices = userNoticesRepository.find(new UserNoticesKey("201608544", NoticeType.PDS, version));

        Assert.assertEquals("201608544", userNotices.getUserNoticesKey().getUserId());
        Assert.assertEquals(NoticeType.PDS, userNotices.getUserNoticesKey().getNoticeTypeId());
        Assert.assertEquals(update.getLastViewedOn(), userNotices.getLastViewedOn());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testFindUserUpdates() {
        UserNotices update1 = new UserNotices(new UserNoticesKey("201608544", NoticeType.PDS, version));
        UserNotices update2 = new UserNotices(new UserNoticesKey("201608544", NoticeType.TERMS_OF_USE, version));
        userNoticesRepository.save(update1);
        userNoticesRepository.save(update2);

        UserNotices userNotices = userNoticesRepository.find(new UserNoticesKey("201608544", NoticeType.PDS, version));
        Assert.assertNotNull(userNotices);
        Assert.assertEquals("201608544", userNotices.getUserNoticesKey().getUserId());
        Assert.assertEquals(NoticeType.PDS, userNotices.getUserNoticesKey().getNoticeTypeId());
        Assert.assertEquals(update1.getLastViewedOn(), userNotices.getLastViewedOn());
    }
}
