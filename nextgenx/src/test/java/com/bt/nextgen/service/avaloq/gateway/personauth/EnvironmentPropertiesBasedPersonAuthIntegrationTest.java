package com.bt.nextgen.service.avaloq.gateway.personauth;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.core.security.avaloq.gateway.personauth.EnvironmentPropertiesBasedPersonAuth;
import com.btfin.panorama.core.security.avaloq.gateway.personauth.PersonAuthProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class EnvironmentPropertiesBasedPersonAuthIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	private PersonAuthProvider provider;

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testGetPersonAuth()
	{
		assertEquals(Properties.get(EnvironmentPropertiesBasedPersonAuth.DUMMY_ADVISER), provider.getPersonAuth());
	}
}
