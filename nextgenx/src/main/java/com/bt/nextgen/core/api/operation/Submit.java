package com.bt.nextgen.core.api.operation;

import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.core.api.exception.ApiException;
import com.bt.nextgen.core.api.exception.ApiValidationException;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.bt.nextgen.core.api.validation.ApiValidation;
import com.bt.nextgen.core.api.validation.ErrorMapper;
import com.bt.nextgen.core.exception.ValidationException;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.ws.soap.saaj.SaajSoapEnvelopeException;

/**
 * Delegates to the supplied dto service to execute the submission of an object.
 * Will return appropriate 400 error when the necessary inputs have not been supplied
 * Will return appropriate 404 error if the service returns null
*/
public class Submit <K, T extends KeyedDto <K>> implements ControllerOperation
{
	private String version;
	private SubmitDtoService <K, T> service;
	private ErrorMapper mapper;
	private T submitObject;

	public Submit(String version, SubmitDtoService <K, T> service, ErrorMapper mapper, T submitObject)
	{
		this.version = version;
		this.service = service;
		this.mapper = mapper;
		this.submitObject = submitObject;
	}

	public KeyedApiResponse <K> performOperation()
	{
		try
		{
			ApiValidation.preconditionNotNull(version, submitObject);
			ServiceErrors serviceErrors = new FailFastErrorsImpl();
			KeyedApiResponse <K> result = new KeyedApiResponse <K>(version, submitObject.getKey(), service.submit(submitObject,
				serviceErrors));
			ApiValidation.postConditionDataNotNull(version, result);
			ApiValidation.postConditionNoServiceErrors(version, serviceErrors);
			return result;
		}
		catch (ValidationException ex)
		{
			throw new ApiValidationException(version, mapper.map(ex.getErrors()), ex);
		}
		catch (com.bt.nextgen.core.exception.ServiceException e)
		{
			throw new ServiceException(version, e.getServiceErrors(), e);
		}
		catch (SaajSoapEnvelopeException e)
		{
			throw new ApiException(version, "Please check if adviser/investor details provided in the application are valid as expected by ICC", e);
		}
		catch (RuntimeException e)
		{
			if (!(e instanceof ApiException))
			{
				throw new ApiException(version, e.getMessage(), e);
			}
			throw e;
		}
	}
}
