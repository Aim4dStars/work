package com.bt.nextgen.service.integration.transfer;

import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;

import java.util.List;

public interface TransferDetails {

    public String getTransferId();

    // public ContainerGroup getContainer();
    public String getDestContainerId();

    public String getDestAssetId();

    public TransferType getTransferType();

    public List<InspecieAsset> getTransferAssets();

    public List<TaxParcel> getTaxParcels();

    public SponsorDetails getSponsorDetails();

    public BeneficialOwnerChangeStatus getChangeOfBeneficialOwnership();

    public AccountKey getAccountKey();

    public List<TransactionValidation> getWarnings();

    public OrderStatus getStatus();

}
