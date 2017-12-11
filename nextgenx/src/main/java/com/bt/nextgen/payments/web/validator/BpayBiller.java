package com.bt.nextgen.payments.web.validator;

import com.bt.nextgen.web.validator.ValidationErrorCode;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(
	{
		METHOD, FIELD
	})
@Retention(RUNTIME)
@Constraint(validatedBy = BpayBillerValidator.class)
@Documented
public @interface BpayBiller
{
	String message() default ValidationErrorCode.INVALID_BPAY_BILLER;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
