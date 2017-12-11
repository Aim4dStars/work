package com.bt.nextgen.core.security.validator;

import org.opensaml.saml2.core.Response;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class SamlResponseCompositeValidator implements Validator<Response>
{

	private final List<Validator> validators;
	private static final Logger logger = LoggerFactory.getLogger(SamlResponseCompositeValidator.class);
	
	public SamlResponseCompositeValidator(List<Validator> validators)
	{
		this.validators = validators;
	}

	public SamlResponseCompositeValidator()
	{
		this.validators = Collections.emptyList();
	}

	@Override
	public void validate(Response response) throws ValidationException
	{
		
		try
		{
			for (Validator validator : validators)
			{
				validator.validate(response);
			}
		}
		catch(ValidationException err)
		{
			
			logger.error("failed to validate SAML",err);
			throw err;
		}

	}
}
