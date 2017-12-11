package com.bt.nextgen.core.security.validator;

import org.opensaml.xml.security.x509.BasicX509Credential;

public interface TrustedCredentialProvider
{

	BasicX509Credential getTrustedCredential();

}
