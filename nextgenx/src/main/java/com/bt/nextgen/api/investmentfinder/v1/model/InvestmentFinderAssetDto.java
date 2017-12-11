package com.bt.nextgen.api.investmentfinder.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * Represents an asset that will be displayed in various categories on the asset finder screens.
 *
 */
public class InvestmentFinderAssetDto extends BaseDto {

    private String assetCode;
    private String assetType;
    private String name;
    private String isin;
    private String securityCode;
    private String exchangeCode;
    private String gicsSector;
    private String gicsIndustry;
    private Boolean asx50;
    private Boolean asx200;
    private Boolean asx300;
    private BigDecimal eps1Year;
    private BigDecimal eps3Year;
    private BigDecimal eps5Year;
    private BigDecimal marketCapitalisation;
    private BigDecimal dividendYield1Year;
    private BigDecimal sustainabilityScore;
    private String performanceDataPoints1Year;
    private BigDecimal cost;
    private BigDecimal performance1Year;
    private BigDecimal performance3Year;
    private BigDecimal performance5Year;
    private String issuer;
    private String benchmark;
    private BigDecimal indirectCostRatio;
    private String apirCode;
    private String fundStandardName;
    private String fundLegalName;
    private String fundManagerName;
    private String fundCategory;
    private BigDecimal allocationAustralianShares;
    private BigDecimal allocationInternationalShares;
    private BigDecimal allocationAustralianProperty;
    private BigDecimal allocationInternationalProperty;
    private BigDecimal allocationAustralianCash;
    private BigDecimal allocationInternationalCash;
    private BigDecimal allocationAustralianFixedInterest;
    private BigDecimal allocationInternationalFixedInterest;
    private BigDecimal allocationTotalAustralian;
    private BigDecimal allocationTotalInternational;
    private BigDecimal allocationCash;
    private BigDecimal allocationShares;
    private BigDecimal allocationManagedFunds;
    private BigDecimal totalIncome1Year;
    private BigDecimal totalIncome3Year;
    private BigDecimal totalIncome5Year;
    private BigDecimal totalReturn1Year;
    private BigDecimal totalReturn3Year;
    private BigDecimal totalReturn5Year;

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(final String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(final String assetType) {
        this.assetType = assetType;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(final String isin) {
        this.isin = isin;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(final String securityCode) {
        this.securityCode = securityCode;
    }

    public String getExchangeCode() {
        return exchangeCode;
    }

    public void setExchangeCode(final String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    public String getGicsSector() {
        return gicsSector;
    }

    public void setGicsSector(final String gicsSector) {
        this.gicsSector = gicsSector;
    }

    public String getGicsIndustry() {
        return gicsIndustry;
    }

    public void setGicsIndustry(final String gicsIndustry) {
        this.gicsIndustry = gicsIndustry;
    }

    public Boolean getAsx50() {
        return asx50;
    }

    public void setAsx50(final Boolean asx50) {
        this.asx50 = asx50;
    }

    public Boolean getAsx200() {
        return asx200;
    }

    public void setAsx200(final Boolean asx200) {
        this.asx200 = asx200;
    }

    public Boolean getAsx300() {
        return asx300;
    }

    public void setAsx300(final Boolean asx300) {
        this.asx300 = asx300;
    }

    public BigDecimal getEps1Year() {
        return eps1Year;
    }

    public void setEps1Year(final BigDecimal eps1Year) {
        this.eps1Year = eps1Year;
    }

    public BigDecimal getEps3Year() {
        return eps3Year;
    }

    public void setEps3Year(final BigDecimal eps3Year) {
        this.eps3Year = eps3Year;
    }

    public BigDecimal getEps5Year() {
        return eps5Year;
    }

    public void setEps5Year(final BigDecimal eps5Year) {
        this.eps5Year = eps5Year;
    }

    public BigDecimal getMarketCapitalisation() {
        return marketCapitalisation;
    }

    public void setMarketCapitalisation(final BigDecimal marketCapitalisation) {
        this.marketCapitalisation = marketCapitalisation;
    }

    public BigDecimal getDividendYield1Year() {
        return dividendYield1Year;
    }

    public void setDividendYield1Year(final BigDecimal dividendYield1Year) {
        this.dividendYield1Year = dividendYield1Year;
    }

    public BigDecimal getSustainabilityScore() {
        return sustainabilityScore;
    }

    public void setSustainabilityScore(final BigDecimal sustainabilityScore) {
        this.sustainabilityScore = sustainabilityScore;
    }

    public String getPerformanceDataPoints1Year() {
        return performanceDataPoints1Year;
    }

    public void setPerformanceDataPoints1Year(final String performanceDataPoints1Year) {
        this.performanceDataPoints1Year = performanceDataPoints1Year;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(final BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getPerformance1Year() {
        return performance1Year;
    }

    public void setPerformance1Year(final BigDecimal performance1Year) {
        this.performance1Year = performance1Year;
    }

    public BigDecimal getPerformance3Year() {
        return performance3Year;
    }

    public void setPerformance3Year(final BigDecimal performance3Year) {
        this.performance3Year = performance3Year;
    }

    public BigDecimal getPerformance5Year() {
        return performance5Year;
    }

    public void setPerformance5Year(final BigDecimal performance5Year) {
        this.performance5Year = performance5Year;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(final String benchmark) {
        this.benchmark = benchmark;
    }

    public BigDecimal getIndirectCostRatio() {
        return indirectCostRatio;
    }

    public void setIndirectCostRatio(final BigDecimal indirectCostRatio) {
        this.indirectCostRatio = indirectCostRatio;
    }

    public String getApirCode() {
        return apirCode;
    }

    public void setApirCode(final String apirCode) {
        this.apirCode = apirCode;
    }

    public String getFundStandardName() {
        return fundStandardName;
    }

    public void setFundStandardName(final String fundStandardName) {
        this.fundStandardName = fundStandardName;
    }

    public String getFundLegalName() {
        return fundLegalName;
    }

    public void setFundLegalName(final String fundLegalName) {
        this.fundLegalName = fundLegalName;
    }

    public String getFundManagerName() {
        return fundManagerName;
    }

    public void setFundManagerName(final String fundManagerName) {
        this.fundManagerName = fundManagerName;
    }

    public String getFundCategory() {
        return fundCategory;
    }

    public void setFundCategory(final String fundCategory) {
        this.fundCategory = fundCategory;
    }

    public BigDecimal getAllocationAustralianShares() {
        return allocationAustralianShares;
    }

    public void setAllocationAustralianShares(final BigDecimal allocationAustralianShares) {
        this.allocationAustralianShares = allocationAustralianShares;
    }

    public BigDecimal getAllocationInternationalShares() {
        return allocationInternationalShares;
    }

    public void setAllocationInternationalShares(final BigDecimal allocationInternationalShares) {
        this.allocationInternationalShares = allocationInternationalShares;
    }

    public BigDecimal getAllocationAustralianProperty() {
        return allocationAustralianProperty;
    }

    public void setAllocationAustralianProperty(final BigDecimal allocationAustralianProperty) {
        this.allocationAustralianProperty = allocationAustralianProperty;
    }

    public BigDecimal getAllocationInternationalProperty() {
        return allocationInternationalProperty;
    }

    public void setAllocationInternationalProperty(final BigDecimal allocationInternationalProperty) {
        this.allocationInternationalProperty = allocationInternationalProperty;
    }

    public BigDecimal getAllocationAustralianCash() {
        return allocationAustralianCash;
    }

    public void setAllocationAustralianCash(final BigDecimal allocationAustralianCash) {
        this.allocationAustralianCash = allocationAustralianCash;
    }

    public BigDecimal getAllocationInternationalCash() {
        return allocationInternationalCash;
    }

    public void setAllocationInternationalCash(final BigDecimal allocationInternationalCash) {
        this.allocationInternationalCash = allocationInternationalCash;
    }

    public BigDecimal getAllocationAustralianFixedInterest() {
        return allocationAustralianFixedInterest;
    }

    public void setAllocationAustralianFixedInterest(final BigDecimal allocationAustralianFixedInterest) {
        this.allocationAustralianFixedInterest = allocationAustralianFixedInterest;
    }

    public BigDecimal getAllocationInternationalFixedInterest() {
        return allocationInternationalFixedInterest;
    }

    public void setAllocationInternationalFixedInterest(final BigDecimal allocationInternationalFixedInterest) {
        this.allocationInternationalFixedInterest = allocationInternationalFixedInterest;
    }

    public BigDecimal getAllocationTotalAustralian() {
        return allocationTotalAustralian;
    }

    public void setAllocationTotalAustralian(final BigDecimal allocationTotalAustralian) {
        this.allocationTotalAustralian = allocationTotalAustralian;
    }

    public BigDecimal getAllocationTotalInternational() {
        return allocationTotalInternational;
    }

    public void setAllocationTotalInternational(final BigDecimal allocationTotalInternational) {
        this.allocationTotalInternational = allocationTotalInternational;
    }

    public BigDecimal getAllocationCash() {
        return allocationCash;
    }

    public void setAllocationCash(final BigDecimal allocationCash) {
        this.allocationCash = allocationCash;
    }

    public BigDecimal getAllocationShares() {
        return allocationShares;
    }

    public void setAllocationShares(final BigDecimal allocationShares) {
        this.allocationShares = allocationShares;
    }

    public BigDecimal getAllocationManagedFunds() {
        return allocationManagedFunds;
    }

    public void setAllocationManagedFunds(final BigDecimal allocationManagedFunds) {
        this.allocationManagedFunds = allocationManagedFunds;
    }

    public BigDecimal getTotalIncome1Year() {
        return totalIncome1Year;
    }

    public void setTotalIncome1Year(final BigDecimal totalIncome1Year) {
        this.totalIncome1Year = totalIncome1Year;
    }

    public BigDecimal getTotalIncome3Year() {
        return totalIncome3Year;
    }

    public void setTotalIncome3Year(final BigDecimal totalIncome3Year) {
        this.totalIncome3Year = totalIncome3Year;
    }

    public BigDecimal getTotalIncome5Year() {
        return totalIncome5Year;
    }

    public void setTotalIncome5Year(final BigDecimal totalIncome5Year) {
        this.totalIncome5Year = totalIncome5Year;
    }

    public BigDecimal getTotalReturn1Year() {
        return totalReturn1Year;
    }

    public void setTotalReturn1Year(final BigDecimal totalReturn1Year) {
        this.totalReturn1Year = totalReturn1Year;
    }

    public BigDecimal getTotalReturn3Year() {
        return totalReturn3Year;
    }

    public void setTotalReturn3Year(final BigDecimal totalReturn3Year) {
        this.totalReturn3Year = totalReturn3Year;
    }

    public BigDecimal getTotalReturn5Year() {
        return totalReturn5Year;
    }

    public void setTotalReturn5Year(final BigDecimal totalReturn5Year) {
        this.totalReturn5Year = totalReturn5Year;
    }

}
