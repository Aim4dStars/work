package com.bt.nextgen.service.avaloq.notification;

import com.bt.nextgen.core.cache.CacheType;
import com.bt.nextgen.core.cache.GeneralEhCacheImpl;
import com.bt.nextgen.core.cache.GenericCache;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractUserCachedAvaloqIntegrationService;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountImpl;
import com.bt.nextgen.service.avaloq.NotificationUnreadCountResponseImpl;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCountResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;


/**
 * Notification Count service using cache.
 */
@Service("notificationCountCacheService")
@SuppressWarnings({"squid:S1200", "squid:S1948", "squid:S1068"})
public class NotificationCountCacheService extends AbstractUserCachedAvaloqIntegrationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationCountCacheService.class);

    /**
     * Type of cache used by the service.
     */
    private static final CacheType CACHE_TYPE = CacheType.NOTIFICATION_COUNT_UNREAD;

   /* @Autowired
    private UserProfileService userProfileService;*/

    @Autowired
    private AvaloqExecute avaloqExecute;

    private NotificationUnreadCountResponse response;

    @Autowired
    private NotificationUnreadCountKeyGetterImpl notificationUnreadCountKeyGetterImpl;
    /**
     * Autowire BeanFactoryTransactionAttributeSourceAdvisor so that
     * Spring executes {@link GenericCache} methods annotated as transactional in a transaction.
     *
     * @see {@link GeneralEhCacheImpl}
     */
    @Autowired
    private BeanFactoryTransactionAttributeSourceAdvisor transactionAnnotationWaiter;

    /**
     * Underlying cache.
     */
    @Autowired
    private GenericCache cache;


    /**
     * Get notification unread total count from cache by User Job ProfileId as Key.
     * In absence of value in cache, retrieve it from Avaloq and store it
     */

    public NotificationUnreadCount getNotificationCount(final ServiceErrors serviceErrors) {

        final String userProfileCacheKey = getActiveProfileCacheKey();
        NotificationUnreadCount notificationUnreadCount = (NotificationUnreadCountImpl) cache.get(CACHE_TYPE, userProfileCacheKey, null);
        if (null == notificationUnreadCount) {
            log.debug("NotificationUnreadCount Cache Empty: Retrieving count from Avaloq...");
            response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(Template.GET_NOTIFICATION_COUNT_UNREAD.getName()),
                    NotificationUnreadCountResponseImpl.class,
                    serviceErrors);
            notificationUnreadCount = new NotificationUnreadCountImpl(response.getTotalUnreadClientNotifications(), response.getTotalUnreadMyNotifications());
            cache.put(notificationUnreadCount, CACHE_TYPE, notificationUnreadCountKeyGetterImpl);

        } else {
            log.debug("NotificationUnreadCount loaded from Cache...");
        }

        return notificationUnreadCount;
    }

    /**
     * Get notification unread count (priority and non priority) from Avaloq
     * Put the total unread count total in cache
     */

    public NotificationUnreadCountResponse getDetailedNotificationCount(final ServiceErrors serviceErrors) {
        response = avaloqExecute.executeReportRequestToDomain(new AvaloqReportRequest(Template.GET_NOTIFICATION_COUNT_UNREAD.getName()),
                NotificationUnreadCountResponseImpl.class,
                serviceErrors);
        NotificationUnreadCount notificationUnreadCount = new NotificationUnreadCountImpl(response.getTotalUnreadClientNotifications(), response.getTotalUnreadMyNotifications());

        cache.put(notificationUnreadCount, CACHE_TYPE, notificationUnreadCountKeyGetterImpl);

        return response;
    }
}
