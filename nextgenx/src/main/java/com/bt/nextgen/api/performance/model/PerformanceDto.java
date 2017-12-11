package com.bt.nextgen.api.performance.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

public class PerformanceDto extends BaseDto implements Comparable<PerformanceDto> {
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
    private BigDecimal frankingCredit;
    private BigDecimal benchmarkDiff;
    private BigDecimal performanceIncomePercentage;
    private BigDecimal performanceGrowthPercentage;
    private String containerType;
    private String assetType;
    private String referenceAssetType;
    private String referenceAssetCode;

    public PerformanceDto(String name, String assetCode, BigDecimal openingBalance, BigDecimal closingBalance,
            BigDecimal purchase, BigDecimal sales, BigDecimal movement, BigDecimal netIncome, BigDecimal performancePercentage,
            BigDecimal performanceDollar, Integer periodHeld, BigDecimal frankingCredit, BigDecimal benchmarkDiff,
            BigDecimal performanceIncomePercentage, BigDecimal performanceGrowthPercentage, String containerType,
            String assetType) {
        super();

        this.name = name;
        this.assetCode = assetCode;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.purchase = purchase;
        this.sales = sales;
        this.movement = movement;
        this.netIncome = netIncome;
        this.performancePercentage = performancePercentage;
        this.performanceDollar = performanceDollar;
        this.periodHeld = periodHeld;
        this.frankingCredit = frankingCredit;
        this.benchmarkDiff = benchmarkDiff;
        this.performanceIncomePercentage = performanceIncomePercentage;
        this.performanceGrowthPercentage = performanceGrowthPercentage;
        this.containerType = containerType;
        this.assetType = assetType;
    }

    public String getName() {
        return name;
    }

    public String getAssetCode() {
        return assetCode;
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

    public BigDecimal getFrankingCredit() {
        return frankingCredit;
    }

    public BigDecimal getBenchmarkDiff() {
        return benchmarkDiff;
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

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getReferenceAssetType() {
        return referenceAssetType;
    }

    public void setReferenceAssetType(String referenceAssetType) {
        this.referenceAssetType = referenceAssetType;
    }

    @Override
    public int compareTo(PerformanceDto o) {
        if (o == null || o.getName() == null) {
            return -1;
        }
        if (name == null) {
            return 1;
        }
        return name.toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public String getReferenceAssetCode() {
        return referenceAssetCode;
    }

    public void setReferenceAssetCode(String referenceAssetCode) {
        this.referenceAssetCode = referenceAssetCode;
    }

}
