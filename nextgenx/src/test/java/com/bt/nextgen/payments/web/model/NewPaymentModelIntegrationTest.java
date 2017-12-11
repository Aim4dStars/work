package com.bt.nextgen.payments.web.model;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

public class NewPaymentModelIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	@Test
	public void testAmount_blankInvalid()
	{
		NewPaymentModel aNew = new NewPaymentModel();
		Errors errors = new BindException(aNew, "conv");
		validator.validate(aNew, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testAmount_9()
	{
		NewPaymentModel aNew = new NewPaymentModel();
		aNew.setRepeatNumber("99");
		Errors errors = new BindException(aNew, "conv");
		validator.validate(aNew, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testAmount_99()
	{
		NewPaymentModel aNew = new NewPaymentModel();
		aNew.setRepeatNumber("99");
		Errors errors = new BindException(aNew, "conv");
		validator.validate(aNew, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testAmount_999()
	{
		NewPaymentModel aNew = new NewPaymentModel();
		aNew.setRepeatNumber("999");
		Errors errors = new BindException(aNew, "conv");
		validator.validate(aNew, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testAmount_9999()
	{
		NewPaymentModel aNew = new NewPaymentModel();
		aNew.setRepeatNumber("9999");
		Errors errors = new BindException(aNew, "conv");
		validator.validate(aNew, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.notNullValue());
	}
}
