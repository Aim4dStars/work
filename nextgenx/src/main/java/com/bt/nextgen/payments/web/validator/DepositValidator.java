package com.bt.nextgen.payments.web.validator;

import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("depositValidator")
public class DepositValidator implements Validator
{
	@Autowired @Qualifier("mvcValidator")
	private Validator validator;

	@Override
	public boolean supports(Class<?> clazz)
	{
		return clazz.isAssignableFrom(MoveMoneyModel.class);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		validator.validate(target, errors);
		MoveMoneyModel conversation = (MoveMoneyModel) target;
		if (conversation.getEndRepeat().equals(PaymentRepeatsEnd.REPEAT_NUMBER))
		{
			String repeatNumber = conversation.getRepeatNumber();
			if (StringUtils.isNotBlank(repeatNumber) && !StringUtils.isNumeric(repeatNumber))
			{
				errors.reject(ValidationErrorCode.INVALID_REPEAT_NUMBER);
			}
		}
	}
}
