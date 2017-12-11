package com.bt.nextgen.payments.web.model;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import com.bt.nextgen.config.BaseSecureIntegrationTest;

public class BpayPayeeModelIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	//TODO : Remove dependency of the database
	@Test
	public void testBillerCode_isValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setBillerCode("0000001008");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("billerCode"), IsNull.nullValue());
	}

	@Test
	public void testBillerCode_blankInvalid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setBillerCode(" ");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("billerCode"), IsNull.notNullValue());
	}

	@Test
	public void testBillerCode_emptyInvalid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setBillerCode("");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("billerCode"), IsNull.notNullValue());
	}

	@Test
	public void testBillerCode_nullInvalid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("billerCode"), IsNull.notNullValue());
	}

	@Test
	public void testCustomerReference_20NumsValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setCustomerReference("01234567890123456789");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("customerReference"), IsNull.nullValue());
	}

	@Test
	public void testCustomerReference_21NumsInValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setCustomerReference("012345678901234567891");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("customerReference"), IsNull.notNullValue());
	}

	@Test
	public void testCustomerReference_emptyInValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setCustomerReference("");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("customerReference"), IsNull.notNullValue());
	}

	@Test
	public void testCustomerReference_blankInValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setCustomerReference(" ");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("customerReference"), IsNull.notNullValue());
	}

	@Test
	public void testCustomerReference_nullInValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setCustomerReference(null);
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("customerReference"), IsNull.nullValue());
	}

	@Test
	public void testNickname_10Valid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setNickname("0123456789");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("nickname"), IsNull.nullValue());
	}

	@Test
	public void testNickname_20Valid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setNickname("0 123456789012345678");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("nickname"), IsNull.nullValue());
	}

	@Test
	public void testNickname_20SpacesValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setNickname("0 123456789012345678");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("nickname"), IsNull.nullValue());
	}

	@Test
	public void testNickname_21SpacesInValid()
	{
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setNickname("0 1234567890123456783");
		BindException errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("nickname"), IsNull.nullValue());
	}
}
