package com.bt.nextgen.core.jms.cacheinvalidation.executor;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GeneralEhCacheImpl;
import com.bt.nextgen.core.cache.KeyGetter;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotification;
import com.bt.nextgen.core.jms.cacheinvalidation.InvalidationNotificationImpl;
import com.bt.nextgen.core.jms.cacheinvalidation.NotificationUnReadCountInvalidationExecutor;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

@RunWith(MockitoJUnitRunner.class)
public class NotificationUnReadCountInvalidationExecutorTest {

    @InjectMocks
    NotificationUnReadCountInvalidationExecutor notificationUnReadCountInvalidationExecutor;

    @Mock
    private GeneralEhCacheImpl cache;

    InvalidationNotification intermediaryInvalidationNotification;

    InvalidationNotification investorInvalidationNotification;

    @Captor
    private ArgumentCaptor<NotificationUnreadCount> notificationArgumentCaptor;

    @Captor
    private ArgumentCaptor<KeyGetter> keyGetterArgumentCaptor;

    @Before
    public void setup() throws Exception {
        ClassPathResource classPathResource = new ClassPathResource("/webservices/response/notification/ntfcn_unread_cnt_invalidation_custr.xml");
        String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));
        DefaultResponseExtractor<InvalidationNotificationImpl> defaultResponseExtractor = new DefaultResponseExtractor<>(InvalidationNotificationImpl.class);
        investorInvalidationNotification = defaultResponseExtractor.extractData(content);

       classPathResource = new ClassPathResource("/webservices/response/notification/ntfcn_unread_cnt_invalidation_not_custr.xml");
        content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));
       defaultResponseExtractor = new DefaultResponseExtractor<>(InvalidationNotificationImpl.class);
        intermediaryInvalidationNotification = defaultResponseExtractor.extractData(content);


    }

    @Test
    public void test_execute_customer() {

        notificationUnReadCountInvalidationExecutor.execute(investorInvalidationNotification);

        Mockito.verify(cache).put(notificationArgumentCaptor.capture(), any(CacheType.class), keyGetterArgumentCaptor.capture());
        NotificationUnreadCount notificationUnreadCount = notificationArgumentCaptor.getValue();
        KeyGetter keyGetter = keyGetterArgumentCaptor.getValue();

        assertThat(notificationUnreadCount.getTotalUnreadClientNotifications(), is(Integer.valueOf("0")));
        assertThat(notificationUnreadCount.getTotalUnreadMyNotifications(), is(Integer.valueOf("17")));
        assertThat(notificationUnreadCount.getTotalNotifications(), is(Integer.valueOf("17")));
        assertThat(keyGetter.getKey(new Object()).toString(), is("17490"));
    }

    @Test
    public void test_execute_non_customer() {

        notificationUnReadCountInvalidationExecutor.execute(intermediaryInvalidationNotification);

        Mockito.verify(cache).put(notificationArgumentCaptor.capture(), any(CacheType.class), keyGetterArgumentCaptor.capture());
        NotificationUnreadCount notificationUnreadCount = notificationArgumentCaptor.getValue();
        KeyGetter keyGetter = keyGetterArgumentCaptor.getValue();

        assertThat(notificationUnreadCount.getTotalUnreadClientNotifications(), is(Integer.valueOf("104")));
        assertThat(notificationUnreadCount.getTotalUnreadMyNotifications(), is(Integer.valueOf("4")));
        assertThat(notificationUnreadCount.getTotalNotifications(), is(Integer.valueOf("108")));
        assertThat(keyGetter.getKey(new Object()).toString(), is("17404"));
    }
}
