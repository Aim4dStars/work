package com.bt.nextgen.service.integration.corporateaction;

import javax.validation.constraints.NotNull;
// avaloaq interface

public interface CorporateActionTransactionDetails{
	
	/**
	 * The CA Position ID
	 *
	 * @return Position id
	 */	
	@NotNull
	String getPositionId();
	

	/**
	 * The CA Account ID
	 *
	 * @return account id
	 */	
	String getAccountId();	
	
	/**
	 * The CA transaction number
	 *
	 * @return transaction number
	 */
	
	Integer getTransactionNumber();
	
	/**
	 * The CA Transaction Description
	 *
	 * @return Transaction Description
	 */
	
	String getTransactionDescription();	
	
	
}