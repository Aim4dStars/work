package com.bt.nextgen.web.validator.annotation;

import com.bt.nextgen.web.validator.ValidationErrorCode;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target(
	{
		ElementType.TYPE
	})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RegistrationPolicyValidator.class)
public @interface RegistrationPolicy
{
	String message() default ValidationErrorCode.INVALID_REGISTRATION_FORM;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
