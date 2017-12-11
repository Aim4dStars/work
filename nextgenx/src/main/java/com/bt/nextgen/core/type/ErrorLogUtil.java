package com.bt.nextgen.core.type;

import org.slf4j.Logger;
import org.springframework.validation.ObjectError;

import java.util.List;
/**
 * 
 *  A utility class.
 */
public class ErrorLogUtil
{

	/**
	 * A utility method for logging errors from a list of ObjectError.
	 * @param logger Logger object for logging.
	 * @param errors list of ObjectError objects.
	 */
	public static void logErrors(Logger logger, List<ObjectError> errors)
	{
		for (ObjectError error : errors)
		{
			logger.error("Validation error: " + error.getCode());
		}

	}
}
