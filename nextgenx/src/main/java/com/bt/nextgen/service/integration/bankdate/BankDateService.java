package com.bt.nextgen.service.integration.bankdate;

import org.joda.time.DateTime;

/**
 * Interface for fetching the Avaloq Bank Date and System Time  
 *
 */
public interface BankDateService
{

	/*
	 *  Will retreive the Avaloq Bank date
	 */
	DateTime getBankDate();
	
	/*
	 *  Will retrieve the Current System time can be used to check how old the object is 
	 */
	DateTime getCurrentTime();

}
