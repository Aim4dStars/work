package com.bt.nextgen.service.integration.supernotification;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.supermatch.SuperFundAccount;

public interface SuperNotificationIntegrationService {

    /**
     * Triggers a notification in ECO to send out the SG letter to the user
     *
     * @param customerId       - customer identifier
     * @param superFundAccount - Super fund account
     * @param serviceErrors    - Object to capture service errors
     */
    boolean notifyCustomer(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors);
}
