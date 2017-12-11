package com.bt.nextgen.service.avaloq.pension;


import com.bt.nextgen.api.pension.model.PensionTrxnDto;
import com.bt.nextgen.service.ServiceErrors;

/**
 * Interface for pension commencement related services.
 */

public interface PensionCommencementIntegrationService {
    /**
     * Commence a pension.
     *
     * @param accountNumber Account number for pension to commence.
     *
     * @return DTO for result of commencing the pension.
     */
    PensionTrxnDto commencePension(String accountNumber);


    /**
     * Check if pension commencement is currently in progress.
     *
     * @param accountNumber Account number for pension.
     * @param serviceErrors Object to put errors in.
     *
     * @return {@code true} if pension commencement is in progress, {@code false} otherwise.
     */
    boolean isPensionCommencementPending(String accountNumber, ServiceErrors serviceErrors);
}
