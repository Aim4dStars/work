package com.bt.nextgen.service.integration;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.portfolio.web.model.ClientStatementsInterface;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ImageServerStatementsServiceIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	StatementsIntegrationService imageServerStatementsService;

    @Test
    @Ignore // Ignored low value test that fails often in dev environments
	public void testGetStatements()
	{
		ClientStatementsInterface clientStatements = imageServerStatementsService.getStatements("12345", new ServiceErrorsImpl()); 
		assertThat(clientStatements, Is.is(notNullValue()));
	}

}
