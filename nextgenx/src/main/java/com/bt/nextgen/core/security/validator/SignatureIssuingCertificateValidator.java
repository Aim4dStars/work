package com.bt.nextgen.core.security.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityHelper;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xml.security.x509.BasicPKIXValidationInformation;
import org.opensaml.xml.security.x509.PKIXValidationInformation;
import org.opensaml.xml.security.x509.StaticPKIXValidationInformationResolver;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.impl.PKIXSignatureTrustEngine;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Set;

import static java.util.Arrays.asList;

public class SignatureIssuingCertificateValidator implements Validator<Response>
{

	private Log logger = LogFactory.getLog(this.getClass());

	// Business/Design decision was not to validate revoked certificates.
	private static final Collection<X509CRL> CERTIFICATE_REVOCATION_LIST = null;
	private static final Set<String> TRUSTED_NAMES = null;
	private static final int CERTIFICATE_CHAIN_DEPTH = 1;
	private PKIXSignatureTrustEngine validationEngine;
	private final TrustedCredentialProvider trustedCredentialProvider;

	public SignatureIssuingCertificateValidator(TrustedCredentialProvider trustedCredentialProvider)
	{
		this.trustedCredentialProvider = trustedCredentialProvider;
	}

	private PKIXSignatureTrustEngine buildValidationEngine(X509Certificate issuingCertificate)
	{
		PKIXValidationInformation validationInformation = new BasicPKIXValidationInformation(asList(issuingCertificate),
			CERTIFICATE_REVOCATION_LIST, CERTIFICATE_CHAIN_DEPTH);
		StaticPKIXValidationInformationResolver validationInformationResolver = new StaticPKIXValidationInformationResolver(
			asList(validationInformation), TRUSTED_NAMES);

		KeyInfoCredentialResolver keyInfoCredentialResolver = SecurityHelper.buildBasicInlineKeyInfoResolver();

		return new PKIXSignatureTrustEngine(validationInformationResolver, keyInfoCredentialResolver);
	}

	@Override
	public void validate(Response response) throws ValidationException
	{
		CriteriaSet criteriaSet = new CriteriaSet(new EntityIDCriteria(response.getID()));
		try
		{
			Signature signature = response.getSignature();
			if (!getValidationEngine().validate(signature, criteriaSet))
			{
				ValidationException validationException = new ValidationException(
					"Saml certificates didn't validate against the chain of trust.");
				logger.error(validationException);
				throw validationException;
			}
		}
		catch (java.lang.SecurityException e)
		{
			throw new ValidationException(e);
		}
		catch (org.opensaml.xml.security.SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}

	public PKIXSignatureTrustEngine getValidationEngine()
	{
		if (validationEngine == null)
		{
			validationEngine = buildValidationEngine(
				trustedCredentialProvider.getTrustedCredential().getEntityCertificate());
		}
		return validationEngine;
	}
}
