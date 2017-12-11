package com.bt.nextgen.web.validator.annotation;

import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.security.InvestorService;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator <Password, String>
{
	@Autowired
	private InvestorService investorOnBoardingService;
	private static final String EPS_ENVIRONMENT = "eps.environment";

	@Override
	public void initialize(Password constraintAnnotation)
	{}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if(Properties.getSafeBoolean(EPS_ENVIRONMENT))
		{
			return true;
		}
		if (StringUtils.isBlank(value))
		{
			return false;
		}

		//(?=.*\\d)  at least one digit
		//(?=.*[a-zA-Z]) at least one char
		//{8,32}  minimum 8 chars and maximum 32 chars in total
		if (!value.matches("((?=.*[^a-zA-Z])(?=.*[a-zA-Z]).{8,32})"))
		{
			return false;
		}

		//No more than 3 consecutive chars
		if (!value.matches("(?!.*(.)\\1{3})^[\\S]*$"))
		{
			if (value.matches(".*(\\d)\\1{3}.*"))
			{
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(ValidationErrorCode.CONSECUTIVE_NUMBERS_IN_PASSWORD)
					.addConstraintViolation();
			}
			else if (value.matches(".*(\\D)\\1{3}.*"))
			{
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(ValidationErrorCode.CONSECUTIVE_CHARACTERS_IN_PASSWORD)
					.addConstraintViolation();
			}
			return false;
		}

		//TODO Password Policy to validate from service
		if (!investorOnBoardingService.isValidPassword(value))
		{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ValidationErrorCode.INVALID_PASSWORD_MATCHES_USERNAME)
				.addConstraintViolation();
			return false;
		}

		return true;
	}
}
