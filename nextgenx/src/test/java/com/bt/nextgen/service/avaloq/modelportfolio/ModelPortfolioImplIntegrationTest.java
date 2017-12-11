package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.ips.IpsKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ModelPortfolioImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenModelIdIsNull_thenServiceErrors()
	{
		ModelPortfolioImpl model = new ModelPortfolioImpl();
		model.setLastUpdateDate(new DateTime());
		model.setStatus("Pending");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("modelKey may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenStatusIsNull_thenServiceErrors()
	{
		ModelPortfolioImpl model = new ModelPortfolioImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setLastUpdateDate(new DateTime());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("status may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		ModelPortfolioImpl model = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		ModelPortfolioImpl model = new ModelPortfolioImpl();
        model.setModelKey(IpsKey.valueOf("1111"));
		model.setLastUpdateDate(new DateTime());
		model.setStatus("Pending");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(model, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
