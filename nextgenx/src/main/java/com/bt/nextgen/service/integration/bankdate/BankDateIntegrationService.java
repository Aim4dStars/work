package com.bt.nextgen.service.integration.bankdate;


import org.joda.time.DateTime;

import com.bt.nextgen.service.ServiceErrors;

public interface BankDateIntegrationService
{

	/**
	 * Method to get fetch the Avaloq Bank Date
	 */
	public DateTime getBankDate(ServiceErrors serviceErrors);

    /**
     * Method to get fetch the current date with Time Stamp
     */
    public DateTime getTime(ServiceErrors serviceErrors);

}
