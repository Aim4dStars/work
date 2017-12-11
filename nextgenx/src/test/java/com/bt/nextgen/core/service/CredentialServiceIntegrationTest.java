package com.bt.nextgen.core.service;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.serviceops.model.UserAccountStatusModel;
import com.btfin.panorama.core.security.UserAccountStatus;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Barker (m035652)
 * Date: 13/11/13
 * Time: 4:43 PM
 */
public class CredentialServiceIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	CredentialService credentialService;

	//TODO : Fix this
	@Test
	@SecureTestContext
	public void testLoadCredentials()  throws Exception
	{
		String username = credentialService.getUserName("201602352", new ServiceErrorsImpl());
		assertThat(username, is("Taylor"));


	}

	@Test
	@SecureTestContext
	public void testLookUpStatus( )  throws Exception	{
		UserAccountStatusModel status = credentialService.lookupStatus("201602352", new ServiceErrorsImpl());
		assertThat(status.getUserAccountStatus(), is(UserAccountStatus.SUSP_TP_PW_XP));
	}

	//TODO : Fix this
	@Test
	@SecureTestContext
	public void testGetCredentialId() throws Exception
	{
		String credentialId = credentialService.getCredentialId("010000705", new ServiceErrorsImpl());
		assertThat(credentialId, is("44b2aafa-3490-11e3-888b-deadbeef90fe"));

	}
}
