package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ManagedFundAssetDto extends AssetDto {
    private BigDecimal price;
    private String distributionMethod;
    private String assetClass;
    private String priceFrequency;
    private String fundManager;
    private BigDecimal indirectCostRatioPercent;
    private Map<String, BigDecimal> allocations;
    private String assetSubClass;

    public ManagedFundAssetDto() {
    }

    public ManagedFundAssetDto(ManagedFundAsset asset) {
        this(asset, null, null);
    }

    public ManagedFundAssetDto(ManagedFundAsset asset, Map<String, BigDecimal> allocations) {
        this(asset, null, allocations);
    }

    public ManagedFundAssetDto(ManagedFundAsset asset, List<DistributionMethod> distributionMethods) {
        this(asset, distributionMethods, null);
    }

    public ManagedFundAssetDto(ManagedFundAsset asset, List<DistributionMethod> distributionMethods, Map<String, BigDecimal> allocations) {
        super(asset, AssetType.MANAGED_FUND.getDisplayName(), distributionMethods);
        this.price = asset.getPrice();
        this.distributionMethod = asset.getDistributionMethod();
        if (asset.getAssetClass() != null) {
            this.assetClass = asset.getAssetClass().getDescription();
        }

        this.priceFrequency = asset.getPriceFrequency();
        this.fundManager = asset.getFundManager();
        this.indirectCostRatioPercent = asset.getIndirectCostRatioPercent();
        this.allocations = allocations;
        this.assetSubClass = asset.getAssetSubclass();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    public String getAssetClass() {
        return assetClass;
    }

    public String getPriceFrequency() {
        return priceFrequency;
    }

    public String getFundManager() {
        return fundManager;
    }

    public BigDecimal getIndirectCostRatioPercent() {
        return indirectCostRatioPercent;
    }

    public Map<String, BigDecimal> getAllocations() {
        return allocations;
    }

    public String getAssetSubClass() {
        return assetSubClass;
    }
}
