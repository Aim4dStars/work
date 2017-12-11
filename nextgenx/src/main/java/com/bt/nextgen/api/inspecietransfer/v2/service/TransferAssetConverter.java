package com.bt.nextgen.api.inspecietransfer.v2.service;

import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDtoImpl;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;

/**
 * @deprecated Use V3
 */
@Deprecated
public final class TransferAssetConverter {

    private TransferAssetConverter() {

    }

    public static SettlementRecordDtoImpl toDto(InspecieAsset transferAsset, String assetCode) {

        return new SettlementRecordDtoImpl(transferAsset.getAssetId(), assetCode, transferAsset.getQuantity());
    }

    public static InspecieAsset fromDto(SettlementRecordDtoImpl transferAsset) {
        return new InspecieAsset(transferAsset.getAssetId(), transferAsset.getQuantity());
    }

}
