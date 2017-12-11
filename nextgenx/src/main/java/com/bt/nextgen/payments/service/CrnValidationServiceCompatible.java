package com.bt.nextgen.payments.service;

import java.math.BigDecimal;

/**
 * This class allows us to validate lots of different data sources (usually models) without the coupling
 */
public interface CrnValidationServiceCompatible
{

	/**
	 * The biller code of the associate bpay biller
	 * @return
	 */
	String getBillerCode();

	/**
	 * The customer reference for customer from the bpay biller
	 * @return
	 */
	String getCustomerReference();

	/**
	 * The amount of the transaction
	 * @return
	 */
	BigDecimal getAmount();

}
