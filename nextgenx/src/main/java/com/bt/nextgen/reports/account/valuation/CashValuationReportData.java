package com.bt.nextgen.reports.account.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;

import java.math.BigDecimal;

public class CashValuationReportData extends SimpleValuationReportData {
    private BigDecimal valueDateBalance;
    private BigDecimal valueDatePercent;

    public CashValuationReportData(CashManagementValuationDto cashValuationDto, String thirdPartySource) {
        super(cashValuationDto.getName(), cashValuationDto.getInterestRate(), cashValuationDto.getBalance(),
                cashValuationDto.getPortfolioPercent(), cashValuationDto.getExternalAsset(), cashValuationDto.getSource(), thirdPartySource);
        this.valueDateBalance = cashValuationDto.getValueDateBalance();
        this.valueDatePercent = cashValuationDto.getValueDatePercent();
    }

    @Override
    public String getBalance() {
        if (!this.getExternalAsset()) {
            return ReportFormatter.format(ReportFormat.CURRENCY, valueDateBalance);
        }
        else {
            return super.getBalance();
        }
    }

    @Override
    public String getAllocationPercent() {
        if (!this.getExternalAsset()) {
            return ReportFormatter.format(ReportFormat.PERCENTAGE, valueDatePercent);
        }
        else {
            return super.getAllocationPercent();

        }
    }
}
