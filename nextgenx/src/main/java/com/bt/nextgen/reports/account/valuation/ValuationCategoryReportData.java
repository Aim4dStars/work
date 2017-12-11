package com.bt.nextgen.reports.account.valuation;

import java.util.ArrayList;
import java.util.List;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDtoInterface;
import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.service.integration.base.SystemType;
import com.btfin.panorama.service.integration.asset.AssetType;

public class ValuationCategoryReportData extends AbstractValuationReportData {
    private ValuationSummaryDto category;
    private List<ValuationReportData> valuationReportData = new ArrayList<>();

    public ValuationCategoryReportData(ValuationSummaryDto category) {
        this.category = category;
    }

    public String getCategoryName() {
        return category.getCategoryName();
    }

    public String getBalance() {
        return ReportFormatter.format(ReportFormat.CURRENCY, category.getBalance());
    }

    public String getCategoryPercent() {
        return ReportFormatter.format(ReportFormat.PERCENTAGE, category.getPortfolioPercent());
    }

    public String getSummaryDescription() {
        return new StringBuilder("Total ").append(category.getCategoryName().toLowerCase()).append(" balance").toString();
    }

    public String getType() {
        AssetType assetType = category.getAssetType();
        String type = null;
        switch (assetType) {
            case CASH:
                type = "CashManagementValuation";
                break;
            case TERM_DEPOSIT:
                type = "TermDepositValuation";
                break;
            case MANAGED_FUND:
                type = "ManagedFundValuation";
                break;
            case SHARE:
                type = "ShareValuation";
                break;
            case MANAGED_PORTFOLIO:
            case TAILORED_PORTFOLIO:
                type = "ManagedPortfolioValuation";
                break;
            case INTERNATIONAL_SHARE:
                type = "QuantisedValuation";
                break;
            default:
                type = "OtherValuation";
        }
        return type;
    }

    @Override
    public List<ValuationReportData> getChildren() {
        for (InvestmentValuationDto investmentValuation : category.getInvestments()) {
            addChild(buildChildFromValuation(investmentValuation));
        }
        if (!SystemType.WRAP.name().equals(category.getThirdPartySource())) {
            addChild(buildOutstandingCashChild());
            addChild(buildIncomeChild());
        }
        return valuationReportData;
    }

    private ValuationReportData buildChildFromValuation(InvestmentValuationDto investmentValuation) {
        ValuationReportData valuation = null;
        if (investmentValuation instanceof CashManagementValuationDto) {
            valuation = new CashValuationReportData((CashManagementValuationDto) investmentValuation, category.getThirdPartySource());
        }
        else if (investmentValuation instanceof TermDepositValuationDto) {
            valuation = new TermDepositValuationReportData((TermDepositValuationDto) investmentValuation, category.getThirdPartySource());
        }
        else if (investmentValuation instanceof ManagedPortfolioValuationDto) {
            valuation = new CompositeValuationReportData(investmentValuation);
        } else if (investmentValuation instanceof ShareValuationDto){
            valuation = new ShareValuationReportData((ShareValuationDto) investmentValuation);
        } else if (investmentValuation instanceof InvestmentAssetDtoInterface) {
            InvestmentAssetDtoInterface investmentAsset = (InvestmentAssetDtoInterface) investmentValuation;
            valuation = new InvestmentValuationReportData(investmentAsset.getInvestmentAsset(),
                    investmentValuation.getExternalAsset(), investmentValuation.getSource());
        }
        return valuation;
    }

    private ValuationReportData buildIncomeChild() {
        if (category.getAssetType() != AssetType.MANAGED_PORTFOLIO && category.getAssetType() != AssetType.TAILORED_PORTFOLIO) {
            if (category.getAssetType() != AssetType.TERM_DEPOSIT) {
                return new SimpleValuationReportData("Income accrued", null, category.getIncome(), category.getIncomePercent(),
                        false, null, null);
            }
            else {
                return new SimpleValuationReportData("Interest accrued", null, category.getIncome(), category.getIncomePercent(),
                        false, null, null);
            }
        }
        return null;
    }

    private ValuationReportData buildOutstandingCashChild() {
        if (category.getAssetType() == AssetType.CASH) {
            return new SimpleValuationReportData("Outstanding cash", null, category.getOutstandingCash(),
                    category.getOutstandingCashPercent(), false,
                    null, null);
        }
        return null;
    }


    private void addChild(ValuationReportData data) {
        if (data != null) {
            valuationReportData.add(data);
        }
    }

}
