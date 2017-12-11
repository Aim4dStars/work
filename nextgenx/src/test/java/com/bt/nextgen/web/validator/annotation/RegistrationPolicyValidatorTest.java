package com.bt.nextgen.web.validator.annotation;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.login.web.model.RegistrationModel;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationPolicyValidatorTest
{

	@InjectMocks
	private RegistrationPolicyValidator registrationPolicyValidator;

	private RegistrationModel registrationModel;
	private ConstraintValidatorContext mockCVC;

	@Before
	public void setup()
	{
		registrationModel = new RegistrationModel();
		registrationModel.setPassword("password");
		registrationModel.setConfirmPassword("password");
		registrationModel.setNewUserName("userCode");
		mockCVC = Mockito.mock(ConstraintValidatorContext.class);
	}

	@Test
	public void test_blankNullPassword()
	{
		registrationModel.setPassword(null);
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));

		registrationModel.setPassword("");
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));
	}

	@Test
	public void test_blankNullConfirmPassword()
	{
		registrationModel.setConfirmPassword(null);
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));

		registrationModel.setConfirmPassword("");
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));
	}

	@Test
	public void test_blankNullUserCode()
	{
		registrationModel.setNewUserName(null);
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));

		registrationModel.setNewUserName("");
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));
	}

	@Test
	public void test_PswAndConfirmPswDifferent()
	{
		registrationModel.setConfirmPassword("confirmPassword");
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));
	}

	@Test
	public void test_PswConatinsUsername()
	{
		registrationModel.setPassword("passuserCodesdsg");
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(false));
	}

	@Test
	public void test_modelWithValidValues()
	{
		assertThat(registrationPolicyValidator.isValid(registrationModel, mockCVC), is(true));
	}

}
