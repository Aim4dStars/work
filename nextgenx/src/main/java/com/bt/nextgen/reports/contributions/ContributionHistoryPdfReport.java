package com.bt.nextgen.reports.contributions;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummary;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummaryClassification;
import com.bt.nextgen.api.contributionhistory.service.ContributionHistoryDtoService;
import com.bt.nextgen.api.superannuation.caps.model.ContributionCapDto;
import com.bt.nextgen.api.superannuation.caps.model.SuperAccountContributionCapsDto;
import com.bt.nextgen.api.superannuation.caps.service.ContributionCapsDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.ReportFormat;
import com.bt.nextgen.core.reporting.ReportFormatter;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectUnique;

/**
 * This class defines the parameters for Contributions History report.
 * Created by M035995 on 22/07/2016.
 */
@Report("contributionHistoryPdfReport")
public class ContributionHistoryPdfReport extends AccountReportV2 {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0145";

    private static final String INFO_MESSAGE_WHEN_NO_DATA = "Ins-IP-0141";

    private static final String HEADER_INFO_MESSAGE = "Ins-IP-0285";

    private static final String REPORT_NAME = "Contribution history";

    private static final String URL_PARAM_DATE = "date";

    private static final String CURRENT_FY_LABEL = "Current FY";

    private static final String FY_LABEL = "FY";

    private static final String TOTAL_CONTRIBUTIONS = "Total contributions";

    private static final String CONCESSIONAL = "conc";

    private static final String NON_CONCESSIONAL = "nconc";

    private static final String CACHE_KEY = "ContributionHistoryPdfReport.contributionHistory";

    /**
     * DTO Service for retrieving contribution history.
     */
    @Autowired
    private ContributionHistoryDtoService contributionHistoryDtoService;

    /**
     * DTO Service for retrieving contribution caps.
     */
    @Autowired
    private ContributionCapsDtoService contributionCapsDtoService;

    /**
     * DTO Service for getting content information
     */
    @Autowired
    private ContentDtoService contentService;

    /**
     * This method returns an object of type {@link com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto}
     *
     * @param params map of parameters to be passed to jasper report
     * @return object of type {@link com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto}
     */
    @ReportBean("contributionHistoryDto")
    public ContributionHistoryDto getContributionHistoryDetails(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getContributionHistoryDto(params, dataCollections);
    }

    /**
     * This method returns a {@link java.util.List} of concessional and non-concessional cap values
     *
     * @param params map of parameters to be passed to jasper report
     * @return {@link java.util.List} of con/non-conc cap values
     */
    @ReportBean("contributionsCap")
    public List<BigDecimal> getContributionsCap(Map<String, String> params) {

        final List<BigDecimal> contributionCapsList = new ArrayList<>();
        final List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();

        // Get the params from the URL and populate SearchCriteria
        final String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
        final String financialYearStartDate = params.get(URL_PARAM_DATE);
        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS,
                accountId, ApiSearchCriteria.OperationType.STRING);
        final ApiSearchCriteria dateCriteria = new ApiSearchCriteria("date", ApiSearchCriteria.SearchOperation.EQUALS,
                financialYearStartDate, ApiSearchCriteria.OperationType.STRING);
        searchCriteriaList.add(accountIdCriteria);
        searchCriteriaList.add(dateCriteria);

        final List<SuperAccountContributionCapsDto> superAccountContributionCapsDtoList = contributionCapsDtoService.search(
                new AccountKey(EncodedString.toPlainText(accountId)), searchCriteriaList, new ServiceErrorsImpl());

        final List<ContributionCapDto> contributionCapDtoList = superAccountContributionCapsDtoList.get(0).getContributionCaps();
        // First item in the list is for concessional cap and the second for non-concessional cap
        for (ContributionCapDto contributionCap : contributionCapDtoList) {
            if (CONCESSIONAL.equalsIgnoreCase(contributionCap.getContributionClassification()))
                contributionCapsList.add(0, contributionCap.getAmount());
            if (NON_CONCESSIONAL.equalsIgnoreCase(contributionCap.getContributionClassification()))
                contributionCapsList.add(1, contributionCap.getAmount());
        }

