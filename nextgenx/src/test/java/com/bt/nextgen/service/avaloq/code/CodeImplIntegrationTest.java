package com.bt.nextgen.service.avaloq.code;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CodeImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenCodeIdIsNull_thenServiceErrors()
	{
		CodeImpl asset = new CodeImpl();
		asset.setUserId("COMPLETED");
		asset.setName("Completed");

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(asset, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("codeId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenUserIdIsNull_thenServiceErrors()
	{
		CodeImpl asset = new CodeImpl();
		asset.setCodeId("123");
		asset.setName("Completed");

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(asset, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("userId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenNameIsNull_thenServiceErrors()
	{
		CodeImpl asset = new CodeImpl();
		asset.setCodeId("123");
		asset.setUserId("COMPLETED");

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(asset, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("name may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceErrors()
	{
		CodeImpl asset = new CodeImpl();
		asset.setCodeId("123");
		asset.setUserId("COMPLETED");
		asset.setName("Completed");

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(asset, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
