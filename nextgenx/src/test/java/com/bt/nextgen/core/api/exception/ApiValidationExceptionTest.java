package com.bt.nextgen.core.api.exception;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;

public class ApiValidationExceptionTest
{
	private String message;
	private Throwable cause;
	List <DomainApiErrorDto> errors;

	@Before
	public void setup() throws Exception
	{
		message = "message";
		cause = new Throwable("cause");

		errors = new ArrayList <>();
		errors.add(new DomainApiErrorDto("domain", "reason", "message"));
	}

	@Test
	public void testConstructor_whenVersionAndErrors_thenBaseClassHasVersionAndErrors()
	{
		ApiValidationException ex = new ApiValidationException(ApiVersion.CURRENT_VERSION, errors);
		assertEquals(ApiVersion.CURRENT_VERSION, ex.getApiVersion());
		assertEquals(errors.get(0), ex.getErrors().get(0));
	}

	@Test
	public void testConstructor_whenVersionAndErrorsAndMessage_thenBaseClassHasVersionAndErrorsAndMessage()
	{
		ApiValidationException ex = new ApiValidationException(ApiVersion.CURRENT_VERSION, errors, message);
		assertEquals(ApiVersion.CURRENT_VERSION, ex.getApiVersion());
		assertEquals(message, ex.getMessage());
		assertEquals(errors.get(0), ex.getErrors().get(0));
	}

	@Test
	public void testConstructor_whenVersionAndErrorsAndCause_thenBaseClassHasVersionAndErrorsAndCause()
	{
		ApiValidationException ex = new ApiValidationException(ApiVersion.CURRENT_VERSION, errors, cause);
		assertEquals(ApiVersion.CURRENT_VERSION, ex.getApiVersion());
		assertEquals(cause, ex.getCause());
		assertEquals(errors.get(0), ex.getErrors().get(0));
	}

	@Test
	public void testConstructor_whenVersionAndErrorsAndMessageAndCause_thenBaseClassHasVersionAndErrorsAndMessageAndCause()
	{
		ApiValidationException ex = new ApiValidationException(ApiVersion.CURRENT_VERSION, errors, message, cause);
		assertEquals(ApiVersion.CURRENT_VERSION, ex.getApiVersion());
		assertEquals(message, ex.getMessage());
		assertEquals(cause, ex.getCause());
		assertEquals(errors.get(0), ex.getErrors().get(0));
	}
}
