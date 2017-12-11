package com.bt.nextgen.core.security.validator;

import org.opensaml.saml2.core.Response;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

public class SignatureProfileValidator implements Validator<Response>
{

	private final SAMLSignatureProfileValidator samlSignatureProfileValidator;

	public SignatureProfileValidator(SAMLSignatureProfileValidator samlSignatureProfileValidator)
	{
		this.samlSignatureProfileValidator = samlSignatureProfileValidator;
	}

	@Override
	public void validate(Response response) throws ValidationException
	{
		if (response.getSignature() == null)
		{
			throw new ValidationException("Response Signature can't be empty");
		}

		samlSignatureProfileValidator.validate(response.getSignature());
	}
}
