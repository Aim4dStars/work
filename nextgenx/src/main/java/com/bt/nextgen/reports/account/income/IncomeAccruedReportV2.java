package com.bt.nextgen.reports.account.income;

import java.util.Map;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.bt.nextgen.api.income.v2.model.IncomeDetailsKey;
import com.bt.nextgen.api.income.v2.model.IncomeDetailsType;
import com.bt.nextgen.api.income.v2.model.IncomeValuesDto;
import com.bt.nextgen.api.income.v2.service.IncomeDetailsDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;

@Report(value = "incomeAccruedReportV2", filename = "Income accrued")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IncomeAccruedReportV2 extends AbstractIncomeReportPdfV2 {
    public static final String ACCOUNT_ID_URI_MAPPING = "account-id";
    public static final String START_DATE_PARAMETER_MAPPING = "start-date";
    public static final String END_DATE_PARAMETER_MAPPING = "end-date";

    private static final String DISCLAIMER_CONTENT = "DS-IP-0004";
    private static final String PARAM_INCOME_TYPE = "income-type";
    private static final String REPORT_TITLE = "Income accrued";
    private static final String SUMMARY_DESCRIPTION = "Total income accrued";
    private static final String REPORT_SUB_TITLE = "As at %s";
    private static final String REPORT_TYPE = "accrued";

    @Autowired
    private IncomeDetailsDtoService incomeDtoService;

    @Autowired
    private ContentDtoService contentService;

    private IncomeValuesDto incomeValuesDto;

    @ReportBean("reportTitle")
    public String getReportTitle() {
        return REPORT_TITLE;
    }

    @ReportBean("reportSubTitle")
    public String getReportSubTitle(Map<String, Object> params) {
        String startDate = getStartDate(params);
        return String.format(REPORT_SUB_TITLE, startDate);
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    @ReportBean("startDate")
    public String getStartDate(Map<String, Object> params) {
        DateTime startDate = new DateTime(params.get(START_DATE_PARAMETER_MAPPING));
        return ReportFormatter.format(ReportFormat.SHORT_DATE, startDate);
    }

    @ReportBean("endDate")
    public String getEndDate(Map<String, Object> params) {
        DateTime endDate = new DateTime(params.get(END_DATE_PARAMETER_MAPPING));
        return ReportFormatter.format(ReportFormat.SHORT_DATE, endDate);
    }

    @Override
    protected IncomeReportData getIncomeData(Map<String, Object> params) {
        String accountId = (String) params.get(ACCOUNT_ID_URI_MAPPING);
        DateTime startDate = new DateTime(params.get(START_DATE_PARAMETER_MAPPING));
        DateTime endDate = new DateTime(params.get(END_DATE_PARAMETER_MAPPING));
        IncomeDetailsType incomeType = IncomeDetailsType.valueOf((String) params.get(PARAM_INCOME_TYPE));
        IncomeDetailsKey key = new IncomeDetailsKey(accountId, incomeType, startDate, endDate);
        incomeValuesDto = incomeDtoService.find(key, new FailFastErrorsImpl());

        IncomeValueReportData incomeValueReportData = new IncomeValueReportData(incomeValuesDto, null, null, REPORT_TYPE);
        IncomeSummaryReportData incomeSummaryData = new IncomeSummaryReportData(incomeValuesDto.getIncomeValueTotals());
        IncomeReportData incomeReportData = new IncomeReportData(incomeValueReportData, incomeSummaryData);

        return incomeReportData;
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        return SUMMARY_DESCRIPTION;
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        return ReportFormatter.format(ReportFormat.CURRENCY, incomeValuesDto.getIncomeValueTotals().getIncomeTotal());
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_TITLE;
    }
}
