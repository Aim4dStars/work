package com.bt.nextgen.reports.account.movements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.movement.GrowthItemDto;
import com.bt.nextgen.api.portfolio.v3.model.movement.ValuationMovementDto;
import com.bt.nextgen.api.portfolio.v3.service.ValuationMovementDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.reports.account.common.AbstractAccountValuationReport;
import com.bt.nextgen.reports.account.common.AccountValuationReportData;
import com.bt.nextgen.reports.account.common.SummaryReportData;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;

@Report(value = "portfolioMovementReportV2", filename = "Portfolio movements")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PortfolioMovementReport extends AbstractAccountValuationReport {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private ValuationMovementDtoService valuationMovementDtoService;

    @Autowired
    private ContentDtoService contentService;

    private static final String REPORT_NAME = "Portfolio movements";
    private static final String DISCLAIMER_KEY = "DS-IP-0075";
    private static final String START_DATE = "start-date";
    private static final String END_DATE = "end-date";
    private static final String ACCOUNT_ID = "account-id";

    private ValuationMovementDto valuationMovementDto;

    @ReportBean("startDate")
    public String getStartDate(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(params.get(START_DATE)));
    }

    @ReportBean("endDate")
    public String getEndDate(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(params.get(END_DATE)));
    }

    @ReportBean("reportOpeningSummary")
    public SummaryReportData getReportOpeningSummary(Map<String, Object> params) {
        String summaryText = "Opening portfolio value " + getStartDate(params);
        String summaryBalance = getOpeningBalance(params);
        return new SummaryReportData(summaryText, summaryBalance, null, false);
    }

    private String getOpeningBalance(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.CURRENCY, valuationMovementDto.getOpeningBalance());
    }

    @ReportBean("reportClosingSummary")
    public SummaryReportData getReportClosingSummary(Map<String, Object> params) {
        String summaryText = "Closing portfolio value " + getEndDate(params);
        String summaryBalance = getClosingBalance(params);
        return new SummaryReportData(summaryText, summaryBalance, null, false);
    }

    private String getClosingBalance(Map<String, Object> params) {
        return ReportFormatter.format(ReportFormat.CURRENCY, valuationMovementDto.getClosingBalance());
    }

    @ReportBean("disclaimer")
    public String getDisclaimer() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_KEY);
        ContentDto content = contentService.find(key, serviceErrors);
        return content.getContent();
    }

    private List<GrowthItemReportData> getGrowthItems(ValuationMovementDto data) {
        List<GrowthItemReportData> growthItemReportDtos = null;
        growthItemReportDtos = getNestedGrowthItems(data.getGrowthItems());
        return growthItemReportDtos;

    }

    private List<GrowthItemReportData> getNestedGrowthItems(List<GrowthItemDto> growthItems) {
        List<GrowthItemReportData> growthItemReportDtos = new ArrayList<>();
        List<GrowthItemReportData> nestedGrowthItemReportDtos = new ArrayList<>();
        GrowthItemReportData growthItemReportDto = null;
        for (GrowthItemDto growthItem : growthItems) {
            if (growthItem.getGrowthItems() != null && !growthItem.getGrowthItems().isEmpty()) {
                nestedGrowthItemReportDtos = getNestedGrowthItems(growthItem.getGrowthItems());
                growthItemReportDto = new GrowthItemReportData(growthItem.getBalance(), growthItem.getDisplayName(), true,
                        nestedGrowthItemReportDtos);
            } else {
                growthItemReportDto = new GrowthItemReportData(growthItem.getBalance(), growthItem.getDisplayName(), true, null);
            }
            growthItemReportDtos.add(growthItemReportDto);
        }
        return growthItemReportDtos;
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        DateRangeAccountKey dateRangeAccountKey = new DateRangeAccountKey(accountId, new DateTime(params.get(START_DATE)), new DateTime(
                params.get(END_DATE)));
        valuationMovementDto = valuationMovementDtoService.find(dateRangeAccountKey, new FailFastErrorsImpl());
        List<GrowthItemReportData> result = new ArrayList<GrowthItemReportData>();
        List<GrowthItemReportData> growthItemReportDtos = getGrowthItems(valuationMovementDto);
        result.add(new GrowthItemReportData(null, null, null, growthItemReportDtos));
        return result;
    }

    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        String endDate = ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime(params.get(END_DATE)));
        return "Closing portfolio value " + endDate;
    }

    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        AccountValuationReportData valuation = getAccountValuation(params, dataCollections);
        return valuation.getTotalBalance();
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    @Override
    public DateTime getEffectiveDate(Map<String, Object> params, Map<String, Object> dataCollections) {
        return new DateTime(params.get(END_DATE));
    }
}