package com.bt.nextgen.service.avaloq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

/**
 * @author L070815
 * 
 * Converter to map the Currency Type with static codes
 */

@Component
public class CurrencyTypeConverter implements Converter <String, CurrencyType>
{

	@Autowired
	StaticIntegrationService staticCodes;

	private static final Logger logger = LoggerFactory.getLogger(CurrencyTypeConverter.class);
	ServiceErrors serviceErrors;

	public CurrencyType convert(String source)
	{

		try
		{
			serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.CURRENCY_TYPE, source, serviceErrors);
			return CurrencyType.getCurrencyType(code.getIntlId());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("No Currency type found for {}", source);
			serviceErrors.addError(new ServiceErrorImpl("CurrencyType is missing"));
		}
		return null;
	}
}
