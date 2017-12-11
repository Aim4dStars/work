package com.bt.nextgen.api.allocation.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @deprecated use account.v2.mode.allocation
 */
@Deprecated
public class HoldingAllocationDto extends BaseDto implements Comparable<HoldingAllocationDto> {

    private String assetId;
    private String assetCode;
    private String assetType;
    private String holdingType;
    private String assetName;
    private List<InvestmentAllocationDto> investments;

    // Allocation details
    private BigDecimal quantity;
    private String assetSector;
    private String industrySector;
    private String industrySubSector;
    private BigDecimal marketValue;
    private BigDecimal allocationPercent;

    public HoldingAllocationDto(String assetId, String assetCode, String assetType, String holdingType, String assetName,
            AllocationDetails allocationDetails, List<InvestmentAllocationDto> investments) {
        super();
        this.assetId = assetId;
        this.assetCode = assetCode;
        this.assetType = assetType;
        this.holdingType = holdingType;
        this.assetName = assetName;
        this.investments = investments;

        // Process allocation details.
        this.quantity = allocationDetails.getQuantity();
        this.assetSector = allocationDetails.getAssetSector();
        this.industrySector = allocationDetails.getIndustrySector();
        this.industrySubSector = allocationDetails.getIndustrySubSector();
        this.marketValue = allocationDetails.getMarketValue();
        this.allocationPercent = allocationDetails.getAllocationPercent();
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getHoldingType() {
        return holdingType;
    }

    @Override
    public int compareTo(HoldingAllocationDto o) {
        if (this.assetName.equals(o.getAssetName())) {
            return 0;
        }

        return assetName.compareToIgnoreCase(o.assetName);
    }

    public List<InvestmentAllocationDto> getInvestments() {
        return investments;
    }

    public AllocationDetails getAllocationDetails() {
        AllocationDetails details = new AllocationDetails(quantity, assetSector, industrySector, industrySubSector, marketValue,
                allocationPercent);
        return details;
    }

    public String getAssetId() {
        return assetId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public String getAssetSector() {
        return assetSector;
    }

    public String getIndustrySector() {
        return industrySector;
    }

    public String getIndustrySubSector() {
        return industrySubSector;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }
}
