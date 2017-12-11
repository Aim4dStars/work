package com.bt.nextgen.api.portfolio.v3.model.allocation.exposure;

import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import org.joda.time.DateTime;

import java.util.List;

public class TermDepositAssetAllocationByExposureDto extends AssetAllocationByExposureDto {

    private DateTime maturityDate;
    private TermDepositPresentation tdPresentation;

    /**
     * @param holding
     * @param contents
     * @param maturityDate
     * @param tdPresentation
     */
    public TermDepositAssetAllocationByExposureDto(Asset asset, List<AllocationByExposureDto> contents,
            TermDepositPresentation tdPresentation) {
        super(asset, contents);
        this.maturityDate = ((TermDepositAsset) asset).getMaturityDate();
        this.tdPresentation = tdPresentation;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
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
