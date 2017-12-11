package com.bt.nextgen.api.portfolio.v3.model;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.smsf.constants.PropertyType;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.avaloq.asset.ExternalValuationAssetImpl;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.EstimatedGainHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ReinvestHolding;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

public class InvestmentAssetDto extends BaseDto {
    private static final BigDecimal ONE_HUNDRED_PERCENT = new BigDecimal("100.00");
    private String assetId;
    private String assetCode;
    private AssetType assetType;
    private String assetName;
    private DateTime effectiveDate;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal marketValue;
    private BigDecimal dollarGain;
    private BigDecimal percentGain;
    private BigDecimal allocationPercent;
    private BigDecimal unitPrice;
    private BigDecimal availableQuantity;
    private BigDecimal availableQuantityToTransfer;
    private Boolean incomeOnly;
    private String status;
    private Boolean hasPending;
    private Boolean prepaymentAsset;
    private String isin;
    private String distributionMethod = null;
    private BigDecimal investmentHoldingLimit;
    private BigDecimal investmentHoldingLimitBuffer;
    private String propertyType;

    // Following IHL and IHL buffer are only for Direct Super
    private BigDecimal superInvestIhl;
    private BigDecimal superInvestIhlBuffer;

    public InvestmentAssetDto(AccountHolding accountHolding, BigDecimal mpBalance) {
        super();
        this.assetId = accountHolding.getAsset().getAssetId();
        this.assetType = accountHolding.getAsset().getAssetType();
        this.assetName = accountHolding.getAsset().getAssetName();
        this.assetCode = accountHolding.getAsset().getAssetCode();
        this.effectiveDate = accountHolding.getUnitPriceDate();
        this.quantity = accountHolding.getUnits();
        this.averageCost = accountHolding.getCost();
        this.marketValue = accountHolding.getMarketValue();
        this.allocationPercent = PortfolioUtils.getValuationAsPercent(this.marketValue, mpBalance);
        this.unitPrice = accountHolding.getUnitPrice();
        this.availableQuantity = accountHolding.getAvailableUnits();
        this.availableQuantityToTransfer = accountHolding.getAvailableUnitsToTransfer();
        this.status = accountHolding.getAsset().getStatus() != null ? accountHolding.getAsset().getStatus().getDisplayName() : "";
        this.hasPending = accountHolding.getHasPending();
        this.incomeOnly = accountHolding.getIncomeOnly();
        this.prepaymentAsset = Boolean.FALSE;
        if (accountHolding.getReferenceAsset() != null) {
            this.prepaymentAsset = Boolean.TRUE;
        }
        if (accountHolding instanceof ReinvestHolding) {
            this.distributionMethod = ((ReinvestHolding) accountHolding).getDistributionMethod() == null ? null
                    : ((ReinvestHolding) accountHolding).getDistributionMethod().getDisplayName();
        }

        if (accountHolding instanceof EstimatedGainHolding) {
            this.dollarGain = ((EstimatedGainHolding) accountHolding).getEstdGainDollar();
            this.percentGain = ((EstimatedGainHolding) accountHolding).getEstdGainPercent();
        }

        this.isin = accountHolding.getAsset().getIsin();

        if (accountHolding.getAsset() instanceof ShareAsset) {
            final ShareAsset shareAsset = (ShareAsset) accountHolding.getAsset();
            this.investmentHoldingLimit = shareAsset.getInvestmentHoldingLimit();
            this.investmentHoldingLimitBuffer = shareAsset.getInvestmentHoldingLimitBuffer();
            this.superInvestIhl = shareAsset.getSuperInvestIhl();
            this.superInvestIhlBuffer = shareAsset.getSuperInvestIhlBuffer();
        }
        if (accountHolding.getAsset() instanceof ExternalValuationAssetImpl) {
            this.propertyType = getHoldingPropertyType((ExternalValuationAssetImpl) accountHolding.getAsset());
        }
    }

