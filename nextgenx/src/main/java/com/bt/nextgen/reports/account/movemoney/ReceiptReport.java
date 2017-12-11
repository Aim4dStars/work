package com.bt.nextgen.reports.account.movemoney;

import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class ReceiptReport extends AccountReportV2 {

    private static final String REPORT_NAME_ONE_OFF = "One-off contribution";
    private static final String REPORT_NAME_REGULAR = "Regular contribution";

    /**
     * @inheritDoc
     */
    @Override
    @ReportBean("reportType")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        String reportType = (String) params.get("report-type");
        if (StringUtils.isNotEmpty(reportType) && "regular".equals(reportType)) {
            return REPORT_NAME_REGULAR;
        } else {
            return REPORT_NAME_ONE_OFF;
        }
    }

    /**
     * This method returns payment due label depending upon the report type
     *
     * @param params
     *            map of parameters to be passed to jasper report
     * @return paymentDueLabel
     */
    @ReportBean("paymentDueLabel")
    public String getPaymentDueLabel(Map<String, Object> params) {
        String reportType = (String) params.get("report-type");
        if (StringUtils.isNotEmpty(reportType) && "regular".equals(reportType)) {
            return "Regular payment due";
        } else {
            return "Payment due";
        }
    }
}
