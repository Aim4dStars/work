package com.bt.nextgen.reports.account.valuation;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.btfin.panorama.service.integration.asset.AssetType;

public class InvestmentValuationReportData extends AbstractValuationReportData {
    private InvestmentAssetDto investmentAsset;
    private Boolean externalAsset;
    private String source;

    public InvestmentValuationReportData(InvestmentAssetDto investmentAsset, Boolean externalAsset, String source) {
        this.investmentAsset = investmentAsset;
        this.externalAsset = externalAsset;
        this.source = source;
    }

    public String getCode() {
        return investmentAsset.getAssetCode();
    }

    public String getName() {
        StringBuilder assetName = new StringBuilder();
        if (externalAsset && StringUtils.isNotBlank(source)) {
            assetName.append(source).append("<br/>");
        }
        if (StringUtils.isNotBlank(investmentAsset.getAssetCode())) {
            assetName.append("<b>");
            assetName.append(investmentAsset.getAssetCode());
            assetName.append("</b>");
            assetName.append(" &#183 ");
        }
        return assetName.append(investmentAsset.getAssetName()).toString();
    }

    public String getQuantity() {
        if (AssetType.MANAGED_FUND.name().equals(investmentAsset.getAssetType())) {
            return ReportFormatter.format(ReportFormat.MANAGED_FUND_UNIT, investmentAsset.getQuantity());
        }
        else {
            return ReportFormatter.format(ReportFormat.UNITS, investmentAsset.getQuantity());
        }
    }

    public String getAverageCost() {
        BigDecimal averageCost = null;
        if (!externalAsset) {
            averageCost = investmentAsset.getAverageCost();
            return ReportFormatter.format(ReportFormat.CURRENCY, investmentAsset.getAverageCost());
        }
        else {
            return ReportFormatter.format(ReportFormat.CURRENCY, averageCost);
        }

    }

    public String getDollarGain() {
        BigDecimal dollarGain = null;
        if (!externalAsset) {
            dollarGain = investmentAsset.getDollarGain();
            return ReportFormatter.format(ReportFormat.CURRENCY, dollarGain);
        }
        else {
            return ReportFormatter.format(ReportFormat.CURRENCY, dollarGain);
        }
    }

    public String getPercentGain() {
        BigDecimal percentGain = null;
        if (!externalAsset) {
            percentGain = investmentAsset.getPercentGain();
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentGain);
        }
        else {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, percentGain);
        }
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, investmentAsset.getMarketValue());
    }

    public String getAllocationPercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, investmentAsset.getAllocationPercent());
    }

    public String getLastPrice() {
        BigDecimal lastPrice = null;
        if (!externalAsset) {
            lastPrice = investmentAsset.getUnitPrice();
            return ReportFormatter.format(ReportFormat.CURRENCY, lastPrice);
        } else {
            return ReportFormatter.format(ReportFormat.CURRENCY, lastPrice);
        }
    }

    public String getLastPriceDate() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, investmentAsset.getEffectiveDate());
    }

    public Boolean getExternalAsset() {
        return externalAsset;
    }

    public String getSource() {
        return source;
    }

    @Override
    public List<ValuationReportData> getChildren() {
        return Collections.emptyList();
    }

}
