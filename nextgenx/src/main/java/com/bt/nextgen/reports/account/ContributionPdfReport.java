package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.contributioncaps.builder.ContributionReportDtoConverter;
import com.bt.nextgen.api.contributioncaps.model.ContributionReportDto;
import com.bt.nextgen.api.contributioncaps.model.MemberContributionCapValuationDto;
import com.bt.nextgen.api.contributioncaps.service.AccountContributionSummaryDtoService;
import com.bt.nextgen.content.api.model.ContentDto;
import com.bt.nextgen.content.api.model.ContentKey;
import com.bt.nextgen.content.api.service.ContentDtoService;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Class for contribution report pdf generation
 */
@SuppressWarnings({"squid:S1172"})
@Report(value = "categorisedContributionReport", filename = "Contributions report")
public class ContributionPdfReport extends AccountReportV2 {

    private static final String DISCLAIMER_CONTENT = "DS-IP-0071";
    private static final String INFO_CONTENT = "Ins-IP-0287";
    private static final String INFO_CONTENT_NO_CONTRIBUTION_DATA = "Ins-IP-0088";
    private static final String INFO_CONTENT_NO_MEMBER_DATA = "Ins-IP-0321";
    private static final String DATE_PARAM_PATTERN = "[1-9][0-9]{3}-07-01";
    private static final String REPORT_NAME = "Contribution report";
    private static final String URL_PARAM_DATE = "date";
    private static final String FY_LABEL = "FY";
    private static final String ZERO_DOLLARS = "0.00";
    
    private static final String CACHE_KEY = "ContributionPdfReport.contributionsDto";


    @Autowired
    private ContentDtoService contentService;

    @Autowired
    private AccountContributionSummaryDtoService accountContributionSummaryDtoService;



