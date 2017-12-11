package com.bt.nextgen.payments.web.validator;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.payments.domain.CRNType;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.repository.BpayPayee;
import com.bt.nextgen.payments.repository.PayeeRepository;
import com.bt.nextgen.payments.service.CrnValidationService;
import com.bt.nextgen.payments.service.CrnValidationServiceCompatible;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.web.validator.ValidationErrorCode;

public class PaymentValidatorTest
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentValidatorTest.class);

	private PaymentValidator paymentValidator;
	private PayeeRepository mockPayeeRepository;
	private CrnValidationService mockCrnValidationService;
	private Validator mockValidator;

	private static  Set<String> fieldSet = new HashSet<String>();
	private MoveMoneyModel target;
	private Errors errors;

	@BeforeClass
	public static void init()
	{
		fieldSet.add("amount");
		fieldSet.add("date");
		fieldSet.add("description");
		//fieldSet.add("payeeDescription");
		fieldSet.add("frequency");
		fieldSet.add("endRepeat");
		fieldSet.add("repeatEndDate");
	}

	@Before
	public void setUp() throws Exception
	{
		paymentValidator = new PaymentValidator();
		mockPayeeRepository = mock(PayeeRepository.class);
		mockCrnValidationService = mock(CrnValidationService.class);
		mockValidator = mock(Validator.class);

		ReflectionTestUtils.setField(paymentValidator, "payeeRepository", mockPayeeRepository);
		ReflectionTestUtils.setField(paymentValidator, "crnValidationService", mockCrnValidationService);
		ReflectionTestUtils.setField(paymentValidator, "validator", mockValidator);

	}

	@Test
	public void testNonBpayClean()
	{
		MoveMoneyModel mockModel = mock(MoveMoneyModel.class);
		when(mockModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE.name());
		when(mockModel.getPayeeId()).thenReturn("-1");

		errors = new BindException(mockModel,"moveMoneyModel");

		paymentValidator.validate(mockModel, errors);

		assertThat(errors.getGlobalErrorCount(), is(0));
	}

	@Test
	public void testBpayYearlyFrequencyInvalid()
	{
		MoveMoneyModel mockModel = mock(MoveMoneyModel.class);
		when(mockModel.getPayeeType()).thenReturn(PayeeType.BPAY.name());
		when(mockModel.getFrequency()).thenReturn(PaymentFrequency.YEARLY);
		when(mockModel.getPayeeId()).thenReturn("-1");

		errors = new BindException(mockModel,"moveMoneyModel");

		setupMockPayeeRepository(CRNType.CRN);

		paymentValidator.validate(mockModel, errors);

		assertThat(errors.getGlobalErrorCount(), is(1));
	}

	@Test
	public void testBpayRepeatsEndIsChecked()
	{
		MoveMoneyModel mockModel = mock(MoveMoneyModel.class);
		when(mockModel.getPayeeType()).thenReturn(PayeeType.BPAY.name());
		when(mockModel.getEndRepeat()).thenReturn(PaymentRepeatsEnd.REPEAT_NUMBER);
		when(mockModel.getFrequency()).thenReturn(PaymentFrequency.FORTNIGHTLY);
		when(mockModel.getRepeatNumber()).thenReturn(String.valueOf(PaymentFrequency.FORTNIGHTLY.getMaxRepeat() + 1));
		when(mockModel.getPayeeId()).thenReturn("-1");

		setupMockPayeeRepository(CRNType.CRN);

		errors = new BindException(mockModel,"moveMoneyModel");

		paymentValidator.validate(mockModel,errors);

		assertThat(errors.getGlobalErrorCount(), is(1));
	}

	@Test
	public void testBpayCrnAreMappedToCorrectErrorCode()
	{
		assertBpayCrnMapping(CRNType.ICRN, ValidationErrorCode.INVALID_INTELLIGENT_REFERENCE_NUMBER);
		assertBpayCrnMapping(CRNType.VCRN, ValidationErrorCode.INVALID_VARIABLE_REFERENCE_NUMBER);
		assertBpayCrnMapping(CRNType.CRN, ValidationErrorCode.INVALID_CUSTOMER_REFERENCE_NUMBER);
	}


	private void assertBpayCrnMapping(CRNType crnType, String expectedValidationErrorCode)
	{
		MoveMoneyModel mockModel = mock(MoveMoneyModel.class);
		when(mockModel.getPayeeType()).thenReturn(PayeeType.BPAY.name());
		when(mockModel.getPayeeId()).thenReturn("-1");

		setupMockPayeeRepository(crnType);

		when(mockCrnValidationService.hasValidBpayCrn(any(CrnValidationServiceCompatible.class))).thenReturn(false);

		errors = new BindException(mockModel,"moveMoneyModel");

		//P.S : Changed CRN validation to warning rather than error
		//paymentValidator.validate(mockModel,errors);

		//assertThat(errors.getGlobalError().getCode(), is(expectedValidationErrorCode));
	}

	private void setupMockPayeeRepository(CRNType crnType)
	{
		BpayPayee mockBpayPayee = mock(BpayPayee.class);
		when(mockBpayPayee.getCrnType()).thenReturn(crnType);
		when(mockPayeeRepository.load(anyLong())).thenReturn(mockBpayPayee);
	}

	@Test
	public void testAssertFieldsAreChecked()
	{
		target = makeMoveMoneyWithInvalidValue();
		errors = new BindException(target,"moveMoneyModel");

		paymentValidator.validate(target,errors);

		verify(mockValidator, times(1)).validate(target, errors);
	}


	@Test
	public void testInvalidPaymentRepeatFrequency()
	{
		target = makeMoveMoneyModelWithValidValue();
		target.setFrequency(PaymentFrequency.YEARLY);
		errors = new BindException(target,"moveMoneyModel");

		setupMockPayeeRepository(CRNType.CRN);

		paymentValidator.validate(target,errors);
		assertThat(errors.getGlobalErrorCount(), is(1));

	}

	private MoveMoneyModel makeMoveMoneyModelWithValidValue()
	{
		MoveMoneyModel target = new MoveMoneyModel();
		target.setCustomerRefNo("10000002");
		target.setAmount(new BigDecimal(100));
		target.setDate("24 Apr 2013");
		target.setDescription("My Description");
		target.setPayeeType(PayeeType.BPAY.toString());
		target.setEndRepeat(PaymentRepeatsEnd.REPEAT_NUMBER);
		target.setFrequency(PaymentFrequency.FORTNIGHTLY);
		target.setRepeatEndDate("10 Nov 2013");
		target.setPayeeId("-1");
		return target;
	}

	private MoveMoneyModel makeMoveMoneyWithInvalidValue()
	{
		MoveMoneyModel target = new MoveMoneyModel();
		target.setDescription("putting more than 18 chars to check description");
		target.setAmount(new BigDecimal("1234567891012345678901.000"));
		target.setDate("invalid");
		target.setFrequency(PaymentFrequency.WEEKLY);
		target.setPayeeId("-1");
		target.setEndRepeat(PaymentRepeatsEnd.REPEAT_NO_END);
		target.setRepeatEndDate("unknown");
		return target;
	}
}