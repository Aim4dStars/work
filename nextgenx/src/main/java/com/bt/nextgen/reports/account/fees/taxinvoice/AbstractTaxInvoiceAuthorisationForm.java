package com.bt.nextgen.reports.account.fees.taxinvoice;

import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import org.joda.time.DateTime;

import java.util.Map;

public abstract class AbstractTaxInvoiceAuthorisationForm extends AccountReportV2 {

    protected static final String ACCOUNT_ID = "account-id";
    protected static final String MONTH = "month";
    protected static final String YEAR = "year";

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Tax invoice- advice fees";
    }

    @ReportBean("dateGenerated")
    public String getDateGenerated() {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, DateTime.now());
    }
}