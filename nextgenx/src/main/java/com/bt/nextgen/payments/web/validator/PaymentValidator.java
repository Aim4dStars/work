package com.bt.nextgen.payments.web.validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.payments.repository.PayeeRepository;
import com.bt.nextgen.payments.service.CrnValidationService;
import com.bt.nextgen.payments.web.model.MoveMoneyModel;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@Component
public class PaymentValidator implements Validator
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentValidator.class);

	@Autowired
	@Qualifier("mvcValidator")
	private Validator validator;

	@Autowired
	private PayeeRepository payeeRepository;

	@Autowired
	private CrnValidationService crnValidationService;

	@Override
	public boolean supports(Class <? > clazz)
	{
		return clazz.isAssignableFrom(MoveMoneyModel.class);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		validator.validate(target, errors);
		MoveMoneyModel moveMoneyModel = (MoveMoneyModel)target;

		validateFrequency(moveMoneyModel, errors);
		//CRN validation is no longer a mandatory check while making payment. Even if the CRN is incorrect user can make payments.
		//validateCrn(moveMoneyModel, errors);
	}

	private void validateFrequency(MoveMoneyModel moveMoneyModel, Errors errors)
	{
		if (PayeeType.BPAY.name().equals(moveMoneyModel.getPayeeType()))
		{
			if (PaymentFrequency.YEARLY.equals(moveMoneyModel.getFrequency()))
			{
				errors.reject(ValidationErrorCode.INVALID_PAYMENT_REPEAT_FREQUENCY);
			}
			if (PaymentRepeatsEnd.REPEAT_NUMBER.equals(moveMoneyModel.getEndRepeat()))
			{
				String repeatNumber = moveMoneyModel.getRepeatNumber();
				if (StringUtils.isNotBlank(repeatNumber) && StringUtils.isNumeric(repeatNumber)
					&& Integer.valueOf(repeatNumber) > moveMoneyModel.getFrequency().getMaxRepeat())
				{
					errors.reject(ValidationErrorCode.INVALID_PAYMENT_REPEAT_NUMBER);
				}
			}
		}
	}

	// CRN validation is no longer a mandatory check while making payment. Thus commenting out the method.
	// Method setErrorCode and MoveMoneyModelAdapter also commented as it is only getting called from validateCrn
	/*private void validateCrn(MoveMoneyModel moveMoneyModel, Errors errors)
	{
		if(moveMoneyModel.getPayeeType().equalsIgnoreCase(PayeeType.BPAY.name())){
		MoveMoneyModelAdapter adaptedMoveMoneyModel = new MoveMoneyModelAdapter(moveMoneyModel);
		if (moveMoneyModel.getPayeeType().equals(PayeeType.BPAY.name()))
		{
			if(!crnValidationService.hasValidBpayCrn(adaptedMoveMoneyModel)){
				setErrorCode(adaptedMoveMoneyModel.getCrnType(), errors);
			}
		}
		}
	}

	private void setErrorCode(CRNType crnType, Errors errors)
	{
		switch (crnType)
		{
			case ICRN:
				errors.reject(ValidationErrorCode.INVALID_INTELLIGENT_REFERENCE_NUMBER);
				break;
			case VCRN:
				errors.reject(ValidationErrorCode.INVALID_VARIABLE_REFERENCE_NUMBER);
				break;
			case CRN:
				errors.reject(ValidationErrorCode.INVALID_CUSTOMER_REFERENCE_NUMBER);
				break;
		}
	}

	/**
	 * This allows us to pass the model directly  into the service, without coupling the two.
	 */

	/*private class MoveMoneyModelAdapter implements CrnValidationServiceCompatible
	{
		private final MoveMoneyModel source;
		final BpayPayee bpayPayee;

		private MoveMoneyModelAdapter(MoveMoneyModel source)
		{
			this.source = source;
			bpayPayee = (BpayPayee) payeeRepository.load(Long.valueOf(source.getPayeeId()));
		}

		@Override public String getBillerCode()
		{
			return bpayPayee.getCode();
		}

		@Override public String getCustomerReference()
		{
			return source.getCustomerRefNo();
		}

		@Override public BigDecimal getAmount()
		{
			return source.getAmount();
		}

		public CRNType getCrnType(){
			return bpayPayee.getCrnType();
		}
	}*/

}
