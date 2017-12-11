package com.bt.nextgen.service.integration.user.repository;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.integration.user.notices.model.Notices;
import com.bt.nextgen.service.integration.user.notices.model.NoticesKey;
import com.bt.nextgen.service.integration.user.notices.model.NoticeType;
import com.bt.nextgen.service.integration.user.notices.repository.NoticesRepository;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;

@TransactionConfiguration
public class NoticesRepositoryTest extends BaseSecureIntegrationTest {

    @Autowired
    private NoticesRepository noticesRepository;

    private Integer version = Integer.valueOf(1);

    @Test
    @Transactional
    @Rollback(true)
    public void testFindAll() {
        List<Notices> noticesList = noticesRepository.findAll();

        Assert.assertThat(noticesList.size(),  greaterThan(0));
        Assert.assertEquals(NoticeType.TERMS_OF_USE, noticesList.get(0).getNoticesKey().getNoticeTypeId());
        Assert.assertEquals(version, noticesList.get(0).getNoticesKey().getVersion());
        Assert.assertEquals(new DateTime("2016-05-01").toDate(), noticesList.get(0).getLastUpdatedOn().toDate());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testFind() {
        Notices update = noticesRepository.find(new NoticesKey(NoticeType.PDS, version));

        Assert.assertNotNull(update);
        Assert.assertEquals(NoticeType.PDS, update.getNoticesKey().getNoticeTypeId());
        Assert.assertEquals(version, update.getNoticesKey().getVersion());
        Assert.assertEquals(new DateTime("2016-05-01").toDate(), update.getLastUpdatedOn().toDate());
    }

    @Test
    @Transactional
    @Rollback(true)
    public void testGetLatestUpdatesMap() {
        noticesRepository.save(new Notices(new NoticesKey(NoticeType.TERMS_OF_USE, Integer.valueOf(4)), ""));
        noticesRepository.save(new Notices(new NoticesKey(NoticeType.PDS, Integer.valueOf(3)), ""));

        Map<NoticeType, Notices> map = noticesRepository.getLatestUpdatesMap();
        Notices updatePds = map.get(NoticeType.PDS);
        Notices updateTerms = map.get(NoticeType.TERMS_OF_USE);

        Assert.assertNotNull(map);
        Assert.assertThat(map.size(),  greaterThan(0));

        Assert.assertEquals(NoticeType.PDS, updatePds.getNoticesKey().getNoticeTypeId());
        Assert.assertEquals(Integer.valueOf(3), updatePds.getNoticesKey().getVersion());

        Assert.assertEquals(NoticeType.TERMS_OF_USE, updateTerms.getNoticesKey().getNoticeTypeId());
        Assert.assertEquals(Integer.valueOf(4), updateTerms.getNoticesKey().getVersion());
    }
}
