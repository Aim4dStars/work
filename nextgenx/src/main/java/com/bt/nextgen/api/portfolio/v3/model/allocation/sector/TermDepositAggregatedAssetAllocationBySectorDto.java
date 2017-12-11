package com.bt.nextgen.api.portfolio.v3.model.allocation.sector;

import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import org.joda.time.DateTime;

import java.util.List;

public class TermDepositAggregatedAssetAllocationBySectorDto extends AssetAllocationBySectorDto {

    private DateTime maturityDate;
    private TermDepositPresentation tdPresentation;
   
    public TermDepositAggregatedAssetAllocationBySectorDto(Asset asset, List<AllocationBySectorDto> allocations,
            TermDepositPresentation tdPresentation) {
        super(asset, false, allocations);
        this.maturityDate = ((TermDepositAsset) asset).getMaturityDate();
        this.tdPresentation = tdPresentation;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    @Override
    public String getName() {
        return tdPresentation.getBrandName();
    }

    public String getBrand() {
        return tdPresentation.getBrandClass();
    }

    public String getTerm() {
        return tdPresentation.getTerm();
    }

    public String getPaymentFrequency() {
        return tdPresentation.getPaymentFrequency();
    }
    
    
}
