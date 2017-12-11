package com.bt.nextgen.service.integration.fundpaymentnotice;

import java.util.Collection;

import org.joda.time.DateTime;

/**
 * 
 * This interface defines the request for getting the Fund Payment Notice from Avaloq
 *
 */

public interface FundPaymentNoticeRequest
{

	/** 
	 * @return date - The start date for the Search parameter
	 */
	public DateTime getStartDate();

	/** 
	 * @return date - The end date for the Search parameter
	 */
	public DateTime getEndDate();

	/** 
	 * @return String - The Search String, could be a APIR Code/ Managed Fund Name/ Fund Manager
	 */
	public Collection <String> getAssetIds();

}