package com.bt.nextgen.web.validator.annotation;

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
@Constraint(validatedBy = UsernameValidator.class)
@Documented
public @interface Username
{
	String message() default ValidationErrorCode.INVALID_USERNAME;

	Class <? >[] groups() default {};

	Class <? extends Payload>[] payload() default {};
}