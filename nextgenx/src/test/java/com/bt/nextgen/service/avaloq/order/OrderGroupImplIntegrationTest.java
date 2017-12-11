package com.bt.nextgen.service.avaloq.order;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderGroupImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenOrderIdIsNull_thenServiceErrors()
	{
		OrderGroupImpl orderGroup = new OrderGroupImpl();
		orderGroup.setReference("Bob's Transaction");
		orderGroup.setLastUpdateDate(new DateTime());
		orderGroup.setOrderType("0");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(orderGroup, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("orderGroupId may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenLastUpdateDateIsNull_thenServiceErrors()
	{
		OrderGroupImpl orderGroup = new OrderGroupImpl();
		orderGroup.setOrderGroupId("1234");
		orderGroup.setReference("Bob's Transaction");
		orderGroup.setOrderType("0");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(orderGroup, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("lastUpdateDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenOrderTypeIsNull_thenServiceErrors()
	{
		OrderGroupImpl orderGroup = new OrderGroupImpl();
		orderGroup.setOrderGroupId("1234");
		orderGroup.setReference("Bob's Transaction");
		orderGroup.setLastUpdateDate(new DateTime());
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(orderGroup, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("orderType may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenObjectIsNull_thenServiceErrors()
	{
		OrderGroupImpl orderGroup = null;
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(orderGroup, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("Failed to validate - object is null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		OrderGroupImpl orderGroup = new OrderGroupImpl();
		orderGroup.setOrderGroupId("1234");
		orderGroup.setReference("Bob's Transaction");
		orderGroup.setLastUpdateDate(new DateTime());
		orderGroup.setOrderType("0");
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(orderGroup, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
