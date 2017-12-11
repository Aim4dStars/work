package com.bt.nextgen.core.security.validator;

import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.x509.BasicX509Credential;

import java.security.*;
import java.security.cert.X509Certificate;


public class StaticLoadKeyStoreTrustedCredentialProvider implements TrustedCredentialProvider
{
	private final String trustedAlias;

	public StaticLoadKeyStoreTrustedCredentialProvider(String trustedAlias)
	{
		this.trustedAlias = trustedAlias;
	}

	public X509Certificate getCertificateFromKeyStore(String alias) throws KeyStoreException
	{
		if (alias == null || alias.length() == 0)
		{
			return null;
		}
		return (X509Certificate) getKeyStore().getCertificate(alias);
	}

	public KeyStore getKeyStore()
	{
		try
		{
			return KeyStore.getInstance(KeyStore.getDefaultType());
		}
		catch (KeyStoreException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public BasicX509Credential getTrustedCredential()
	{
		try
		{
			return this.getCredential(trustedAlias, "TODO");
		}
		catch (UnrecoverableKeyException e)
		{
			throw new RuntimeException(e);
		}
		catch (KeyStoreException e)
		{
			throw new RuntimeException(e);
		}
		catch (NoSuchAlgorithmException e)
		{
			throw new RuntimeException(e);
		}
	}

	private BasicX509Credential getCredential(String alias,
		String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException
	{
		java.security.cert.Certificate certificate = getKeyStore().getCertificate(alias);

		X509Certificate x509certificate = (X509Certificate) certificate;
		PublicKey publicKey = certificate.getPublicKey();
		BasicX509Credential credential = new BasicX509Credential();
		credential.setUsageType(UsageType.SIGNING);
		credential.setEntityCertificate(x509certificate);
		credential.setPublicKey(publicKey);

		return credential;
	}

}
