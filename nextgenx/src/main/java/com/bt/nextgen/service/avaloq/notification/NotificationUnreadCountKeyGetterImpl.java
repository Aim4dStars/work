package com.bt.nextgen.service.avaloq.notification;

import com.bt.nextgen.core.cache.KeyGetter;
import com.bt.nextgen.service.avaloq.AbstractUserCachedAvaloqIntegrationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationUnreadCountKeyGetterImpl extends AbstractUserCachedAvaloqIntegrationService implements KeyGetter {

    @Override
    public Object getKey(Object obj) {
        return getActiveProfileCacheKey();
    }

}
