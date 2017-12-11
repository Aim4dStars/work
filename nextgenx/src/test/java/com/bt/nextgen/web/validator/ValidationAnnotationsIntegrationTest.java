package com.bt.nextgen.web.validator;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.login.web.model.RegistrationModel;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.web.model.BpayPayeeModel;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.payments.web.model.NewPaymentModel;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationAnnotationsIntegrationTest extends BaseSecureIntegrationTest
{
	private static final Logger logger = LoggerFactory.getLogger(ValidationAnnotationsIntegrationTest.class);

	@Autowired
	@Qualifier("mvcValidator")
	private Validator validator;

	@Test
	public void testDate()
	{
		NewPaymentModel aNew = new NewPaymentModel();
		aNew.setAmount(new BigDecimal("234342.224"));
		aNew.setDate("28 Nov 2012");
		aNew.setFrequency(PaymentFrequency.FORTNIGHTLY);
		aNew.setEndRepeat(PaymentRepeatsEnd.REPEAT_END_DATE);
		aNew.setRepeatEndDate("12 Dec 2015");
		Errors errors = new BindException(aNew, "aNew");
		validator.validate(aNew, errors);
		assertTrue(errors.hasErrors());
		aNew.setAmount(new BigDecimal("012003.33"));
		String dateStr = ApiFormatter.asShortDate(new DateTime().plusDays(5));
		aNew.setDate(dateStr);
		aNew.setRepeatEndDate(dateStr);
		errors = new BindException(this, "error");
		validator.validate(aNew, errors);
		for (FieldError error : errors.getFieldErrors())
		{
			logger.info("Invalid field: " + error.getField() + " ---> " + aNew.getRepeatEndDate() + " ---> "
				+ error.getDefaultMessage());
		}
		assertFalse(errors.hasErrors());
	}

	@Ignore
	@Test
	public void testCrn()
	{
		//Failure of CRN is not added to the conversation errors. It is just a warning now.
		BpayPayeeModel conversation = new BpayPayeeModel();
		conversation.setBillerCode("234asdf122");
		Errors errors = new BindException(conversation, "conversation");
		validator.validate(conversation, errors);
		assertTrue(errors.hasErrors());
		conversation.setCustomerReference("17521892");
		conversation.setBillerCode("686162");
		conversation.setNickname("the name");
		errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		assertFalse(!errors.hasErrors());
	}

	@Test
	public void testPassword()
	{
		RegistrationModel conversation = new RegistrationModel();
		conversation.setPassword("today123");
		conversation.setConfirmPassword("today123");
		conversation.setUserCode("myUsername1");
		Errors errors = new BindException(conversation, "conversation");
		validator.validate(conversation, errors);
		//        assertFalse(errors.hasErrors());
	}

	@Test
	public void testDepositData()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setAmount(new BigDecimal("56784.224"));
		conversation.setDate("14 Feb 2013");
		conversation.setFrequency(PaymentFrequency.MONTHLY);
		conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_END_DATE);
		conversation.setRepeatEndDate("12 March 2013");
		Errors errors = new BindException(conversation, "conversation");
		validator.validate(conversation, errors);
		assertTrue(errors.hasErrors());
		conversation.setAmount(new BigDecimal("34654.33"));
		conversation.setDate("14 Feb 2013");
		conversation.setFrequency(PaymentFrequency.MONTHLY);
		conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_END_DATE);
		conversation.setRepeatEndDate("14 March 2013");
		errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		for (FieldError error : errors.getFieldErrors())
		{
			logger.info("Invalid field DepositData: " + error.getField() + " ---> " + conversation.getRepeatEndDate() + " ---> "
				+ error.getDefaultMessage());
		}
		assertFalse(errors.hasErrors());

	}

	@Test
	public void testYearDeposit()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setAmount(new BigDecimal("56784.224"));
		conversation.setDate("14 Feb 2013");
		conversation.setFrequency(PaymentFrequency.YEARLY);
		conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_END_DATE);
		conversation.setRepeatEndDate("12 March 2014");
		Errors errors = new BindException(conversation, "conversation");
		validator.validate(conversation, errors);
		assertTrue(errors.hasErrors());
		conversation.setAmount(new BigDecimal("34654.33"));
		conversation.setDate("14 Feb 2013");
		conversation.setFrequency(PaymentFrequency.YEARLY);
		conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_END_DATE);
		conversation.setRepeatEndDate("14 March 2014");
		errors = new BindException(this, "error");
		validator.validate(conversation, errors);
		for (FieldError error : errors.getFieldErrors())
		{
			logger.info("Invalid field DepositYear: " + error.getField() + " ---> " + conversation.getRepeatEndDate() + " ---> "
				+ error.getDefaultMessage());
		}
		assertFalse(errors.hasErrors());

	}

	@Test
	public void testRepeatNumber()
	{
		MoveMoneyModel conversation = new MoveMoneyModel();
		conversation.setAmount(new BigDecimal("56784.54"));
		conversation.setDate("20 Feb 2013");
		conversation.setFrequency(PaymentFrequency.MONTHLY);
		conversation.setEndRepeat(PaymentRepeatsEnd.REPEAT_NUMBER);
		conversation.setRepeatNumber("9999");
		Errors errors = new BindException(conversation, "conversation");
		validator.validate(conversation, errors);
		assertTrue(errors.hasErrors());
	}

}
