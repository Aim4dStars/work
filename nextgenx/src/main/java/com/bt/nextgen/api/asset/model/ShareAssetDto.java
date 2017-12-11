package com.bt.nextgen.api.asset.model;

import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ShareAsset;

import java.math.BigDecimal;
import java.util.List;

public class ShareAssetDto extends AssetDto {
    private BigDecimal price;
    private Boolean taxAssetDomicile;
    private String industryType;
    private String industrySector;
    private String hybridType;
    private BigDecimal investmentHoldingLimit;
    private BigDecimal investmentHoldingLimitBuffer;

    // Following IHL and IHL buffer are only for Direct Super
    private BigDecimal superInvestIhl;
    private BigDecimal superInvestIhlBuffer;

    public ShareAssetDto() {
    }

    public ShareAssetDto(ShareAsset asset) {
        this(asset, null);
    }

    public ShareAssetDto(ShareAsset asset, List<DistributionMethod> distributionMethods) {
        super(asset, AssetType.SHARE.getDisplayName(), distributionMethods);
        this.price = asset.getPrice();
        this.taxAssetDomicile = asset.getTaxAssetDomicile();
        this.industryType = asset.getIndustryType();
        this.industrySector = asset.getIndustrySector();
        this.hybridType = asset.getHybridType();
        this.investmentHoldingLimit = asset.getInvestmentHoldingLimit();
        this.investmentHoldingLimitBuffer = asset.getInvestmentHoldingLimitBuffer();
        this.superInvestIhl = asset.getSuperInvestIhl();
        this.superInvestIhlBuffer = asset.getSuperInvestIhlBuffer();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Boolean getTaxAssetDomicile() {
        return taxAssetDomicile;
    }

    public String getIndustryType() {
        return industryType;
    }

    public String getIndustrySector() {
        return industrySector;
    }

    public String getHybridType() {
        return hybridType;
    }

    public BigDecimal getInvestmentHoldingLimit() {
        return investmentHoldingLimit;
    }

    public BigDecimal getInvestmentHoldingLimitBuffer() {
        return investmentHoldingLimitBuffer;
    }

    public BigDecimal getSuperInvestIhl() {
        return superInvestIhl;
    }

    public BigDecimal getSuperInvestIhlBuffer() {
        return superInvestIhlBuffer;
    }
}
