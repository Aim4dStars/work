package com.bt.nextgen.web.validator.annotation;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.service.security.InvestorService;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@RunWith(MockitoJUnitRunner.class)
public class PasswordValidatorTest
{
	@InjectMocks
	private PasswordValidator passVal = new PasswordValidator();

	@Mock
	private InvestorService mockInvService;

	@Before
	public void setup()
	{
		Mockito.when(mockInvService.isValidPassword(Matchers.anyString())).thenReturn(true);
	}

	@Test
	public void test_blankIsBad() throws Exception
	{
		assertThat(passVal.isValid("", Mockito.mock(ConstraintValidatorContext.class)), is(false));
		assertThat(passVal.isValid(null, Mockito.mock(ConstraintValidatorContext.class)), is(false));
	}

	@Test
	public void test_atLeastOneDigit()
	{
		assertThat(passVal.isValid("ddd", Mockito.mock(ConstraintValidatorContext.class)), is(false));
	}

	@Test
	public void test_atLeastOneCharacter()
	{
		assertThat(passVal.isValid("111", Mockito.mock(ConstraintValidatorContext.class)), is(false));
	}

	@Test
	public void test_sizeBetween8and32()
	{
		Mockito.when(mockInvService.isValidPassword(Matchers.anyString())).thenReturn(true);

		assertThat(passVal.isValid("1234567", Mockito.mock(ConstraintValidatorContext.class)), is(false));
		assertThat(passVal.isValid("a234567890", Mockito.mock(ConstraintValidatorContext.class)), is(true));
		assertThat(passVal.isValid("123456789012345678901234567890123", Mockito.mock(ConstraintValidatorContext.class)),
			is(false));

	}

	@Test
	public void test_noMoreThan3ConsecutiveChars()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);

		assertThat(passVal.isValid("aa34567890", mockCVC), is(true));
		assertThat(passVal.isValid("113456789a", mockCVC), is(true));
	}

	@Test
	public void test_startWith4ConsecutiveNumbersBad()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
		assertThat(passVal.isValid("1111456789a", mockCVC), is(false));
		Mockito.verify(mockCVC, Mockito.times(1))
			.buildConstraintViolationWithTemplate(Mockito.eq(ValidationErrorCode.CONSECUTIVE_NUMBERS_IN_PASSWORD));
	}

	@Test
	public void test_3Consecutivenumbersfine()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
		assertThat(passVal.isValid("122256789a", mockCVC), is(true));
	}

	@Test
	public void test_startsWith4ConsecutiveLettersBad()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
		assertThat(passVal.isValid("aaaa4567890", mockCVC), is(false));
		Mockito.verify(mockCVC, Mockito.times(1))
			.buildConstraintViolationWithTemplate(Mockito.eq(ValidationErrorCode.CONSECUTIVE_CHARACTERS_IN_PASSWORD));
	}

	@Test
	public void test_3ConsecutiveLettersFine()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
		assertThat(passVal.isValid("456aaa7890", mockCVC), is(true));
	}

	@Test
	public void test_4ConsecutiveNumbersInMiddleBad()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
		assertThat(passVal.isValid("456666ab90", mockCVC), is(false));
		Mockito.verify(mockCVC, Mockito.times(1))
			.buildConstraintViolationWithTemplate(Mockito.eq(ValidationErrorCode.CONSECUTIVE_NUMBERS_IN_PASSWORD));
	}

	@Test
	public void test_endsWith4ConsecutiveLettersBad()
	{
		ConstraintValidatorContext mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
		assertThat(passVal.isValid("4567890aaaa", mockCVC), is(false));
		Mockito.verify(mockCVC, Mockito.times(1))
			.buildConstraintViolationWithTemplate(Mockito.eq(ValidationErrorCode.CONSECUTIVE_CHARACTERS_IN_PASSWORD));

	}
}
