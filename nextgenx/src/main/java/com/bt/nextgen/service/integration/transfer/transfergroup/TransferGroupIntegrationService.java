package com.bt.nextgen.service.integration.transfer.transfergroup;

import com.bt.nextgen.service.ServiceErrors;

public interface TransferGroupIntegrationService {

    /**
     * Validate the specified transferDetails before submitting. The validation process is similar to that in Order-Capture/RIP,
     * where it relies on Avaloq to vett details provided.
     * 
     * @param transfer
     * @param serviceErrors
     * @return
     */
    public TransferGroupDetails validateTransfer(TransferGroupDetails transferGroup, ServiceErrors serviceErrors);

    /**
     * Submit/Create the transferDetails specified.
     * 
     * @param transfer
     * @param serviceErrors
     * @return
     */
    public TransferGroupDetails submitTransfer(TransferGroupDetails transfer, ServiceErrors serviceErrors);

}
