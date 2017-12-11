package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertThat;

public class PayAnyoneModelIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	@Test
	public void testValidBsb_isValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setBsb("012003");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("bsb"), IsNull.nullValue());
	}

	@Test
	public void testValidBsbWithHyphen_isValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setBsb("012-003");
		Errors errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("bsb"), IsNull.nullValue());
	}

	@Test
	public void testValidBsbWithSpace_isValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setBsb("012 003");
		Errors errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("bsb"), IsNull.nullValue());
	}

	@Test
	public void testInValidBsb_isInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setBsb("999999");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("bsb"), IsNull.notNullValue());
	}

	@Test
	public void testInValidBsb_charsInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setBsb("testing");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("bsb"), IsNull.notNullValue());
	}

	@Test
	public void testPayeeName_isValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setPayeeName("james");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("payeeName"), IsNull.nullValue());
	}

	@Test
	public void testPayeeName_funnyCharsInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setPayeeName("james__");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("payeeName"), IsNull.notNullValue());
	}

	@Test
	public void testPayeeName_tooBigInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setPayeeName("qwerasdfzxcvasdfqwerasdfzxcvasdfqwer");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("payeeName"), IsNull.notNullValue());
	}

	@Test
	public void testPayeeName_withSpaceValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setPayeeName("tom harry");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("payeeName"), IsNull.nullValue());
	}

	@Test
	public void testPayeeName_withTwoSpacesValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setPayeeName("tom harry jones");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("payeeName"), IsNull.nullValue());
	}

	@Test
	public void testAccountNumber_lettersInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setAccountNumber("tom harry jones");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("accountNumber"), IsNull.notNullValue());
	}

	@Test
	public void testAccountNumber_toBigInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setAccountNumber("1234567890");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("accountNumber"), IsNull.notNullValue());
	}

	@Test
	public void testAccountNumber_emptyInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setAccountNumber("");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("accountNumber"), IsNull.notNullValue());
	}

	@Test
	public void testAccountNumber_blankInValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setAccountNumber(" ");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("accountNumber"), IsNull.notNullValue());
	}

	@Test
	public void testNickname_charsWithSpaceValid()
	{
		PayAnyoneModel conversation = new PayAnyoneModel();
		conversation.setNickname("tom harry jones");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("nickname"), IsNull.nullValue());
	}
}
