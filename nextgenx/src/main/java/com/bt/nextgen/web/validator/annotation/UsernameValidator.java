package com.bt.nextgen.web.validator.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

public class UsernameValidator implements ConstraintValidator <Username, String>
{

	@Override
	public void initialize(Username constraintAnnotation)
	{}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{

		if (StringUtils.isBlank(value))
		{
			return false;
		}

		if (value.length() > 250)
		{
			return false;
		}

		return true;
	}
}
