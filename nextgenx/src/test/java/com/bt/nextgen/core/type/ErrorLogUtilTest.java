package com.bt.nextgen.core.type;

import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.validation.ObjectError;

public class ErrorLogUtilTest
{

	Logger logger = mock(Logger.class);

	@Test(expected = NullPointerException.class)
	public void testLogErrors_forNullList()
	{
		ErrorLogUtil.logErrors(logger, null);
	}
	@Test
	public void testLogErrors()
	{

		List <ObjectError> errors = new ArrayList <ObjectError>();
		ObjectError error1 = new ObjectError("object1", "Some exception");
		errors.add(error1);
		ErrorLogUtil.logErrors(logger, errors);
		verify(logger).error(anyString());
		
		ObjectError error2 = new ObjectError("object2", "Some exception");
		ObjectError error3 = new ObjectError("object3", "Some exception");
		errors.add(error2);
		errors.add(error3);
		ErrorLogUtil.logErrors(logger, errors);
		verify(logger,times(4)).error(anyString());
	}

}
