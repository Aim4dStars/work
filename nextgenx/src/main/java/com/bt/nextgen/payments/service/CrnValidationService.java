package com.bt.nextgen.payments.service;

public interface CrnValidationService
{
	boolean hasValidBpayCrn(CrnValidationServiceCompatible toValidate);
}
