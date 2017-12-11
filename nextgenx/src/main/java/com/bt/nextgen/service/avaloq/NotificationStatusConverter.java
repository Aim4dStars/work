package com.bt.nextgen.service.avaloq;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author L070354
 *         <p/>
 *         Converter to map the Notification status with static codes
 */

@Component
public class NotificationStatusConverter implements Converter<String, NotificationStatus> {

    //TODO - XXX - This is to enforce that caching happens before auto-wiring in this instance. Improvement Required
    @Autowired
    BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

    @Autowired
    private StaticIntegrationService staticCodes;

    private static final Logger logger = LoggerFactory.getLogger(NotificationStatusConverter.class);

    public NotificationStatus convert(String source) {
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        String statusCode = source;
        Code code = staticCodes.loadCode(CodeCategory.NOTIFICATION_STATUS, source, serviceErrors);
        if (code != null) {
            statusCode = code.getIntlId();
        } else {
            logger.error("No Notification status found for {}", source);
            serviceErrors.addError(new ServiceErrorImpl("Notification status is missing"));
        }
        return NotificationStatus.getStatus(statusCode);
    }
}
