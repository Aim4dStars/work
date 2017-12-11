package com.bt.nextgen.web.validator.annotation;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UsernameValidatorTest
{

	@InjectMocks
	private UsernameValidator usernameVal;

	@Test
	public void test_blankUsername() throws Exception
	{
		assertThat(usernameVal.isValid("", Mockito.mock(ConstraintValidatorContext.class)), is(false));
		assertThat(usernameVal.isValid(null, Mockito.mock(ConstraintValidatorContext.class)), is(false));
	}

	@Test
	public void test_moreThan250CharNotAllowed() throws Exception
	{
		StringBuilder username = new StringBuilder();
		for (int i = 0; i < 250; i++)
		{
			username.append("a");
		}

		assertThat(username.length() > 250, is(false));
		assertThat(usernameVal.isValid(username.toString(), Mockito.mock(ConstraintValidatorContext.class)), is(true));

		username.append("a");
		assertThat(username.length() > 250, is(true));
		assertThat(usernameVal.isValid(username.toString(), Mockito.mock(ConstraintValidatorContext.class)), is(false));
	}

	@Test
	public void test_validUsername()
	{
		assertThat(usernameVal.isValid("A", Mockito.mock(ConstraintValidatorContext.class)), is(true));
		assertThat(usernameVal.isValid("a$f", Mockito.mock(ConstraintValidatorContext.class)), is(true));
		assertThat(usernameVal.isValid("1111", Mockito.mock(ConstraintValidatorContext.class)), is(true));
	}
}
