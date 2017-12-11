package com.bt.nextgen.service.avaloq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.core.security.integration.messages.NotificationSubCategory;

/**
 * @author L070589
 * 
 * Converter to map the notification sub category from static codes
 *
 */

@Component
public class NotificationSubCategoryConverter implements Converter <String, NotificationSubCategory>
{

	@Autowired
	StaticIntegrationService staticCodes;

	private static final Logger logger = LoggerFactory.getLogger(NotificationSubCategoryConverter.class);
	ServiceErrors serviceErrors;

	public NotificationSubCategory convert(String source)
	{
		try
		{
			serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.NOTIFICATION_SUB_CATGEORY, source, serviceErrors);

			return NotificationSubCategory.getCategory(code.getIntlId());
		}
		catch (Exception e)
		{
			logger.error("No Notification sub category found for: {}", source);
			serviceErrors.addError(new ServiceErrorImpl("Notification Sub category  is missing"));
		}
		return null;
	}
}
