package com.bt.nextgen.api.account.v2.model.performance;

import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import org.joda.time.DateTime;

@Deprecated
public class TermDepositPerformanceDto extends PerformanceDto implements Comparable<PerformanceDto> {

    private TermDepositPresentation tdPresentation;
    private DateTime maturityDate;

    public TermDepositPerformanceDto(AssetPerformance assetPerformance, TermDepositPresentation tdPresentation,
            DateTime maturityDate) {

        super(assetPerformance, tdPresentation.getBrandName(), assetPerformance.getAssetType().getDisplayName());

        this.tdPresentation = tdPresentation;
        this.maturityDate = maturityDate;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
