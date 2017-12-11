package com.bt.nextgen.core.security.validator;

import org.opensaml.saml2.core.Response;
import org.opensaml.xml.security.keyinfo.KeyInfoHelper;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SignatureValueMatchesPublicKeyValidator implements Validator<Response>
{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public void validate(Response response) throws ValidationException
	{
		Signature signature = response.getSignature();
		try
		{
			for (X509Certificate certificate : KeyInfoHelper.getCertificates(signature.getKeyInfo()))
			{
				certificate.checkValidity();
				validateSignature(certificate, signature);
			}
		}
		catch (CertificateException e)
		{
			log.error(e.getMessage());
			throw new ValidationException(e);
		}
	}

	private void validateSignature(X509Certificate certificate, Signature signature) throws ValidationException
	{
		BasicX509Credential credential = new BasicX509Credential();
		credential.setEntityCertificate(certificate);
		new SignatureValidator(credential).validate(signature);
	}
}
