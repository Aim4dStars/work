package com.bt.nextgen.reports.account.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;

public class ShareValuationReportData extends InvestmentValuationReportData {

    private String hinType;

    public ShareValuationReportData(ShareValuationDto shareValuationDto) {
        super(shareValuationDto.getInvestmentAsset(), shareValuationDto.getExternalAsset(), shareValuationDto.getSource());
        this.hinType = shareValuationDto.getHinType().name();
    }

    public String getHinType() {
        return this.hinType;
    }
}
