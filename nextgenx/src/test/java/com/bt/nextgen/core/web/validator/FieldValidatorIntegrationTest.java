package com.bt.nextgen.core.web.validator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.payments.web.model.BpayPayeeModel;

public class FieldValidatorIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private FieldValidator fieldValidator;

	 //Shouldn't be dependent on content of database and other unit test.
	@Test
	public void testBillerCode()
	{
		assertTrue(fieldValidator.validateField(BpayPayeeModel.class, "billerCode", "0000001008").isEmpty());
	}

	@Test
	public void testCustomerReference()
	{
		assertTrue(fieldValidator.validateField(BpayPayeeModel.class, "customerReference", "55423").isEmpty());
	}

	 //Shouldn't be dependent on content of database and other unit test.
	@Test
	public void testInvalidBillerCode()
	{
		assertTrue(
			fieldValidator.validateField(BpayPayeeModel.class, "billerCode", "55asdfasdfsd42sd3").size() > 0);
	}

	@Test
	public void testInvalidCustomerReference()
	{
		assertTrue(fieldValidator.validateField(BpayPayeeModel.class, "customerReference",
			"012345678901234567890").size() > 0);
	}
}
