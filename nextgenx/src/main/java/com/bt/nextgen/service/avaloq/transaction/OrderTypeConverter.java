package com.bt.nextgen.service.avaloq.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

@Component
public class OrderTypeConverter implements Converter <String, String>
{

	@Autowired
	StaticIntegrationService staticCodes;
	
	private static final Logger logger = LoggerFactory.getLogger(OrderTypeConverter.class);

	public String convert(String source)
	{
		try
		{
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.ORDER_TYPE, source,
					serviceErrors);
			return code.getName();
		}
		catch (Exception e)
		{
			logger.error("Error converting to String to orderType", e);
		}
		return source;
	}
}
