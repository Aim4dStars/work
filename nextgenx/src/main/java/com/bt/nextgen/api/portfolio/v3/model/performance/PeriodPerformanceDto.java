package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.core.api.model.BaseDto;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.asset.AssetPerformance;
import com.btfin.panorama.service.integration.asset.AssetType;

import java.math.BigDecimal;

public class PeriodPerformanceDto extends BaseDto implements Comparable<PeriodPerformanceDto> {

    private String name;
    private String assetCode;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal purchase;
    private BigDecimal sales;
    private BigDecimal movement;
    private BigDecimal netIncome;
    private BigDecimal performancePercentage;
    private BigDecimal performanceDollar;
    private Integer periodHeld;
    private BigDecimal performanceIncomePercentage;
    private BigDecimal performanceGrowthPercentage;
    private String containerType;
    private AssetType assetType;
    private String referenceAssetType;
    private String referenceAssetCode;

    public PeriodPerformanceDto(AssetPerformance assetPerformance, String name, AssetType assetType) {
        super();

        this.name = name;
        this.assetType = assetType;
        this.assetCode = getSafeString(assetPerformance.getCode());

        this.openingBalance = assetPerformance.getOpeningBalance();
        this.closingBalance = assetPerformance.getClosingBalance();
        this.purchase = assetPerformance.getPurchases();
        this.sales = assetPerformance.getSales();
        this.movement = assetPerformance.getMarketMovement();
        this.netIncome = assetPerformance.getIncome();
        this.performanceDollar = assetPerformance.getPerformanceDollar();
        this.periodHeld = assetPerformance.getPeriodOfDays();
        this.containerType = assetPerformance.getContainerType().getCode();
        
        this.performancePercentage = getSafePercent(assetPerformance.getPerformancePercent());
        this.performanceIncomePercentage = getSafePercent(assetPerformance.getIncomeReturn());
        this.performanceGrowthPercentage = getSafePercent(assetPerformance.getCapitalReturn());
    }

    public String getName() {
        return name;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public BigDecimal getPurchase() {
        return purchase;
    }

    public BigDecimal getSales() {
        return sales;
    }

    public BigDecimal getMovement() {
        return movement;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public BigDecimal getPerformancePercentage() {
        return performancePercentage;
    }

    public BigDecimal getPerformanceDollar() {
        return performanceDollar;
    }

    public Integer getPeriodHeld() {
        return periodHeld;
    }

    public String getContainerType() {
        return containerType;
    }

    public BigDecimal getPerformanceIncomePercentage() {
        return performanceIncomePercentage;
    }

    public BigDecimal getPerformanceGrowthPercentage() {
        return performanceGrowthPercentage;
    }

    public AssetType getAssetTypeCode() {
        return assetType;
    }

    public String getAssetType() {
        return assetType.getDisplayName();
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public String getReferenceAssetType() {
        return referenceAssetType;
    }

    public void setReferenceAssetType(String referenceAssetType) {
        this.referenceAssetType = referenceAssetType;
    }

    public String getReferenceAssetCode() {
        return referenceAssetCode;
    }

    public void setReferenceAssetCode(String referenceAssetCode) {
        this.referenceAssetCode = referenceAssetCode;
    }

    @Override
    public int compareTo(PeriodPerformanceDto o) {
        if (o == null || o.getName() == null) {
            return -1;
        } else if (this.name == null) {
            return 1;
        } else {
            return this.name.compareToIgnoreCase(o.getName());
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PeriodPerformanceDto)) {
            return false;
        }

        PeriodPerformanceDto other = (PeriodPerformanceDto) o;
        if (this.name == null) {
            return other.getName() == null;
        }
        return this.name.equalsIgnoreCase(other.getName());
    }

    private String getSafeString(String value) {
        if (value != null) {
            return value;
        }
        return Constants.EMPTY_STRING;
    }
    
    private BigDecimal getSafePercent(BigDecimal value) {
        if (value != null) {
            return value.divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}
