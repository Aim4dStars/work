package com.bt.nextgen.web.conversation;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.login.web.model.RegistrationModel;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertThat;

public class RegistrationConversationIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	@Qualifier("mvcValidator")
	private Validator validator;

	@Test
	public void testInvalidPasswordsConsecutiveChar()
	{
		RegistrationModel conversation = new RegistrationModel();
		conversation.setPassword("troday11111");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("password"), IsNull.notNullValue());
	}

	@Test
	public void testInvalidPasswordsTooShort()
	{
		RegistrationModel conversation = new RegistrationModel();
		conversation.setPassword("today12");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("password"), IsNull.notNullValue());
	}

	@Test
	public void testInvalidPasswordsAtleastOneDigit()
	{
		RegistrationModel conversation = new RegistrationModel();
		conversation.setPassword("todayHello");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("password"), IsNull.notNullValue());
	}

	
	@Test
	public void testValidPasswords()
	{
		RegistrationModel conversation = new RegistrationModel();
		conversation.setPassword("T@odaaay111");
		Errors errors = new BindException(conversation, "conv");
		validator.validate(conversation, errors);
		assertThat(errors.getFieldError("password"), IsNull.nullValue());
	}

}
