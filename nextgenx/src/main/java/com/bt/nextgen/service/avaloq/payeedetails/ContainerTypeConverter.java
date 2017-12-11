package com.bt.nextgen.service.avaloq.payeedetails;

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
public class ContainerTypeConverter implements Converter <String, String>
{
	private static final Logger logger = LoggerFactory.getLogger(ContainerTypeConverter.class);

	@Autowired
	StaticIntegrationService staticService;
	
	public String convert(String source)
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		try
		{
			Code code = staticService.loadCode(CodeCategory.CONTAINER_TYPE, source, serviceErrors);
			return code.getIntlId();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("Static code cannot be resolved for input {}", source);
		}
		return source;
	}

}
