package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedFundValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.OtherAssetValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.QuantisedAssetValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class InvestmentDetailDto {
    private String investmentType;
    private String type;
    private Boolean externalAsset;
    private String source;
    private String categoryName;
    private String assetCode;
    private String assetName;
    private BigDecimal balance;
    private BigDecimal portfolioPercent;
    private BigDecimal rate;
    private DateTime maturityDate;
    private String maturityInstructions;
    private String incomeElection;
    private BigDecimal lastPrice;
    private DateTime lastPriceDate;
    private BigDecimal quantity;
    private BigDecimal averageCost;
    private BigDecimal capgainDollar;
    private BigDecimal capgainPercent;
    private String thirdPartySource;

    public InvestmentDetailDto(CashManagementValuationDto cashManagementValuation, String thirdPartySource) {
        this.categoryName = cashManagementValuation.getCategoryName();
        this.type = cashManagementValuation.getType();
        this.externalAsset = cashManagementValuation.getExternalAsset();
        this.source = cashManagementValuation.getSource();
        this.assetName = cashManagementValuation.getName();
        this.balance = cashManagementValuation.getValueDateBalance();
        this.portfolioPercent = cashManagementValuation.getValueDatePercent().multiply(new BigDecimal(100));
        this.rate = cashManagementValuation.getInterestRate().multiply(new BigDecimal(100));
        this.type = cashManagementValuation.getType();
        this.thirdPartySource = thirdPartySource;
    }

    public InvestmentDetailDto(String categoryName, String assetName, BigDecimal balance, BigDecimal portfolioPercent) {
        this.categoryName = categoryName;
        this.externalAsset = false;
        this.assetName = assetName;
        this.balance = balance;
        this.portfolioPercent = portfolioPercent.multiply(new BigDecimal(100));
    }

    public InvestmentDetailDto(TermDepositValuationDto termDepositValuation, String thirdPartySource) {
        this.categoryName = termDepositValuation.getCategoryName();
        this.type = termDepositValuation.getType();
        this.assetName = termDepositValuation.getName();
        this.externalAsset = termDepositValuation.getExternalAsset();
        this.source = termDepositValuation.getSource();
        this.balance = termDepositValuation.getBalance();
        this.portfolioPercent = termDepositValuation.getPortfolioPercent().multiply(new BigDecimal(100));
        this.rate = termDepositValuation.getInterestRate().multiply(new BigDecimal(100));
        this.maturityDate = termDepositValuation.getMaturityDate();
        this.maturityInstructions = termDepositValuation.getMaturityInstruction();
        this.type = termDepositValuation.getType();
        this.thirdPartySource = thirdPartySource;
    }

    public InvestmentDetailDto(ManagedPortfolioValuationDto managedPortfolioValuation) {
        this.categoryName = managedPortfolioValuation.getCategoryName();
        this.externalAsset = managedPortfolioValuation.getExternalAsset();
        this.assetCode = managedPortfolioValuation.getAssetCode();
        this.source = managedPortfolioValuation.getSource();
        this.assetName = managedPortfolioValuation.getName();
        this.balance = managedPortfolioValuation.getBalance();
        this.portfolioPercent = managedPortfolioValuation.getPortfolioPercent().multiply(new BigDecimal(100));
        this.type = managedPortfolioValuation.getType();
    }

    public InvestmentDetailDto(ManagedFundValuationDto managedFundValuation) {
        this.categoryName = managedFundValuation.getCategoryName();
        this.type = managedFundValuation.getType();
        this.externalAsset = managedFundValuation.getExternalAsset();
        this.balance = managedFundValuation.getBalance();
        this.portfolioPercent = managedFundValuation.getPortfolioPercent().multiply(new BigDecimal(100));
        this.assetCode = managedFundValuation.getInvestmentAsset().getAssetCode();
        this.assetName = managedFundValuation.getName();
        this.incomeElection = managedFundValuation.getDistributionMethod();
        this.lastPrice = managedFundValuation.getInvestmentAsset().getUnitPrice();
        this.lastPriceDate = managedFundValuation.getInvestmentAsset().getEffectiveDate();
        this.quantity = !managedFundValuation.getPendingSellDown()
                && !managedFundValuation.getInvestmentAsset().isPrepaymentAsset() ? managedFundValuation.getInvestmentAsset()
                .getQuantity() : null;
        this.averageCost = managedFundValuation.getInvestmentAsset().getAverageCost();
        this.capgainDollar = managedFundValuation.getInvestmentAsset().getDollarGain();
        if (managedFundValuation.getInvestmentAsset().getPercentGain() != null) {
            this.capgainPercent = managedFundValuation.getInvestmentAsset().getPercentGain().multiply(new BigDecimal(100));
        }
        this.type = managedFundValuation.getType();
    }

    public InvestmentDetailDto(ShareValuationDto shareValuation) {
        this.categoryName = shareValuation.getCategoryName();
        this.type = shareValuation.getType();
        this.externalAsset = shareValuation.getExternalAsset();
        this.source = shareValuation.getSource();
        this.assetCode = shareValuation.getInvestmentAsset().getAssetCode();
        this.assetName = shareValuation.getName();
        this.balance = shareValuation.getBalance();
        this.portfolioPercent = shareValuation.getPortfolioPercent().multiply(new BigDecimal(100));
        this.incomeElection = shareValuation.getDividendMethod();
        this.lastPrice = (!shareValuation.getInvestmentAsset().isPrepaymentAsset()) ? shareValuation.getInvestmentAsset()
                .getUnitPrice() : null;
        this.lastPriceDate = (!shareValuation.getInvestmentAsset().isPrepaymentAsset()) ? shareValuation.getInvestmentAsset()
                .getEffectiveDate() : null;
        this.quantity = (!shareValuation.getInvestmentAsset().isPrepaymentAsset()) ? shareValuation.getInvestmentAsset()
                .getQuantity() : null;
        this.averageCost = shareValuation.getInvestmentAsset().getAverageCost();
        this.capgainDollar = shareValuation.getInvestmentAsset().getDollarGain();
        if (shareValuation.getInvestmentAsset().getPercentGain() != null) {
            this.capgainPercent = shareValuation.getInvestmentAsset().getPercentGain().multiply(new BigDecimal(100));
        }
        this.type = shareValuation.getType();
    }

    public InvestmentDetailDto(QuantisedAssetValuationDto quantisedAssetValuation) {
        this.categoryName = quantisedAssetValuation.getCategoryName();
        this.type = quantisedAssetValuation.getType();
        this.externalAsset = quantisedAssetValuation.getExternalAsset();
        this.source = quantisedAssetValuation.getSource();
        this.assetCode = quantisedAssetValuation.getInvestmentAsset().getAssetCode();
        this.assetName = quantisedAssetValuation.getName();
        this.balance = quantisedAssetValuation.getBalance();
        this.portfolioPercent = quantisedAssetValuation.getPortfolioPercent().multiply(new BigDecimal(100));
        this.quantity = quantisedAssetValuation.getUnits();
        this.type = quantisedAssetValuation.getType();
    }

    public InvestmentDetailDto(OtherAssetValuationDto otherAssetValuation) {
        this.categoryName = otherAssetValuation.getCategoryName();
        this.type = otherAssetValuation.getType();
        this.externalAsset = otherAssetValuation.getExternalAsset();
        this.source = otherAssetValuation.getSource();
        this.assetCode = otherAssetValuation.getInvestmentAsset().getAssetCode();
        this.balance = otherAssetValuation.getBalance();
        this.portfolioPercent = otherAssetValuation.getPortfolioPercent().multiply(new BigDecimal(100));
        this.type = otherAssetValuation.getType();
    }

    public InvestmentDetailDto(InvestmentAssetDto investmentAsset, String categoryName, String incomeElection) {
        this.categoryName = categoryName;
        this.type = investmentAsset.getType();
        this.investmentType = investmentAsset.getAssetType();
        this.externalAsset = false;
        this.assetCode = investmentAsset.getAssetCode();
        this.assetName = investmentAsset.getAssetName();
        this.balance = investmentAsset.getMarketValue();
        this.portfolioPercent = investmentAsset.getAllocationPercent().multiply(new BigDecimal(100));
        this.lastPrice = investmentAsset.getUnitPrice();
        this.quantity = investmentAsset.isPrepaymentAsset() ? null : investmentAsset.getQuantity();
        this.averageCost = investmentAsset.getAverageCost();
        this.capgainDollar = investmentAsset.getDollarGain();
        if (investmentAsset.getPercentGain() != null) {
            this.capgainPercent = investmentAsset.getPercentGain().multiply(new BigDecimal(100));
        }
        this.incomeElection = incomeElection;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getPortfolioPercent() {
        return portfolioPercent;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public DateTime getMaturityDate() {
        return maturityDate;
    }

    public String getMaturityInstructions() {
        return maturityInstructions;
    }

    public String getIncomeElection() {
        return incomeElection;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public DateTime getLastPriceDate() {
        return lastPriceDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAverageCost() {
        return averageCost;
    }

    public BigDecimal getCapgainDollar() {
        return capgainDollar;
    }

    public BigDecimal getCapgainPercent() {
        return capgainPercent;
    }

    public Boolean getExternalAsset() {
        return externalAsset;
    }

    public String getSource() {
        return source;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThirdPartySource() {
        return thirdPartySource;
    }
}
