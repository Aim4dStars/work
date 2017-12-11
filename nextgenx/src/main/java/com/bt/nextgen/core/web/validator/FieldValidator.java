package com.bt.nextgen.core.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class FieldValidator
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	@SuppressWarnings("unchecked")
	public List<String> validateField(Class clazz, String fieldName, Object value)
	{
		LocalValidatorFactoryBean javaxValidator = (LocalValidatorFactoryBean) validator;
		Set<ConstraintViolation<String>> errors = javaxValidator.validateValue(clazz, fieldName, value,
			javax.validation.groups.Default.class);

		List<String> errorCodes = new ArrayList<String>();
		for (ConstraintViolation<String> violation : errors)
		{
			errorCodes.add(violation.getMessage());
		}
		return errorCodes;
	}
}
