package com.bt.nextgen.core.jms.cacheinvalidation;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.core.cache.KeyGetter;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountImpl;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("BTFG$UI_NTFCN_LIST.USER#UNREAD_CNT")
public class NotificationUnReadCountInvalidationExecutor implements TemplateBasedInvalidationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(NotificationUnReadCountInvalidationExecutor.class);

    @Autowired
    private GenericCache cache;

    @Override
    //Get the updated values from InvalidationNotification message and update cache
    public void execute(InvalidationNotification invalidationMessage) {

        logger.debug("Received Invalidation Message for Notification Unread Count..");
        final String userProfileCacheKey = invalidationMessage.getParamValList().get(0);
        final int totalNotifictionCountMy = NumberUtils.toInt(invalidationMessage.getParamValList().get(1));
        final int totalNotifictionCountClient =  NumberUtils.toInt(invalidationMessage.getParamValList().get(2));
        logger.info("Invalidation Message Details:[User Profile Id:{} Client Message Count:{} My Message Count:{}]",userProfileCacheKey ,
                totalNotifictionCountClient, totalNotifictionCountMy);
        NotificationUnreadCount notificationUnreadCount = new NotificationUnreadCountImpl(totalNotifictionCountClient, totalNotifictionCountMy);
        cache.put(notificationUnreadCount, CacheType.NOTIFICATION_COUNT_UNREAD, new KeyGetter() {
            @Override
            public Object getKey(Object obj) {
                return userProfileCacheKey;
            }
        });
        logger.info("Stored Unread notification count for (profile = {}) into cache {}", userProfileCacheKey,
                CacheType.NOTIFICATION_COUNT_UNREAD);
    }

}