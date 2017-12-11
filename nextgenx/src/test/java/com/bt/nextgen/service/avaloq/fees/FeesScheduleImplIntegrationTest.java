package com.bt.nextgen.service.avaloq.fees;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class FeesScheduleImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenFeesComponentsIsNull_thenServiceErrors()
	{
		FeesScheduleImpl impl = new FeesScheduleImpl();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		impl.setType(FeesType.ONGOING_FEE);
		validator.validate(impl, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());

		Assert.assertEquals("feesComponents may not be null", serviceErrors.getErrorList().iterator().next().getReason());

	}

	@Test
	public void testValidation_whenTypeIsNull_thenServiceErrors()
	{
		FeesScheduleImpl impl = new FeesScheduleImpl();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <FeesComponents> list = new ArrayList <FeesComponents>();
		list.add(new DollarFeesComponent());
		impl.setFeesComponents(list);
		validator.validate(impl, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("feesType may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		FeesScheduleImpl impl = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(impl, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}
}
