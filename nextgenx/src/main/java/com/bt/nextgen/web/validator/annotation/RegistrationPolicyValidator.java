package com.bt.nextgen.web.validator.annotation;

import com.bt.nextgen.login.web.model.RegistrationModel;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegistrationPolicyValidator implements ConstraintValidator<RegistrationPolicy, Object>
{

	@Override
	public void initialize(RegistrationPolicy registrationPolicy)
	{
	}

	@Override
	public boolean isValid(Object candidate, ConstraintValidatorContext arg1)
	{
		RegistrationModel converstation = (RegistrationModel) candidate;

		if (StringUtils.isBlank(converstation.getPassword()))
		{
			return false;
		}
		if (StringUtils.isBlank(converstation.getConfirmPassword()))
		{
			return false;
		}
		if (StringUtils.isBlank(converstation.getNewUserName()))
		{
			return false;
		}

		//password and confirm password should match
		if (!converstation.getPassword().equals(converstation.getConfirmPassword()))
		{
			return false;
		}

		//password should not contain UserName in it
		if (converstation.getPassword().indexOf(converstation.getNewUserName()) != -1)
		{
			return false;
		}

		return true;
	}
}
