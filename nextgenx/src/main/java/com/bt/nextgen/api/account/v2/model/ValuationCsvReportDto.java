package com.bt.nextgen.api.account.v2.model;

import java.math.BigDecimal;

@Deprecated
public class ValuationCsvReportDto {
    private ValuationCsvReportBaseDto valuationBaseDetails;
    private InvestmentAssetDto investmentAssetDetails;
    // TD fields
    private final String maturityDate;
    private final String maturityInstruction;

    private final BigDecimal percentGain;
    private final String lastPriceDate;
    private final String distributionMethod;

    // Constructor for TD
    public ValuationCsvReportDto(ValuationCsvReportBaseDto valuationBaseDetails, String maturityDate, String maturityInstruction) {
        this.valuationBaseDetails = valuationBaseDetails;
        this.maturityDate = maturityDate;
        this.maturityInstruction = maturityInstruction;
        this.percentGain = null;
        this.lastPriceDate = null;
        this.distributionMethod = null;
    }

    // Constructor for MP and MF
    public ValuationCsvReportDto(ValuationCsvReportBaseDto valuationBaseDetails, InvestmentAssetDto investmentAssetDetails,
            BigDecimal percentGain, String lastPriceDate, String distributionMethod) {
        this.valuationBaseDetails = valuationBaseDetails;
        this.investmentAssetDetails = investmentAssetDetails;
        this.percentGain = percentGain;
        this.lastPriceDate = lastPriceDate;
        this.distributionMethod = distributionMethod;
        this.maturityDate = null;
        this.maturityInstruction = null;
    }

    // Constructor for Cash
    public ValuationCsvReportDto(ValuationCsvReportBaseDto valuationBaseDetails) {
        this.valuationBaseDetails = valuationBaseDetails;
        this.maturityDate = null;
        this.maturityInstruction = null;
        this.percentGain = null;
        this.lastPriceDate = null;
        this.distributionMethod = null;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public String getMaturityInstruction() {
        return maturityInstruction;
    }

    public InvestmentAssetDto getInvestmentAssetDetails() {
        return investmentAssetDetails;
    }

    public BigDecimal getPercentGain() {
        return percentGain;
    }

    public String getLastPriceDate() {
        return lastPriceDate;
    }

    public String getDistributionMethod() {
        return distributionMethod;
    }

    public ValuationCsvReportBaseDto getValuationBaseDetails() {
        return valuationBaseDetails;
    }
}
