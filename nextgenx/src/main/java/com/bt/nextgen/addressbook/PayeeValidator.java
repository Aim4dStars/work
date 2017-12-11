package com.bt.nextgen.addressbook;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.bt.nextgen.payments.repository.BpayBillerCodeRepository;
import com.bt.nextgen.payments.repository.BsbCodeRepository;
import com.bt.nextgen.payments.service.CrnValidationService;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@Component
public class PayeeValidator implements Validator
{
	private static final int NICKNAME_MAX_LENGTH = 30;
	private static final int BILLER_CODE_MAX_LENGTH = 10;
	private static final int CRN_MAX_LENGTH = 20;
	private static final int BSB_MAX_LENGTH = 7;
	private static final int ACCOUNT_NUMBER_MAX_LENGTH = 9;
	private static final int ACCOUNT_NUMBER_MIN_LENGTH = 5;
	private static final int NAME_MAX_LENGTH = 32;
	private static final String FIELD_CODE = "code";
	private static final String FIELD_REFERENCE = "reference";
	@Autowired
	private CrnValidationService crnValidationService;
	@Autowired
	private BpayBillerCodeRepository bpayBillerCodeRepository;
	@Autowired
	private BsbCodeRepository bsbCodeRepository;

	@Override
	public boolean supports(Class <? > clazz)
	{
		return clazz.isAssignableFrom(PayeeModel.class);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
		PayeeModel payee = (PayeeModel)target;
		String code = payee.getCode();
		String reference = payee.getReference();
		String nickname = payee.getNickname();
		String name = payee.getName();
		switch (payee.getPayeeType())
		{
			case BPAY:
				validateBillerCode(code, errors);
				//CRN validation is no longer a mandatory check while making payment. Even if the CRN is incorrect user can make payments.
				//validateCrn(reference, code, errors);
				validateNickname(nickname, errors);
				break;
			case PAY_ANYONE:
				validateBsb(code, errors);
				validateAccountNumber(reference, errors);
				validatePayeeName(name, errors);
				break;
			case SECONDARY_LINKED:
				validateBsb(code, errors);
				validateAccountNumber(reference, errors);
				validatePayeeName(name, errors);
				break;
			case PRIMARY_LINKED:
				validateBsb(code, errors);
				validateAccountNumber(reference, errors);
				validatePayeeName(name, errors);
				break;
		}
	}

	//TODO This method is not being used in the class

	/*private void validateCrn(final String crn, final String billerCode, Errors errors)
	{
		if (StringUtils.isBlank(crn) || crn.length() > CRN_MAX_LENGTH || !crnValidationService.hasValidBpayCrn(
			new CrnValidationServiceCompatible()
			{
				@Override public String getBillerCode()
				{
					return billerCode;
				}

				@Override public String getCustomerReference()
				{
					return crn;
				}

				@Override public BigDecimal getAmount()
				{
					throw new RuntimeException("This object doesn't support this value");
				}

			}))
		{
			errors.rejectValue(FIELD_REFERENCE, null, ValidationErrorCode.INVALID_CUSTOMER_REFERENCE_NUMBER);
		}
	}*/

	public String validateBillerCode(String billerCode, Errors errors)
	{
		if (StringUtils.isBlank(billerCode) || !StringUtils.isNumeric(billerCode) || billerCode.length() > BILLER_CODE_MAX_LENGTH
			|| bpayBillerCodeRepository.load(billerCode) == null)
		{
			if (errors != null)
			{
				errors.rejectValue(FIELD_CODE, ValidationErrorCode.INVALID_BPAY_BILLER);
			}
			return ValidationErrorCode.INVALID_BPAY_BILLER;
		}
		return null;
	}

	public void validateNickname(String nickname, Errors errors)
	{
		if (StringUtils.isNotBlank(nickname) && nickname.length() > NICKNAME_MAX_LENGTH)
		{
			errors.rejectValue("nickname", ValidationErrorCode.INVALID_NICKNAME);
		}
	}

	public String validateBsb(String bsb, Errors errors)
	{
		if (StringUtils.isBlank(bsb) || bsb.length() > BSB_MAX_LENGTH
			|| bsbCodeRepository.load(bsb.replaceAll("[- ]?", "")) == null)
		{
			if (errors != null)
			{
				errors.rejectValue(FIELD_CODE, ValidationErrorCode.INVALID_BSB);
			}
			return ValidationErrorCode.INVALID_BSB;
		}
		return null;
	}

	private void validateAccountNumber(String accountNumber, Errors errors)
	{
		if (StringUtils.isBlank(accountNumber) || accountNumber.length() < ACCOUNT_NUMBER_MIN_LENGTH
			|| accountNumber.length() > ACCOUNT_NUMBER_MAX_LENGTH)
		{
			errors.rejectValue(FIELD_REFERENCE, ValidationErrorCode.INVALID_ACCOUNT_NUMBER);
		}
	}

	private void validatePayeeName(String name, Errors errors)
	{
		if (StringUtils.isBlank(name) || !StringUtils.isAlphanumericSpace(name) || name.length() > NAME_MAX_LENGTH)
		{
			errors.rejectValue("name", ValidationErrorCode.INVALID_NICKNAME);
		}

	}
}
