package com.bt.nextgen.addressbook;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.payments.domain.PayeeType;

public class PayeeValidatorIntegrationTest extends BaseSecureIntegrationTest
{
	private static final Logger logger = LoggerFactory.getLogger(PayeeValidatorIntegrationTest.class);
	private PayeeModel target;
	private Errors errors;
	private Set<String> fieldSet = new HashSet<String>();
	private static final String NAME = "name";
	private static final String NICK_NAME = "nickname";

	@Autowired
	private PayeeValidator payeeValidator;

	@Before
	public void init() throws Exception
	{
		errors = new BindException(getTargetWithBlankValue(),"payeeModel");
	}

	@Test
	public void testBpayWithEmptyValue() throws Exception
	{
		logger.debug("testBpayWithEmptyValue");
		fieldSet.add("code");
		//fieldSet.add("reference");
		target = getTargetWithBlankValue();
		target.setPayeeType(PayeeType.BPAY);

		payeeValidator.validate(target,errors);

		assertThat(fieldSet.size(), equalTo(errors.getFieldErrorCount()));

		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testBpayWithInvalidValue() throws Exception
	{
		logger.debug("testBpayWithInvalidValue");
		fieldSet.add(NICK_NAME);
		fieldSet.add("code");
		//fieldSet.add("reference");
		target = getBpayWithInvalidValue();

		payeeValidator.validate(target,errors);

		assertThat(fieldSet.size(), equalTo(errors.getFieldErrorCount()));

		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testPayAnyoneWithEmptyValue() throws Exception
	{
		logger.debug("testPayAnyoneWithEmptyValue");
		fieldSet.add(NAME);
		fieldSet.add("code");
		fieldSet.add("reference");
		target = getTargetWithBlankValue();
		target.setPayeeType(PayeeType.PAY_ANYONE);

		payeeValidator.validate(target,errors);

		assertThat(fieldSet.size(), equalTo(errors.getFieldErrorCount()));
		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testPayAnyoneWithInvalidValue() throws Exception
	{
		logger.debug("testPayAnyoneWithInvalidValue");
		target = getPayeeModelWithInvalidValue();
		target.setPayeeType(PayeeType.PAY_ANYONE);
		fieldSet.add(NAME);
		fieldSet.add("code");
		fieldSet.add("reference");
		payeeValidator.validate(target,errors);
		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testSecondaryWithEmptyValue() throws Exception
	{
		logger.debug("testSecondaryWithEmptyValue");
		fieldSet.add(NAME);
		fieldSet.add("code");
		fieldSet.add("reference");
		target = getTargetWithBlankValue();
		target.setPayeeType(PayeeType.SECONDARY_LINKED);

		payeeValidator.validate(target,errors);

		assertThat(fieldSet.size(), equalTo(errors.getFieldErrorCount()));
		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testSecondaryWithInvalidValue() throws Exception
	{
		logger.debug("testSecondaryWithInvalidValue");
		target = getPayeeModelWithInvalidValue();
		target.setPayeeType(PayeeType.SECONDARY_LINKED);
		fieldSet.add(NAME);
		fieldSet.add("code");
		fieldSet.add("reference");

		payeeValidator.validate(target,errors);
		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testNomineeWithEmptyValue() throws Exception
	{
		logger.debug("testNomineeWithEmptyValue");
		fieldSet.add(NAME);
		fieldSet.add("code");
		fieldSet.add("reference");
		target = getTargetWithBlankValue();
		target.setPayeeType(PayeeType.PRIMARY_LINKED);

		payeeValidator.validate(target,errors);

		assertThat(fieldSet.size(), equalTo(errors.getFieldErrorCount()));
		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}

	@Test
	public void testNomineeWithInvalidValue() throws Exception
	{
		logger.debug("testNomineeWithInvalidValue");
		fieldSet.add(NAME);
		fieldSet.add("code");
		fieldSet.add("reference");
		target = getPayeeModelWithInvalidValue();
		target.setPayeeType(PayeeType.PRIMARY_LINKED);

		payeeValidator.validate(target,errors);

		for(FieldError fieldError: errors.getFieldErrors())
		{
			assertThat(true, equalTo(fieldSet.contains(fieldError.getField())));
		}
	}


	private PayeeModel getTargetWithBlankValue()
	{
		PayeeModel target = new PayeeModel();
		return target;
	}
	private PayeeModel getBpayWithInvalidValue()
	{
		PayeeModel target = new PayeeModel();
		target.setPayeeType(PayeeType.BPAY);
		//only numeric is allowed
		target.setCode("X12345");
		//max 20 allowed .. setting > 20
		target.setReference("123456789012345678901");
		target.setNickname("nick name must be 30 but putting more then that");
		return target;
	}

	private PayeeModel getPayeeModelWithInvalidValue()
	{
		PayeeModel target = new PayeeModel();
		//only numeric is allowed code stands for bsb for PayAnyone
		target.setCode("X12345");
		//reference stands for accountNumber for PayAnyone min 5 & max 9  .. setting <5
		target.setReference("1234");
		target.setNickname("nick name must be 30 but putting more then that");
		return target;
	}
}