    /**
     * @inheritDoc
     */
    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return Collections.singletonList(getContributionsDto(params, dataCollections));
    }
    /**
     * Calls contribution service and populates Contribution report dto
     * @param params
     * @return
     */
    @ReportBean("contributionsDto")
    public List <ContributionReportDto> getContributionReport(Map<String, Object> params, Map<String, Object> dataCollections) {
        return getContributionsDto(params, dataCollections);
    }

    private List <ContributionReportDto> getContributionsDto(Map<String, Object> params, Map<String, Object> dataCollections) {
        synchronized (dataCollections) {
            List <ContributionReportDto> contributionReportDtos = (List <ContributionReportDto>) dataCollections.get(CACHE_KEY);
            if (contributionReportDtos == null) {
                String accId = (String) params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);
                String date = (String) params.get("date");
                validateDateParam(date);
                if (StringUtils.isEmpty(accId)) {
                    throw new IllegalArgumentException("Account id is not valid");
                }
                List <ApiSearchCriteria> criteria = new ArrayList <ApiSearchCriteria>();
                criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, ApiSearchCriteria.SearchOperation.EQUALS, accId, ApiSearchCriteria.OperationType.STRING));
                criteria.add(new ApiSearchCriteria("date", ApiSearchCriteria.SearchOperation.EQUALS, date, ApiSearchCriteria.OperationType.STRING));
                ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", ApiSearchCriteria.SearchOperation.EQUALS, EncodedString.toPlainText(accId), ApiSearchCriteria.OperationType.STRING);
                ApiSearchCriteria dateCriteria = new ApiSearchCriteria("financialYearDate", ApiSearchCriteria.SearchOperation.EQUALS, date, ApiSearchCriteria.OperationType.STRING);
                List<ApiSearchCriteria> searchCriteriaList = new ArrayList<>();
                searchCriteriaList.add(accountIdCriteria);
                searchCriteriaList.add(dateCriteria);
                ServiceErrors serviceErrors = new FailFastErrorsImpl();
                List<MemberContributionCapValuationDto> memberContributionCapValuationDtos = accountContributionSummaryDtoService.search(searchCriteriaList, serviceErrors);
                contributionReportDtos = new ArrayList<>();
                setContributionDto(memberContributionCapValuationDtos, contributionReportDtos);
                dataCollections.put(CACHE_KEY, contributionReportDtos);
            }
            return contributionReportDtos;
        }
    }

    boolean hasAtLeastOneContribution(List<ContributionReportDto> contributionReportDtos) {
        boolean hasAtLeastOneContribution = false;
        if (contributionReportDtos != null) {
            for (ContributionReportDto contributionReportDto : contributionReportDtos) {
                if (!contributionReportDto.getTotalContributions().equals(ZERO_DOLLARS)) {
                    hasAtLeastOneContribution = true;
                    break;
                }
            }
        }
        return hasAtLeastOneContribution;
    }

    private void validateDateParam(String dateStr) {
        if (!Pattern.compile(DATE_PARAM_PATTERN).matcher(dateStr).matches()) {
            throw new IllegalArgumentException("Invalid date parameter");
        }
    }

    public void setContributionDto(List<MemberContributionCapValuationDto> memberContributionCapValuationDtos, List<ContributionReportDto> contributionReportDtos) {
        int max = 0;
        String contriType = "Contribution type";
        if (CollectionUtils.isNotEmpty(memberContributionCapValuationDtos)){
            for (MemberContributionCapValuationDto memberContributionCapValuationDto: memberContributionCapValuationDtos) {
                ContributionReportDto contributionReportDto = ContributionReportDtoConverter.toContributionReportDto(memberContributionCapValuationDto);
                max = calculateHeaderColumnHeight(contributionReportDto, max);
                contributionReportDtos.add(contributionReportDto);
            }
            if (max==0 && CollectionUtils.isNotEmpty(contributionReportDtos)){
                contributionReportDtos.get(0).setContributionTypeLabel(contriType);
            }
        }
    }

    /**
     * Contribution type label space adjusted for vertical alignment with value column
     * @param contributionReportDto
     * @param max
     * @return
     */
    private int calculateHeaderColumnHeight(ContributionReportDto contributionReportDto, int max)
    {
        String contriType = "Contribution type";
        int nameLength = contributionReportDto.getName().length();
        contributionReportDto.setContributionTypeLabel(contriType);
        int newMax = max;
        if ( nameLength>newMax && nameLength>17){
            newMax = nameLength;
            StringBuilder preContriType = new StringBuilder("");
            StringBuilder postcontriType = new StringBuilder("");
            int halfNameLength = (nameLength)/2;
            String spaces = "&nbsp; ";
            for (int i=0;i<halfNameLength;i++){
                preContriType.append(spaces);
                postcontriType.append(spaces);
            }
            contributionReportDto.setContributionTypeLabel("<pre>"+preContriType.toString()+"<br>"+contriType+"<br>"+postcontriType.toString()+"</pre>");
        }
        return newMax;
    }

    /**
     * Contribution report - header content
     * @param params
     * @return
     */
    @ReportBean("financialYear")
    public String getFinancialYear(Map<String, Object> params)
    {
        String date = (String) params.get("date");
        if (StringUtils.isEmpty(date)) {
            throw new IllegalArgumentException("Date is not valid");
        }
        DateTime fromDateTime = DateTime.parse(date, DateTimeFormat.forPattern("YYYY-MM-DD"));
        return "FY " + fromDateTime.getYear() + " / " + (fromDateTime.getYear()+1);
    }

    @ReportBean("disclaimer")
    public String getDisclaimer()
    {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        ContentKey key = new ContentKey(DISCLAIMER_CONTENT);
        ContentDto content = contentService.find(key, serviceErrors);
        return content!=null ? content.getContent() : "";
    }

    @ReportBean("infoMessage")
    public String getInfoMessage(Map<String, Object> params, Map<String, Object> dataCollections)
    {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        //Account has members with contribution data
        ContentKey key = new ContentKey(INFO_CONTENT);
        List <ContributionReportDto> contributionReportDtos = getContributionsDto(params, dataCollections);
        //Account has no members
        if(contributionReportDtos.isEmpty()){
            key = new ContentKey(INFO_CONTENT_NO_MEMBER_DATA);
        }
        //Account has members and no contribution data
        else if (!hasAtLeastOneContribution(contributionReportDtos)) {
            key = new ContentKey(INFO_CONTENT_NO_CONTRIBUTION_DATA);
        }

        ContentDto content = contentService.find(key, serviceErrors);
        return content!=null ? content.getContent() : "";
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
     * This method returns financial year label depending upon the financial year and current date
     *
     * @param params map of parameters to be passed to jasper report
     * @return financialYearLabel
     */
    @ReportBean("financialYearLabel")
    public String getFinancialYearLabel(Map<String, Object> params) {
        final String financialYearDate = (String) params.get(URL_PARAM_DATE);
        final Integer searchCriteriaDateFinancialYear = Integer
                .valueOf(DateUtil.getFinPeriodStartYear(new DateTime(financialYearDate).toDate()));
        return FY_LABEL + " " + searchCriteriaDateFinancialYear + "/" + (searchCriteriaDateFinancialYear + 1);
    }
}
