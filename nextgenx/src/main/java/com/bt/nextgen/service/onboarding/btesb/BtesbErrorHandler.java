package com.bt.nextgen.service.onboarding.btesb;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

import ns.btfin_com.sharedservices.integration.common.response.errorresponsetype.v3.ErrorResponseType;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;

import static java.util.Collections.singletonList;

public class BtesbErrorHandler 
{
	private static final Logger logger = LoggerFactory.getLogger(BtesbErrorHandler.class);

	public static ServiceErrors parseErrorResponse(List<? extends ErrorResponseType> errors)
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		for(ErrorResponseType error: errors)
		{
			ServiceError serviceError = new ServiceErrorImpl();
			serviceError.setId(error.getSubCode());
			serviceError.setMessage(error.getDescription());
            if(error.getCode()!=null)
			    serviceError.setType(error.getCode().value());
            logger.debug("Setting ServiceError :{}",serviceError);
			serviceErrors.addError(serviceError);
		}
		return serviceErrors;
	}

	public static ServiceErrors parseErrorRsp(ErrorResponseType error)
	{
		return parseErrorResponse(singletonList(error));
	}
}
