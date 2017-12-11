package com.bt.nextgen.core.security;

import com.btfin.panorama.core.security.saml.*;

@Deprecated
public class FakeServerAuthorityServiceImpl implements BankingAuthorityService
{

	@Override//TODO this will need to be replaced with a version which uses an x509 certificate to load a SAML token from the server
	public com.btfin.panorama.core.security.saml.SamlToken getSamlToken() {
		return new com.btfin.panorama.core.security.saml.SamlToken("<saml:Assertion xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">I am a fake server level saml token</saml:Assertion>");

	}



}
