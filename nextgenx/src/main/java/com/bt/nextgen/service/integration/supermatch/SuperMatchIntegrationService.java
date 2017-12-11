package com.bt.nextgen.service.integration.supermatch;

import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Interface for Super match integration service
 */
public interface SuperMatchIntegrationService {

    /**
     * Gets the super details for a customer in ECO
     *
     * @param customerId       - customer identifier
     * @param superFundAccount - Super fund account
     * @param serviceErrors    - Object to capture service errors
     */
    List<SuperMatchDetails> retrieveSuperDetails(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors);

    /**
     * Updates consent status in ECO
     *
     * @param customerId        - customer identifier
     * @param superFundAccount  - Super fund account
     * @param isConsentProvided - Flag to consent/unconsent for Super check
     * @param serviceErrors     - Object to capture service errors
     */
    List<SuperMatchDetails> updateConsentStatus(String customerId, SuperFundAccount superFundAccount, Boolean isConsentProvided, ServiceErrors serviceErrors);

    /**
     * Updates acknowledgement status in ECO
     *
     * @param customerId       - customer identifier
     * @param superFundAccount - Super fund account
     * @param serviceErrors    - Object to capture service errors
     */
    List<SuperMatchDetails> updateAcknowledgementStatus(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors);

    /**
     * Triggers the super roll-over for a customer
     *
     * @param customerId       - customer identifier
     * @param superFundAccount - Super fund account
     * @param rollOverFunds    - Super funds to roll over
     * @param serviceErrors    - Object to capture service errors
     */
    List<SuperMatchDetails> updateRollOverStatus(String customerId, SuperFundAccount superFundAccount, List<SuperFundAccount> rollOverFunds, ServiceErrors serviceErrors);

    /**
     * Updates member details in ECO
     *
     * @param customerId       - customer identifier
     * @param superFundAccount - Super fund account
     * @param serviceErrors    - Object to capture service errors
     */
    boolean createMember(String customerId, SuperFundAccount superFundAccount, ServiceErrors serviceErrors);
}
