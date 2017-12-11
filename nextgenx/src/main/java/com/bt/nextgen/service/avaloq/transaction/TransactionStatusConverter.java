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
public class TransactionStatusConverter implements Converter <String, TransactionWorkflowStatus>
{
	@Autowired
	StaticIntegrationService staticCodes;
	
	private static final Logger logger = LoggerFactory.getLogger(TransactionStatusConverter.class);

	public TransactionWorkflowStatus convert(String source)
	{
		try
		{
			ServiceErrors serviceErrors = new ServiceErrorsImpl();
			Code code = staticCodes.loadCode(CodeCategory.PORTFOLIO_STATUS, source,
					serviceErrors);
			return TransactionWorkflowStatus.getPaymentStatus(code.getIntlId());
		}
		catch (Exception e)
		{
			logger.error("Error converting to TransactionWorkflow status", e);
		}
		return null;
	}
}