        return contributionCapsList;
    }

    /**
     * This method returns financial year label depending upon the financial year and current date
     *
     * @param params map of parameters to be passed to jasper report
     * @return financialYearLabel
     */
    @ReportBean("financialYearLabel")
    public String getFinancialYearLabel(Map<String, Object> params) {
        final String financialYearDate = (String) params.get(URL_PARAM_DATE);

        final Integer currentFinancialYear = Integer.valueOf(DateUtil.getFinPeriodStartYear(new Date()));
        final Integer searchCriteriaDateFinancialYear =
                Integer.valueOf(DateUtil.getFinPeriodStartYear(new DateTime(financialYearDate).toDate()));
        if (currentFinancialYear.compareTo(searchCriteriaDateFinancialYear) == 0)
            return CURRENT_FY_LABEL;
        else
            return FY_LABEL + " " + searchCriteriaDateFinancialYear + "/" + (searchCriteriaDateFinancialYear + 1);
    }


    /**
     * @inheritDoc
     */
    @Override
    @ReportBean("reportType")
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return REPORT_NAME;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSummaryDescription(Map<String, Object> params, Map<String, Object> dataCollections) {
        final String financialYearLabel = getFinancialYearLabel(params);
        if (CURRENT_FY_LABEL.equals(financialYearLabel))
            return TOTAL_CONTRIBUTIONS + " - " + financialYearLabel + " - As at " +
                    ReportFormatter.format(ReportFormat.SHORT_DATE, new DateTime());
        else
            return TOTAL_CONTRIBUTIONS + " - " + financialYearLabel;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getSummaryValue(Map<String, Object> params, Map<String, Object> dataCollections) {
        final ContributionHistoryDto contributionHistoryDto = getContributionHistoryDto(params, dataCollections);
        return ReportFormatter.format(ReportFormat.CURRENCY,
                contributionHistoryDto.getContributionSummary() != null ?
                        contributionHistoryDto.getContributionSummary().getTotalContributions() : BigDecimal.ZERO);
    }

    /**
     * This method retrieves the disclaimer text for the content id.
     * Sonar warning represents the issue with params not being used in the method.
     * @param params map of parameters to be passed to jasper report
     * @return disclaimer text
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("disclaimer")
    public String getDisclaimer(Map<String, String> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }

    /**
     * This method retrieves the disclaimer text for the content id.
     * Sonar warning represents the issue with params not being used in the method.
     *
     * @param params map of parameters to be passed to jasper report
     * @return disclaimer text
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("infoMessage")
    public String getInfoMessageWhenNoData(Map<String, String> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(INFO_MESSAGE_WHEN_NO_DATA);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }


    /**
     * This method retrieves the Header Info message.
     * Sonar warning represents the issue with params not being used in the method.
     *
     * @param params map of parameters to be passed to jasper report
     * @return disclaimer text
     */
    @SuppressWarnings("squid:S1172")
    @ReportBean("headerInfoMessage")
    public String getHeaderInfoMessage(Map<String, String> params) {
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final ContentKey key = new ContentKey(HEADER_INFO_MESSAGE);
        final ContentDto content = contentService.find(key, serviceErrors);
        return content != null ? content.getContent() : "";
    }

    /**
     * This method sets the ContributionHistoryDto object into dataCollections and retrieves it from collection itself, if needed again
     *
     * @param params          map of parameters being sent to jasper
     * @param dataCollections dataCollection
     * @return object of {@link ContributionHistoryDto}
     */
    private ContributionHistoryDto getContributionHistoryDto(Map<String, Object> params, Map<String, Object> dataCollections) {
        synchronized (dataCollections) {
            ContributionHistoryDto contributionHistoryDto = (ContributionHistoryDto) dataCollections.get(CACHE_KEY);
            if (contributionHistoryDto == null) {
                // Get params from URL and populate the search criteria
                final String accountId = (String) params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
                final String financialYearStartDate = (String) params.get(URL_PARAM_DATE);
                final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId",
                        ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accountId),
                        ApiSearchCriteria.OperationType.STRING);
                final ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate",
                        ApiSearchCriteria.SearchOperation.EQUALS, financialYearStartDate,
                        ApiSearchCriteria.OperationType.STRING);

                final List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();

                searchCriteriaList.add(accountIdCriteria);
                searchCriteriaList.add(dateCriteria);
                contributionHistoryDto = contributionHistoryDtoService.search(searchCriteriaList, new ServiceErrorsImpl());
                // Check if all contributions are available; if not, add the default value in the list for various Contribution types
                populateContributionSummary(contributionHistoryDto);
                dataCollections.put(CACHE_KEY, contributionHistoryDto);
            }
            return contributionHistoryDto;
        }
    }


    /**
     * This method would populate Contribution Summary object in case value is not present for
     * any contribution type
     *
     * @param contributionHistoryDto object of {@link com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto}
     */
    protected void populateContributionSummary(ContributionHistoryDto contributionHistoryDto) {
        ContributionSummary contributionSummary = contributionHistoryDto.getContributionSummary();

        // ContributionSummary object would be null in case there are no contributions for the client
        // In this case, initialize the list and populate all three types.
        final List<ContributionSummaryClassification> contributionSummaryClassificationList =
                contributionSummary != null ? contributionSummary.getContributionSummaryClassifications() :
                        new ArrayList<ContributionSummaryClassification>();

        // If concessional is not available for the client, set the default value in the list
        ContributionSummaryClassification classification = selectUnique(contributionSummaryClassificationList,
                having(on(ContributionSummaryClassification.class).getContributionClassificationLabel(),
                        org.hamcrest.Matchers.equalTo("concessional")));
        if (classification == null) {
            contributionSummaryClassificationList.add(setContributionClassfication(ContributionClassification.CONCESSIONAL));
        }
        else if (classification.getTotal() == null) {
            classification.setTotal(BigDecimal.ZERO);
        }

        // If 'non-concessional' is not available for the client, set the default value in the list
        classification = selectUnique(contributionSummaryClassificationList,
                having(on(ContributionSummaryClassification.class).getContributionClassificationLabel(),
                        org.hamcrest.Matchers.equalTo("non-concessional")));
        if (classification == null)
            contributionSummaryClassificationList.add(setContributionClassfication(ContributionClassification.NON_CONCESSIONAL));

        // Finally, if the contribution summary object was null; define it so that we can show the default rows on the screen.
        if (contributionSummary == null) {
            contributionSummary = new ContributionSummary();
            contributionSummary.setTotalContributions(BigDecimal.ZERO);
        }

        contributionSummary.
                setContributionSummaryClassifications(contributionSummaryClassificationList);

        // Set all modifications back to contributionHistoryDto
        contributionHistoryDto.setContributionSummary(contributionSummary);
    }

    /**
     * This method would return {@link com.bt.nextgen.api.contributionhistory.model.ContributionSummaryClassification} object for {@link com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification} type.
     *
     * @param contributionClassification {@link com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification} enum
     * @return object of {@link com.bt.nextgen.api.contributionhistory.model.ContributionSummaryClassification}
     */
    private ContributionSummaryClassification setContributionClassfication(ContributionClassification contributionClassification) {
        final ContributionSummaryClassification classification = new ContributionSummaryClassification();
        classification.setContributionClassification(contributionClassification.getAvaloqInternalId());
        classification.setContributionClassificationLabel(contributionClassification.getName());
        classification.setTotal(BigDecimal.ZERO);
        return classification;
    }

    @Override
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#root.this.getAccountEncodedId(#params), 'account.super.contribution.view')")
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return super.getData(params, dataCollections);
    }
}
