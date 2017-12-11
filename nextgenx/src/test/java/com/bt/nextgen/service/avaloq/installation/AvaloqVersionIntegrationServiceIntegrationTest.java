package com.bt.nextgen.service.avaloq.installation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;

public class AvaloqVersionIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	AvaloqVersionIntegrationService service;

	@SecureTestContext(username = "service", customerId = "M035652", authorities = "ROLE_SERVICE")
	@Test
	public void testAvaloqInstallation()
	{
		ServiceErrors testErrors = new FailFastErrorsImpl();
		AvaloqInstallationInformation install = service.getAvaloqInstallInformation(testErrors);

		assertThat(install,is(notNullValue()));

		assertThat(install.getInstallationUid(),is(notNullValue()));

		assertThat(install.getInstallationUid(),is("6deed463ad02a5418366b256ffad41ee"));

	}



}