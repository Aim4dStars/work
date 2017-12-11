package com.bt.nextgen.payments.web.model;

import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.config.BaseSecureIntegrationTest;


public class MoveMoneyModelIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	@Test
	public void testRepeatNo_blankInvalid()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testRepeatNo_9()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setRepeatNumber("9");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testRepeatNo_99()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setRepeatNumber("99");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testRepeatNo_999()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setRepeatNumber("999");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.nullValue());
	}

	@Test
	public void testRepeatNo_1000()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setRepeatNumber("1000");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("repeatNumber"), IsNull.notNullValue());
	}

	@Test
	public void testAmount_blankInvalid()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("amount"), IsNull.nullValue());
	}

	@Test
	public void testInValidAmount()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setAmount(new BigDecimal("1234.789"));
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("amount"), IsNull.notNullValue());
	}

	@Test
	public void testValidAmount()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setAmount(new BigDecimal("5647.89"));
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("amount"), IsNull.nullValue());
	}

}
