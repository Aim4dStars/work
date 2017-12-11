package com.bt.nextgen.service.integration.messages;

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

/**
 * @author L070354
 * 
 * Converter to map the notification event from static codes
 *
 */

@Component
public class NotificationEventConverter implements Converter <String, String>
{

	@Autowired
	StaticIntegrationService staticCodes;

	private static final Logger logger = LoggerFactory.getLogger(NotificationEventConverter.class);
	ServiceErrors serviceErrors;

	public String convert(String source)
	{

		try
		{
			serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.NOTIFICATION_EVENT_TYPE, source, serviceErrors);

			return code.getName();
		}
		catch (Exception e)
		{
			logger.error("No Notification event type found for: {} ", source);
			serviceErrors.addError(new ServiceErrorImpl("Notification Event Type is missing"));
		}
		return null;
	}
}
