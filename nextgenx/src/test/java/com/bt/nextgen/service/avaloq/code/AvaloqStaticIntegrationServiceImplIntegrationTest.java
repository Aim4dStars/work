package com.bt.nextgen.service.avaloq.code;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.TestConfig;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;

@Ignore
public class AvaloqStaticIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	StaticIntegrationService avaloqStaticIntegrationService;

	@Before
	public void setup()
	{}

	@Test
	public void testLoadCodes_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		Collection <Code> codes = avaloqStaticIntegrationService.loadCodes(CodeCategory.ORDER_TYPE, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(codes);
		for (Code code : codes)
		{
			Assert.assertNotNull(code);
		}
	}
}
