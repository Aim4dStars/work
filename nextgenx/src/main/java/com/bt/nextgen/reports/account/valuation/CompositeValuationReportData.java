package com.bt.nextgen.reports.account.valuation;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompositeValuationReportData extends AbstractValuationReportData {

    private InvestmentValuationDto investmentValuationDto;
    private List<ValuationReportData> valuationReportData = new ArrayList<>();

    public CompositeValuationReportData(InvestmentValuationDto investmentValuationDto) {
        this.investmentValuationDto = investmentValuationDto;

    }

    public String getCode() {
        ManagedPortfolioValuationDto managedPortfolioValuation = (ManagedPortfolioValuationDto) investmentValuationDto;
        return managedPortfolioValuation.getAssetCode();
    }

    public String getName() {
        return investmentValuationDto.getName();
    }

    public String getAllocationPercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, investmentValuationDto.getPortfolioPercent());
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, investmentValuationDto.getBalance());
    }

    public String getAverageCost() {
        ManagedPortfolioValuationDto managedPortfolioValuationDto = (ManagedPortfolioValuationDto) investmentValuationDto;
        BigDecimal cost = null;
        if (!managedPortfolioValuationDto.getExternalAsset()) {
            cost = managedPortfolioValuationDto.getCost();
            return ReportFormatter.format(ReportFormat.CURRENCY, cost);
        }
        else {
            return ReportFormatter.format(ReportFormat.CURRENCY, cost);
        }

    }

    public String getDollarGain() {
        ManagedPortfolioValuationDto managedPortfolioValuationDto = (ManagedPortfolioValuationDto) investmentValuationDto;
        BigDecimal dollarGain = null;
        if (!managedPortfolioValuationDto.getExternalAsset()) {
            dollarGain = managedPortfolioValuationDto.getCapgainDollar();
            return ReportFormatter.format(ReportFormat.CURRENCY, dollarGain);
        }
        else {
            return ReportFormatter.format(ReportFormat.CURRENCY, dollarGain);
        }
    }

    public String getPercentGain() {
        ManagedPortfolioValuationDto managedPortfolioValuationDto = (ManagedPortfolioValuationDto) investmentValuationDto;
        BigDecimal percentGain = null;
        if (!managedPortfolioValuationDto.getExternalAsset()) {
            percentGain = managedPortfolioValuationDto.getCapgainPercent();
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentGain);
        }
        else {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentGain);
        }

    }

    public Boolean getExternalAsset() {
        ManagedPortfolioValuationDto managedPortfolioValuationDto = (ManagedPortfolioValuationDto) investmentValuationDto;
        return managedPortfolioValuationDto.getExternalAsset();
    }

    public String getSource() {
        ManagedPortfolioValuationDto managedPortfolioValuationDto = (ManagedPortfolioValuationDto) investmentValuationDto;
        return managedPortfolioValuationDto.getSource();
    }

    @Override
    public List<ValuationReportData> getChildren() {
        ManagedPortfolioValuationDto managedPortfolioValuationDto = (ManagedPortfolioValuationDto) investmentValuationDto;
        for (InvestmentAssetDto investmentAsset : managedPortfolioValuationDto.getInvestmentAssets()) {
            valuationReportData.add(new InvestmentValuationReportData(investmentAsset, managedPortfolioValuationDto
                    .getExternalAsset(), managedPortfolioValuationDto.getSource()));
        }
        valuationReportData.add(new SimpleValuationReportData("Income accrued", null, managedPortfolioValuationDto.getIncome(),
                managedPortfolioValuationDto.getIncomePercent(), false, null, null));
        return valuationReportData;
    }

}
