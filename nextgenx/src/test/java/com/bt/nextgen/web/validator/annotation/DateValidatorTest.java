package com.bt.nextgen.web.validator.annotation;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.bt.nextgen.web.validator.ValidationErrorCode;

@RunWith(MockitoJUnitRunner.class)
public class DateValidatorTest
{

	@InjectMocks
	private DateValidator dateValidator;

	private String format = "dd MMM yyyy";
	private ConstraintValidatorContext mockCVC;

	@Before
	public void setup()
	{
		ReflectionTestUtils.setField(dateValidator, "format", format);
		mockCVC = Mockito.mock(ConstraintValidatorContext.class, Mockito.RETURNS_MOCKS);
	}

	@Test
	public void test_invalidDateFormat()
	{
		assertThat(dateValidator.isValid("234342", mockCVC), is(false));
		verify(mockCVC, times(1)).buildConstraintViolationWithTemplate(ValidationErrorCode.INVALID_DATE);
	}

	@Test
	public void test_nullDateValue()
	{

		assertThat(dateValidator.isValid(null, mockCVC), is(false));
		verify(mockCVC, times(1)).buildConstraintViolationWithTemplate(ValidationErrorCode.INVALID_DATE);
	}

	@Test
	public void test_fromDateNAValue()
	{
		ReflectionTestUtils.setField(dateValidator, "fromDate", Date.Day.NA);
		assertThat(dateValidator.isValid("29 Aug 1987", mockCVC), is(true));

	}

	@Test
	public void test_dateValuelessThanFromDate()
	{
		ReflectionTestUtils.setField(dateValidator, "fromDate", Date.Day.Today);
		assertThat(dateValidator.isValid("29 Aug 1987", mockCVC), is(false));
		verify(mockCVC, times(1)).buildConstraintViolationWithTemplate(ValidationErrorCode.DATE_EARLIER_THEN_TODAY);
	}

	@Test
	public void test_correctDateValue()
	{
		ReflectionTestUtils.setField(dateValidator, "fromDate", Date.Day.Today);
		assertThat(dateValidator.isValid("29 Aug 2069", mockCVC), is(true));
	}
}
