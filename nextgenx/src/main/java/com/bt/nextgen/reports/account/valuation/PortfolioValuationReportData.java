package com.bt.nextgen.reports.account.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.util.ArrayList;
import java.util.List;

public class PortfolioValuationReportData {

    private ValuationDto valuationDto;

    public PortfolioValuationReportData(ValuationDto valuationDto) {
        this.valuationDto = valuationDto;
    }

    public List<ValuationReportData> getChildren() {
        List<ValuationReportData> valuationCategories = new ArrayList<>();
        for (ValuationSummaryDto category : valuationDto.getCategories()) {
            valuationCategories.add(new ValuationCategoryReportData(category));
        }
        return valuationCategories;
    }

    public String getPortfolioValue() {
        return ReportFormatter.format(ReportFormat.CURRENCY, valuationDto.getBalance());
    }

    public String getSummaryDescription() {
        return "Total portfolio value";
    }
}
