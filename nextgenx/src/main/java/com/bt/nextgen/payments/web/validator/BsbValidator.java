package com.bt.nextgen.payments.web.validator;

import com.bt.nextgen.payments.repository.BsbCodeRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BsbValidator implements ConstraintValidator<Bsb, String>
{
	@Autowired
	private BsbCodeRepository bsbCodeRepository;

	@Override
	public void initialize(Bsb constraintAnnotation)
	{
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		return !StringUtils.isBlank(value) && bsbCodeRepository.load(parseBsb(value)) != null;
	}

	public static String parseBsb(String value)
	{
		return value.replaceAll("[- ]?", "");
	}
}
