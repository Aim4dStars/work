package com.bt.nextgen.service.integration.transfer;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;

import java.util.List;

public interface InspecieTransferIntegrationService {

    /**
     * Validate the specified transferDetails before submitting. The validation
     * process is similar to that in Order-Capture/RIP, where it relies on
     * Avaloq to vett details provided.
     * 
     * @param transfer
     * @param serviceErrors
     * @return
     */
    public TransferDetails validateTransfer(TransferDetails transfer, ServiceErrors serviceErrors);

    /**
     * Submit/Create the transferDetails specified.
     * 
     * @param transfer
     * @param serviceErrors
     * @return
     */
    public TransferDetails submitTransfer(final TransferDetails transfer, final ServiceErrors serviceErrors);

    /**
     * Retrieve a transferDetails based on the unique transferId. The
     * TransferDetails retrieved will NOT include the transfer statuses of each
     * underlying assets.
     * 
     * @param transferId
     * @param accountKey
     * @param serviceErrors
     * @return
     */
    public TransferDetails loadTransferDetails(final String transferId, final AccountKey accountKey,
            final ServiceErrors serviceErrors);

    public List<TransferOrder> loadAccountTransferOrders(final AccountKey accountKey, final ServiceErrors serviceErrors);
}
