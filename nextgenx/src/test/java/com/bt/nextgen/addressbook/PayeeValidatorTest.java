package com.bt.nextgen.addressbook;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@RunWith(MockitoJUnitRunner.class)
public class PayeeValidatorTest
{

	@InjectMocks
	private PayeeValidator payeeValidator;
	private BsbCodeRepository bsbCodeRepository;
	private BpayBillerCodeRepository bpayBillerCodeRepository;

	@Test
	public void test_invalidBillerCodeForEPAY() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);

		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("a");
		when(payeeModel.getNickname()).thenReturn(" ");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.BPAY);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("code", ValidationErrorCode.INVALID_BPAY_BILLER);
	}

	@Test
	public void test_invalidNickNameForEPAY() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);

		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("code");
		when(payeeModel.getNickname()).thenReturn("iurtheutheutheutheutheiutheuitheuitheuitheiutheu");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.BPAY);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("nickname", ValidationErrorCode.INVALID_NICKNAME);
	}

	@Test
	public void test_invalidBsbForPayAnyone() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("code", ValidationErrorCode.INVALID_BSB);
	}

	@Test
	public void test_validBsbForPayAnyone() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("code", ValidationErrorCode.INVALID_BSB);
	}

	@Test
	public void test_validAccountNumberForPayAnyone() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("123456");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(0)).rejectValue("reference", ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
	}

	@Test
	public void test_invalidAccountNumberForPayAnyone() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("reference", ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
	}

	@Test
	public void test_invalidPayeeNameForPayAnyone() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
	}

	@Test
	public void test_validPayeeNameForPayAnyone() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("name");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PAY_ANYONE);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(0)).rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
	}

	@Test
	public void test_invalidBsbForSecondaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("qwsertyuj");
		when(payeeModel.getName()).thenReturn("name");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.SECONDARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("code", ValidationErrorCode.INVALID_BSB);
	}

	@Test
	public void test_validAccountNumberForSecondaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getReference()).thenReturn("123456");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.SECONDARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(0)).rejectValue("reference", ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
	}

	@Test
	public void test_invalidAccountNumberForSecondaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getReference()).thenReturn("1234");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.SECONDARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("reference", ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
	}

	@Test
	public void test_validPayeeNameForSecondaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("name");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.SECONDARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(0)).rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
	}

	@Test
	public void test_invalidPayeeNameForSecondaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.SECONDARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
	}

	@Test
	public void test_invalidBsbForPrimaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("qwsertyuj");
		when(payeeModel.getName()).thenReturn("name");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PRIMARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("code", ValidationErrorCode.INVALID_BSB);
	}

	@Test
	public void test_validAccountNumberForPrimaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getReference()).thenReturn("123456");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PRIMARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(0)).rejectValue("reference", ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
	}

	@Test
	public void test_invalidAccountNumberForPrimaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getReference()).thenReturn("1234");
		when(payeeModel.getNickname()).thenReturn("abc");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PRIMARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("reference", ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
	}

	@Test
	public void test_validPayeeNameForPrimaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("name");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PRIMARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(0)).rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
	}

	@Test
	public void test_invalidPayeeNameForPrimaryLinked() throws Exception
	{
		PayeeModel payeeModel = mock(PayeeModel.class);
		bsbCodeRepository = mock(BsbCodeRepository.class);
		Errors errors = mock(Errors.class);
		when(payeeModel.getCode()).thenReturn("");
		when(payeeModel.getReference()).thenReturn("1234");

		when(payeeModel.getName()).thenReturn("");
		when(payeeModel.getPayeeType()).thenReturn(PayeeType.PRIMARY_LINKED);
		payeeValidator.validate(payeeModel, errors);
		verify(errors, times(1)).rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
	}

}
