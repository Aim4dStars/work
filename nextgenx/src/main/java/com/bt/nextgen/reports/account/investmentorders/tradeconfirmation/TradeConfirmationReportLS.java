package com.bt.nextgen.reports.account.investmentorders.tradeconfirmation;

import com.bt.nextgen.core.reporting.stereotype.Report;

@Report("tradeConfirmationReportLS")
public class TradeConfirmationReportLS extends TradeConfirmationReport {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0077";

    @Override
    public String getDisclaimerContent() {
        return DISCLAIMER_CONTENT;
    }
}
