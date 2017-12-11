package com.bt.nextgen.service.avaloq.fundpaymentnotice;

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

/**
 * This converter has been defined to fetch the Distribution Codes from the static tables  
 *
 */

@Component
public class DistributionComponentConverter implements Converter <String, String>
{

	//TODO - XXX - This is to enforce that caching happens before autowiring in this instance. Improvement Required
	@Autowired
	BeanFactoryCacheOperationSourceAdvisor waitForCachingAspect;

	@Autowired
	StaticIntegrationService staticCodes;

	private static final Logger logger = LoggerFactory.getLogger(DistributionComponentConverter.class);
	ServiceErrors serviceErrors;

	public String convert(String source)
	{

		try
		{
			serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.DISTRIBUTION_COMPONENT, source, serviceErrors);
			return code.getName();
		}
		catch (Exception e)
		{
			logger.error("No  status found for {}", source);
			serviceErrors.addError(new ServiceErrorImpl(" DistributionComponent Code is missing"));
		}
		return null;
	}
}
