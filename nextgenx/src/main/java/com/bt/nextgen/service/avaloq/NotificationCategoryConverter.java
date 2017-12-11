package com.bt.nextgen.service.avaloq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationCategory;

/**
 * @author L070354
 * 
 * Converter to map the notification category from static codes
 * @deprecated  Please use StaticCodeConverter
 */
@Deprecated
@Component
public class NotificationCategoryConverter implements Converter <String, NotificationCategory>
{
	//TODO - XXX - This is to enforce that caching happens before autowiring in this instance. Improvement Required
	@Autowired BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

	@Autowired
	StaticIntegrationService staticCodes;

	private static final Logger logger = LoggerFactory.getLogger(NotificationCategoryConverter.class);
	ServiceErrors serviceErrors;

	public NotificationCategory convert(String source)
	{
		try
		{
			serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.NOTIFICATION_CATGEORY, source, serviceErrors);

			return NotificationCategory.getCategory(code.getIntlId());
		}
		catch (Exception e)
		{
			logger.error("No Notification category found for: {}", source);
			serviceErrors.addError(new ServiceErrorImpl("Notification category  is missing"));
		}
		return null;
	}
}
