package com.bt.nextgen.payments.web.validator;

import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BpayBillerValidator implements ConstraintValidator<BpayBiller, String>
{
	@Autowired
	private BpayBillerCodeRepository bpayBillerCodeRepository;

	@Override
	public void initialize(BpayBiller constraintAnnotation)
	{
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		return !StringUtils.isBlank(value) && bpayBillerCodeRepository.load(value) != null;
	}
}