    public InvestmentAssetDto(List<InvestmentAssetDto> investmentAssets, BigDecimal accountBalance) {
        this.quantity = Lambda.sumFrom(investmentAssets).getQuantity();
        this.averageCost = Lambda.sumFrom(investmentAssets).getAverageCost();
        this.marketValue = Lambda.sumFrom(investmentAssets).getMarketValue();
        this.availableQuantity = Lambda.sumFrom(investmentAssets).getAvailableQuantity();
        this.availableQuantityToTransfer = Lambda.sumFrom(investmentAssets).getAvailableQuantityToTransfer();
        this.allocationPercent = PortfolioUtils.getValuationAsPercent(this.marketValue, accountBalance);
        this.dollarGain = Lambda.sumFrom(investmentAssets).getDollarGain();
        if ((this.marketValue.subtract(this.dollarGain)).compareTo(BigDecimal.ZERO) != 0) {
            this.percentGain = ONE_HUNDRED_PERCENT.multiply(this.dollarGain.divide(this.marketValue.subtract(this.dollarGain), MathContext.DECIMAL64));
        }
        else {
            this.percentGain = BigDecimal.ZERO;
        }
        InvestmentAssetDto investmentAssetDto = investmentAssets.get(0);
        this.assetId = investmentAssetDto.getAssetId();
        this.assetCode = investmentAssetDto.getAssetCode();
        this.assetType = AssetType.forName(investmentAssetDto.getAssetType());
        this.assetName = investmentAssetDto.getAssetName();
        this.effectiveDate = investmentAssetDto.getEffectiveDate();
        this.unitPrice = investmentAssetDto.getUnitPrice();
        this.status = investmentAssetDto.getStatus();
        this.prepaymentAsset = Boolean.FALSE;
        this.isin = investmentAssetDto.getIsin();
        this.distributionMethod = investmentAssetDto.getDistributionMethod();
        this.investmentHoldingLimit = investmentAssetDto.getInvestmentHoldingLimit();
        this.investmentHoldingLimitBuffer = investmentAssetDto.getInvestmentHoldingLimitBuffer();
        this.superInvestIhl = investmentAssetDto.getSuperInvestIhl();
        this.superInvestIhlBuffer = investmentAssetDto.getSuperInvestIhlBuffer();
        this.propertyType = investmentAssetDto.getPropertyType();
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetType() {
        return assetType.name();
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public DateTime getEffectiveDate() {
        return effectiveDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public BigDecimal getDollarGain() {
        return dollarGain;
    }

    public BigDecimal getPercentGain() {
        if (percentGain != null) {
            return percentGain.divide(BigDecimal.valueOf(100));
        }
        return null;
    }

    public BigDecimal getAllocationPercent() {
        return allocationPercent;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Boolean getHasPending() {
        return hasPending;
    }

    public Boolean isPrepaymentAsset() {
        return prepaymentAsset;
    }

    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public BigDecimal getAvailableQuantityToTransfer() {
        return availableQuantityToTransfer;
    }

    public String getStatus() {
        return status;
    }

    public String getIsin() {
        return isin;
    }

    public Boolean getIncomeOnly() {
        return incomeOnly;
    }

    public String getDistributionMethod() {
        if (assetType == AssetType.MANAGED_FUND) {
            return distributionMethod;
        }
        return null;
    }

    public String getDividendMethod() {
        if (assetType == AssetType.SHARE) {
            return distributionMethod;
        }
        return null;
    }

    public BigDecimal getInvestmentHoldingLimit() {
        return investmentHoldingLimit;
    }

    public BigDecimal getInvestmentHoldingLimitBuffer() {
        return investmentHoldingLimitBuffer;
    }

    public String getPropertyType() {
        return propertyType;
    }

    /**
     * Get the property type id external holding asset
     *
     * @param asset - External Holding asset
     */
    private String getHoldingPropertyType(ExternalValuationAssetImpl asset) {
        return asset.getPropertyType() != null ? PropertyType.getByCode(asset.getPropertyType()).getShortDesc() : null;
    }

    public BigDecimal getSuperInvestIhl() {
        return superInvestIhl;
    }

    public BigDecimal getSuperInvestIhlBuffer() {
        return superInvestIhlBuffer;
    }
}
