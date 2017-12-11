package com.bt.nextgen.web.validator.annotation;

import com.bt.nextgen.web.validator.ValidationErrorCode;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<Date, String>
{
	Logger logger = LoggerFactory.getLogger(DateValidator.class);

	private Date.Day fromDate;
	private String format;

	@Override
	public void initialize(Date constraintAnnotation)
	{
		this.format = constraintAnnotation.format();
		this.fromDate = constraintAnnotation.from();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		java.util.Date dateValue = null;
		try
		{
			dateValue = dateFormat.parse(value);
		}
		catch (Exception e)
		{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(ValidationErrorCode.INVALID_DATE).addConstraintViolation();
			return false;
		}
		if (fromDate != Date.Day.NA && (dateValue == null || dateValue.before(
			new DateTime().toDateMidnight().toDate())))
		{
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
				ValidationErrorCode.DATE_EARLIER_THEN_TODAY).addConstraintViolation();
			return false;
		}
		return true;
	}
}
