package com.bt.nextgen.reports.account.transfer.inspecie;

import com.bt.nextgen.service.integration.transfer.TransferType;

import java.util.List;

public class InspecieTransferReportData {

    private String transferId;
    private TransferType transferType;
    private List<InspecieAssetReportData> inspecieAssets;

    public InspecieTransferReportData(String transferId, TransferType transferType, List<InspecieAssetReportData> inspecieAssets) {
        this.transferId = transferId;
        this.transferType = transferType;
        this.inspecieAssets = inspecieAssets;
    }

    public String getTransferId() {
        return transferId;
    }

    public boolean getIsBrokerSponsoredShareTransfer() {
        return TransferType.LS_BROKER_SPONSORED.equals(transferType);
    }

    public boolean getIsIssuerSponsoredShareTransfer() {
        return TransferType.LS_ISSUER_SPONSORED.equals(transferType);
    }

    public boolean getIsOtherShareTransfer() {
        return TransferType.LS_OTHER.equals(transferType);
    }

    public boolean getIsManagedFundTransfer() {
        return TransferType.MANAGED_FUND.equals(transferType);
    }

    public List<InspecieAssetReportData> getInspecieAssets() {
        return inspecieAssets;
    }
}
