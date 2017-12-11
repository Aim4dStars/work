package com.bt.nextgen.service.integration.fundpaymentnotice;

import java.util.List;

import com.bt.nextgen.service.ServiceErrors;

/**
 * Interface for the Fund Payment Notice  
 *
 */
public interface FundPaymentNoticeIntegrationService
{

	/**
	 * Method to fetch the Fund Payment Notice Details
	 */
	List <FundPaymentNotice> getFundPaymentNoticeDetails(FundPaymentNoticeRequest request, ServiceErrors serviceErrors);

}
