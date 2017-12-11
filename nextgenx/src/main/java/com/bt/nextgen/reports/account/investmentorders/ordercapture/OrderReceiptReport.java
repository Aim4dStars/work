package com.bt.nextgen.reports.account.investmentorders.ordercapture;

import java.util.Map;

import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;

@Report("orderReceiptReport")
public class OrderReceiptReport extends AbstractOrderReportV2 {

    private static final String REPORT_TITLE = "Orders successfully submitted";
    private static final String DISCLAIMER_CONTENT = "DS-IP-0007";
    private static final String FEE_DISCLAIMER_RECEIPT = "DS-IP-0087";

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        return getContent(DISCLAIMER_CONTENT);
    }

    @ReportBean("title")
    public String getReportTitle() {
        return REPORT_TITLE;
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }

    @ReportBean("reportFileName")
    public String getReportFileName(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountKey accountKey = getAccountKey(params);
        WrapAccountDetail account = getAccount(accountKey, dataCollections, getServiceType(params));
        StringBuilder filename = new StringBuilder(account.getAccountNumber());
        filename.append(" - ");
        filename.append("Order Receipt");
        return filename.toString();
    }

    @ReportBean("feeDisclaimer")
    public String getFeeDisclaimer() {
        return getContent(FEE_DISCLAIMER_RECEIPT);
    }
}
