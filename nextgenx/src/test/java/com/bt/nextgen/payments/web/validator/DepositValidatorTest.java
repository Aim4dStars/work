package com.bt.nextgen.payments.web.validator;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@RunWith(MockitoJUnitRunner.class)
public class DepositValidatorTest
{
	@InjectMocks
	private DepositValidator depositValidator;

	@Mock
	private Validator validator;

	

	@Test
	public void test_Validate() throws Exception
	{
		MoveMoneyModel mockModel = mock(MoveMoneyModel.class);
		Errors errors = mock(Errors.class);
		when(mockModel.getPayeeType()).thenReturn(PayeeType.BPAY.name());
		when(mockModel.getFrequency()).thenReturn(PaymentFrequency.YEARLY);
		when(mockModel.getPayeeId()).thenReturn("-1");
		when(mockModel.getRepeatNumber()).thenReturn("2");
		when(mockModel.getEndRepeat()).thenReturn(PaymentRepeatsEnd.REPEAT_NUMBER);

		depositValidator.validate(mockModel, errors);
		verify(errors, times(0)).reject(eq(ValidationErrorCode.INVALID_REPEAT_NUMBER));
	}
	
	@Test
	public void test_Validate_Error() throws Exception
	{
		Errors errors = mock(Errors.class);
		MoveMoneyModel mockModel = mock(MoveMoneyModel.class);
		when(mockModel.getPayeeType()).thenReturn(PayeeType.BPAY.name());
		when(mockModel.getFrequency()).thenReturn(PaymentFrequency.YEARLY);
		when(mockModel.getPayeeId()).thenReturn("-1");
		when(mockModel.getRepeatNumber()).thenReturn("three");
		when(mockModel.getEndRepeat()).thenReturn(PaymentRepeatsEnd.REPEAT_NUMBER);

		depositValidator.validate(mockModel, errors);
		verify(errors, times(1)).reject(eq(ValidationErrorCode.INVALID_REPEAT_NUMBER));
		
	}
	
	
}
