package com.bt.nextgen.api.global.service;

import com.bt.nextgen.api.global.model.GlobalDetailsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.messages.NotificationIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationUnreadCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "springJpaTransactionManager")
class GlobalDetailsDtoServiceImpl implements GlobalDetailsDtoService {

    @Autowired
    private NotificationIntegrationService notificationService;

    @Override
    public GlobalDetailsDto findOne(ServiceErrors serviceErrors) {
        NotificationUnreadCount counts = notificationService.getUnReadNotification(serviceErrors);
        return new GlobalDetailsDto(counts.getTotalUnreadClientNotifications(),
                counts.getTotalUnreadMyNotifications());
    }
}
