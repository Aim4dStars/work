package com.bt.nextgen.core.security.validator;

import com.bt.nextgen.core.security.exception.InvalidIssueTimeException;
import com.bt.nextgen.core.util.Clock;

import org.joda.time.DateTime;
import org.opensaml.saml2.core.*;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class SamlResponseValidator implements Validator<Response>
{
	protected static final String BEARER_CONFIRMATION = "urn:oasis:names:tc:SAML:2.0:cm:bearer";

	private static final Logger logger = LoggerFactory.getLogger(SamlResponseValidator.class);
	
	private static int LOW_BUFFER_IN_SECONDS = (int) (3.5d * 60);
	private static int UP_BUFFER_IN_SECONDS = (int) (3.5d * 60);

	private DateTime now()
	{
		return Clock.get().now();
	}

	private void assertNotNull(Response samlResponse) throws ValidationException
	{
		if (samlResponse == null)
		{
			throw new ValidationException("Unspecified SAML parameters.");
		}
	}

	public void validate(Response samlResponse) throws ValidationException
	{
		// Should we use Saml Validators for this?
		// docs: https://spaces.internet2.edu/display/OpenSAML/OSTwoUserManJavaValidation

		// this.samlResponse.registerValidator(new IssuerSchemaValidator());
		// this.samlResponse.validate(true);
		//
		// org.opensaml.Configuration.get
		// c.getValidatorSuite("");

		assertNotNull(samlResponse);
		verifyStatusCode(samlResponse);
		new SamlInstant(samlResponse.getIssueInstant(), LOW_BUFFER_IN_SECONDS, UP_BUFFER_IN_SECONDS).validate();
		verifyDestination(samlResponse);
		verifyIssuer(samlResponse);
		verifyAssertion(samlResponse);
	}

	private void verifyAssertion(Response samlResponse) throws ValidationException
	{
		Assertion subjectAssertion = null;
		List<Assertion> assertionList = samlResponse.getAssertions();
		for (Assertion a : assertionList)
		{
			verifyAssertion(a);
			if (a.getAuthnStatements().size() > 0)
			{
				if (a.getSubject() != null && a.getSubject().getSubjectConfirmations() != null)
				{
					for (SubjectConfirmation conf : a.getSubject().getSubjectConfirmations())
					{
						if (BEARER_CONFIRMATION.equals(conf.getMethod()))
						{
							subjectAssertion = a;
						}
					}
				}
			}
		}

		// Make sure that at least one assertion contains authentication statement and subject with bearer cofirmation
		if (subjectAssertion == null)
		{
			throw new ValidationException("Error validating SAML response");
		}
	}

	private void verifyIssuer(Response samlResponse) throws ValidationException
	{
		verifyIssuer(samlResponse.getIssuer());
	}

	private void verifyDestination(Response samlResponse) throws ValidationException
	{
		if (samlResponse.getDestination() == null)
		{
			throw new ValidationException("Error validating SAML response");
		}
	}

	private void verifyStatusCode(Response samlResponse) throws ValidationException
	{
		if (!StatusCode.SUCCESS_URI.equals(samlResponse.getStatus().getStatusCode().getValue()))
		{
			throw new ValidationException("SAML status is not success code");
		}
	}

	private void verifyAssertion(Assertion assertion) throws ValidationException
	{
		new SamlInstant(assertion.getIssueInstant(), LOW_BUFFER_IN_SECONDS, UP_BUFFER_IN_SECONDS).validate();
		verifyIssuer(assertion.getIssuer());
		verifySubject(assertion.getSubject());

		// Assertion with authentication statement must contain audience restriction
		if (assertion.getAuthnStatements().size() > 0)
		{
			verifyAssertionConditions(assertion.getConditions());
			for (AuthnStatement statement : assertion.getAuthnStatements())
			{
				verifyAuthenticationStatement(statement);
			}
		}
		else
		{
			verifyAssertionConditions(assertion.getConditions());
		}
	}

	protected void verifyAuthenticationStatement(AuthnStatement auth)
	{
		new SamlInstant(auth.getAuthnInstant(), LOW_BUFFER_IN_SECONDS, UP_BUFFER_IN_SECONDS).validate();

		// Validate users session is still valid
		if (auth.getSessionNotOnOrAfter() != null && auth.getSessionNotOnOrAfter().isBefore(now()))
		{ //franklin
			//        if (auth.getSessionNotOnOrAfter() != null && auth.getSessionNotOnOrAfter().isAfter(now())) { //spring-saml code
			throw new InvalidIssueTimeException("Users authentication is expired");
		}
	}

	private void verifyAssertionConditions(Conditions conditions) throws ValidationException
	{
		// If no conditions are implied, assertion is deemed valid
		if (conditions == null)
		{
			return;
		}

		if (conditions.getNotBefore() != null)
		{
			if (conditions.getNotBefore().minusSeconds(LOW_BUFFER_IN_SECONDS).isAfter(now()))
			{
				throw new ValidationException(
					String.format("SAML response is not valid [now: %s / NotBefore: %s]", now(),
						conditions.getNotBefore()));
			}
		}
		if (conditions.getNotOnOrAfter() != null)
		{
			if (conditions.getNotOnOrAfter().isBefore(now()))
			{
				throw new ValidationException("SAML response is not valid");
			}
		}
	}

	protected void verifySubject(Subject subject) throws ValidationException
	{
		boolean confirmed = false;
		for (SubjectConfirmation confirmation : subject.getSubjectConfirmations())
		{
			if (BEARER_CONFIRMATION.equals(confirmation.getMethod()))
			{
				SubjectConfirmationData data = confirmation.getSubjectConfirmationData();
				// Bearer must have confirmation 554
				logger.error("confirmation data is {}",data);
				if (data == null)
				{
					logger.error("confirmation data is null");
					throw new ValidationException("SAML Assertion is invalid");
				}

				// Not before forbidden by core 558
				if (data.getNotBefore() != null)
				{
					throw new ValidationException("SAML Assertion is invalid");
				}

				// Validate not on or after
				DateTime notOnOrAfter = data.getNotOnOrAfter();
				if (notOnOrAfter.isBefore(now()))
				{
					confirmed = false;
					continue;
				}
				confirmed = true;
			}
		}

		if (!confirmed)
		{
			throw new ValidationException("SAML Assertion is invalid");
		}
	}

	protected void verifyIssuer(Issuer issuer) throws ValidationException
	{
		if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY))
		{
			throw new ValidationException("SAML Assertion is invalid");
		}
	}
}
