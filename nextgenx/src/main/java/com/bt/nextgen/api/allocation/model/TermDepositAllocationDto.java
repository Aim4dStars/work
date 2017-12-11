package com.bt.nextgen.api.allocation.model;

import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public class TermDepositAllocationDto extends HoldingAllocationDto implements Comparable<HoldingAllocationDto> {

    private String brand;
    private DateTime maturityDate;
    private String term;
    private String paymentFrequency;

    public TermDepositAllocationDto(String assetId, String assetCode, String assetType, String assetName, String brand,
            AllocationDetails details, List<InvestmentAllocationDto> investments, DateTime maturityDate, String term,
            String paymentFrequency) {
        super(assetId, assetCode, assetType, AssetType.TERM_DEPOSIT.name(), assetName, details, investments);
        this.brand = brand;
        this.maturityDate = maturityDate;
        this.term = term;
        this.paymentFrequency = paymentFrequency;
    }

    public String getBrand() {
        return brand;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public String getTerm() {
        return term;
    }

    public String getPaymentFrequency() {
        return paymentFrequency;
    }

}
