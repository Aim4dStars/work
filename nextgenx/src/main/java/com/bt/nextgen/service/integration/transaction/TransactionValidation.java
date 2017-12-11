package com.bt.nextgen.service.integration.transaction;

import java.util.List;

import com.bt.nextgen.core.validation.ValidationError.ErrorType;

public interface TransactionValidation
{

	/**
	 * Retrieve error-type of this validation.
	 * 
	 * @return
	 */
	public String getErrorType();

	/**
	 * Retrieve the error-id of this validation. This id is typically for ABS
	 * only.
	 * 
	 * @return
	 */
	public String getErrorId();

	/**
	 * Retrieve the message associated with this validation. This is the message
	 * as-is from ABS.
	 * 
	 * @return
	 */
	public String getErrorMessage();

	/**
	 * Retrieve the log-id of the valiation.
	 * 
	 * @return
	 */
	public String getLogId();

	/**
	 * Retrieve the extneral-key for this validation. This is the key used to
	 * map ABS error to something more user friendly for the front-end.
	 * 
	 * @return
	 */
	public String getExternalKey();

	/**
	 * Retrieve the loc-list of the validation. Loc-list contains reference to
	 * any asset-level details error.
	 * 
	 * @return
	 */
	public List <String> getLocList();

	/**
	 * Retrieve the parameter-list for this validation.
	 * 
	 * @return
	 */
	public List <ParList> getParamList();

	/**
	 * Retrieve the corresponding ErrorType based on the error-type specified
	 * above.
	 * 
	 * @return
	 */
	public ErrorType getType();

	/**
	 * Retrieve the field which this validation is associated with. This field
	 * is not mapped from ABS.
	 * 
	 * @return
	 */
	public String getField();

}
