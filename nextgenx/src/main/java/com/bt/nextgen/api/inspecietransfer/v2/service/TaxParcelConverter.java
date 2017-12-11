package com.bt.nextgen.api.inspecietransfer.v2.service;

import com.bt.nextgen.api.inspecietransfer.v2.model.TaxParcelDto;
import com.bt.nextgen.service.avaloq.transfer.TaxParcelImpl;
import com.bt.nextgen.service.integration.transfer.TaxParcel;

/**
 * @deprecated Use V3
 */
@Deprecated
public final class TaxParcelConverter {

    private TaxParcelConverter() {

    }

    public static TaxParcel fromDto(TaxParcelDto taxParcelDto) {
        TaxParcelImpl parcel = new TaxParcelImpl(taxParcelDto.getAssetId(), taxParcelDto.getTaxRelvDate(),
                taxParcelDto.getTaxVisibDate(), taxParcelDto.getQuantity(), taxParcelDto.getCostBase(),
                taxParcelDto.getReducedCostBase(), taxParcelDto.getIndexedCostBase());
        parcel.setOriginalCostBase(taxParcelDto.getOriginalCostBase());
        return parcel;
    }

    public static TaxParcelDto toDto(TaxParcel taxParcel) {
        return new TaxParcelDto(taxParcel);
    }

}
