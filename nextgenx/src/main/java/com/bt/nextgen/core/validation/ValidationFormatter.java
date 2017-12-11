package com.bt.nextgen.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.bt.nextgen.core.service.MessageService;
import com.bt.nextgen.core.service.MessageServiceImpl;


public class ValidationFormatter
{
	private static MessageService msgService = new MessageServiceImpl();
	private static final Logger logger = LoggerFactory.getLogger(ValidationFormatter.class);

	public static Object format(BindingResult bindingResult)
	{
		List <ValidationError> formattedResults = new ArrayList <ValidationError>();
		for (ObjectError message : bindingResult.getAllErrors())
		{
			String error = msgService.lookup(message.getCode());
			logger.info("errorCode: " + message.getCode() + ", errorMessage: " + error);
			if (message.getArguments() != null)
			{
				ValidationError ve = new ValidationError(((DefaultMessageSourceResolvable)message.getArguments()[0]).getDefaultMessage(),
					error);

				formattedResults.add(ve);
			}
			else
			{
				// We got an unexpected message type (probably from an InitBinder type validation)
				// So simply return it as a String
				return error;
			}
		}
		return formattedResults;
	}

	public static List <ValidationError> format(String fieldName, String errorCode)
	{
		return format(fieldName, errorCode, ValidationError.ErrorType.ERROR);
	}

	/**
	 * This function will format error results to show Form level errors
	 */
	public static List <ValidationError> format(String fieldName, String errorCode, ValidationError.ErrorType type)
	{
		List <ValidationError> formattedResults = new ArrayList <ValidationError>();
		String error = msgService.lookup(errorCode);
		formattedResults.add(new ValidationError(fieldName, error, type));
		return formattedResults;
	}
}
