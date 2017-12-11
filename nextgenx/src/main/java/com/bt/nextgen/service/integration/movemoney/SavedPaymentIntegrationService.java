package com.bt.nextgen.service.integration.movemoney;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transaction.SavedPayment;

import java.util.List;

/**
 * Interface for retrieving saved payments.
 */
public interface SavedPaymentIntegrationService {


    /**
     * Get Saved payments for an account
     *
     * @param accountNumber          Account Number
     * @param orderTypes             Order types for which payments are to be retrieved
     * @param serviceErrors
     *
     * @return List<PensionPayment> List of saved pension payments
     */
    List<SavedPayment> loadSavedPensionPayments(String accountNumber, List<String> orderTypes, ServiceErrors serviceErrors);
}
