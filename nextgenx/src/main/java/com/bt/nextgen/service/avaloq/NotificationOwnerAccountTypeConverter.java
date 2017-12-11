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
import com.btfin.panorama.core.security.integration.messages.NotificationOwnerAccountType;

/**
 * @author L070354
 *	
 * Converter to map the notification owner account type with static codes
 */

@Component
public class NotificationOwnerAccountTypeConverter implements Converter <String, NotificationOwnerAccountType>
{

	//TODO - XXX - This is to enforce that caching happens before autowiring in this instance. Improvement Required
	@Autowired BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

	@Autowired
	StaticIntegrationService staticCodes;

	private static final Logger logger = LoggerFactory.getLogger(NotificationOwnerAccountTypeConverter.class);
	ServiceErrors serviceErrors;

	public NotificationOwnerAccountType convert(String source)
	{

		serviceErrors = new ServiceErrorsImpl();

		try
		{
			Code code = staticCodes.loadCode(CodeCategory.ACCOUNT_STRUCTURE_TYPE, source, serviceErrors);
			return NotificationOwnerAccountType.getOwner(code.getIntlId());
		}
		catch (Exception e)
		{
			logger.error("No Notification owner account type found for {}", source);
			serviceErrors.addError(new ServiceErrorImpl("Notification Owner Account Type is missing"));
		}
		return null;
	}
}
