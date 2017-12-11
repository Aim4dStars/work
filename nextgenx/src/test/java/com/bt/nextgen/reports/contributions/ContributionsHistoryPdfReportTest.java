package com.bt.nextgen.reports.contributions;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.contributionhistory.model.ContributionByClassification;
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
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.type.DateUtil;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link ContributionHistoryPdfReport}
 * Created by M035995 on 4/08/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContributionsHistoryPdfReportTest {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final String PARAM_DOC_ID = "di";

    private Map<String, Object> paramsMap;

    private Map<String, Object> dataCollections;

    @InjectMocks
    private ContributionHistoryPdfReport contributionHistoryPdfReport;

    @Mock
    private ContributionHistoryDtoService contributionHistoryDtoService;

    @Mock
    private ContributionCapsDtoService contributionCapsDtoService;

    @Mock
    private ContentDtoService contentService;

    @Before
    public void init() {
        paramsMap = new HashMap<>();
        dataCollections = new HashMap<>();
        paramsMap.put("date", getFYFormattedDate(new Date()));
        // By default, get the current FY start date
        paramsMap.put("account-id", "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421");
    }

    @Test
    public void testGetContributionHistoryDetails() {
        when(contributionHistoryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class))).thenReturn(constructContributionHistory());
        ContributionHistoryDto contributionHistoryDto = contributionHistoryPdfReport.getContributionHistoryDetails(paramsMap, dataCollections);
        assertThat("Contribution History is not null", contributionHistoryDto, notNullValue());
        assertThat("Contribution Summary object is not null", contributionHistoryDto.getContributionSummary(), notNullValue());
        assertThat("Total contribution amount is 1000.00", contributionHistoryDto.getContributionSummary().
                getTotalContributions(), Matchers.is(equalTo(new BigDecimal("1000.00"))));

        //Assert Contribution Summary details which were not initially there in the object
        assertThat(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().size(), equalTo(2));
        assertThat(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().get(1).getContributionClassificationLabel(), equalTo("non-concessional"));
        assertThat(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().get(1).getContributionClassification(), equalTo("nconc"));
        assertThat(contributionHistoryDto.getContributionSummary().getContributionSummaryClassifications().get(1).getTotal(), equalTo(BigDecimal.ZERO));

        //Assert one of the Contribution History details
        assertThat(contributionHistoryDto.getContributionByClassifications().size(), equalTo(3));
        assertThat(contributionHistoryDto.getContributionByClassifications().get(0).getContributionTypeLabel(), equalTo("Employer - Award"));
        assertThat(contributionHistoryDto.getContributionByClassifications().get(0).getContributionType(), equalTo("conc"));
        assertThat(contributionHistoryDto.getContributionByClassifications().get(0).getAmount(), equalTo(new BigDecimal("300.00")));
    }


    @Test
    public void testCurrentFinancialYearLabel() {
        // Check the label value for current financial year
        assertThat("Current FY", equalTo(contributionHistoryPdfReport.getFinancialYearLabel(paramsMap)));
    }

    @Test
    public void testForPreviousFinancialYearLabel() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.JULY, 1);
        // This would override the date value in String map
        paramsMap.put("date", getFYFormattedDate(calendar.getTime()));
        assertThat("FY 2014/2015", equalTo(contributionHistoryPdfReport.getFinancialYearLabel(paramsMap)));
    }

    @Test
    public void testPopulateContributionSummaryWhenNoSummary() {
        final ContributionHistoryDto contributionHistoryDto = new ContributionHistoryDto();
        contributionHistoryPdfReport.populateContributionSummary(contributionHistoryDto);

        final ContributionSummary contributionSummary = contributionHistoryDto.getContributionSummary();
        // Validate whether contributionHistoryDto has three contribution details
        assertThat("Contribution Summary is not null", contributionSummary, notNullValue());
        assertThat("Total contribution amount is 0", contributionSummary.getTotalContributions(), Matchers.is(equalTo(BigDecimal.ZERO)));
        // Total summary classifications should be 3 rather than ZERO
        assertThat(contributionSummary.getContributionSummaryClassifications().size(), equalTo(2));
        assertThat(contributionSummary.getContributionSummaryClassifications().get(0).getContributionClassificationLabel(), equalTo("concessional"));
        assertThat(contributionSummary.getContributionSummaryClassifications().get(1).getContributionClassificationLabel(), equalTo("non-concessional"));
    }

    @Test
    public void testPopulateContributionSummaryWhenSummaryAlreadyPopulated() {
        final ContributionHistoryDto contributionHistoryDto = constructContributionHistory();
        contributionHistoryPdfReport.populateContributionSummary(contributionHistoryDto);

        final ContributionSummary contributionSummary = contributionHistoryDto.getContributionSummary();
        // Validate whether contributionHistoryDto has three contribution details
        assertThat("Contribution Summary is not null", contributionSummary, notNullValue());
        assertThat("Total contribution amount is 1000.00", contributionSummary.getTotalContributions(), Matchers.is(equalTo(new BigDecimal("1000.00"))));
        // Total summary classifications should be 3 rather than 1
        assertThat(contributionSummary.getContributionSummaryClassifications().size(), equalTo(2));
        assertThat(contributionSummary.getContributionSummaryClassifications().get(0).getContributionClassificationLabel(), equalTo("concessional"));
        assertThat(contributionSummary.getContributionSummaryClassifications().get(1).getContributionClassificationLabel(), equalTo("non-concessional"));
    }

    private String getFYFormattedDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 7) {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
        } else {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        }
        calendar.set(Calendar.MONTH, Calendar.JULY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return DateUtil.formatDate(calendar.getTime());
    }

    private ContributionHistoryDto constructContributionHistory() {
        final ContributionHistoryDto contributionHistoryDto = new ContributionHistoryDto();
        // Get the local date for
        contributionHistoryDto.setFinancialYearStartDate(dateTimeFormatter.parseLocalDate("2016-07-01"));

        final List<ContributionByClassification> contributionClassificationList = new ArrayList<>();
        contributionClassificationList.add(constructContributionClassification("conc", "Employer - Award", new BigDecimal("300.00")));
        contributionClassificationList.add(constructContributionClassification("conc", "Employer - Super Guarantee (SG)", new BigDecimal("300.00")));
        contributionClassificationList.add(constructContributionClassification("conc", "Employer - Voluntary/Additional", new BigDecimal("400.00")));

        contributionHistoryDto.setContributionSummary(constructContributionSummary());
        contributionHistoryDto.setContributionByClassifications(contributionClassificationList);
        return contributionHistoryDto;
    }

    private ContributionSummary constructContributionSummary() {
        final ContributionSummary contributionSummary = new ContributionSummary();
        contributionSummary.setTotalContributions(new BigDecimal("1000.00"));
        contributionSummary.setLastContributionAmount(new BigDecimal("1000.00"));
        contributionSummary.setLastContributionTime(new DateTime());

        final List<ContributionSummaryClassification> classificationList = new ArrayList<>();
        classificationList.add(constructContributionClassifications(ContributionClassification.CONCESSIONAL, new BigDecimal("1000.00")));

        contributionSummary.setContributionSummaryClassifications(classificationList);
        return contributionSummary;
    }

    private ContributionSummaryClassification constructContributionClassifications(ContributionClassification contributionClassification, BigDecimal total) {
        final ContributionSummaryClassification classification = new ContributionSummaryClassification();
        classification.setTotal(total);
        classification.setContributionClassification(contributionClassification.getAvaloqInternalId());
        classification.setContributionClassificationLabel(contributionClassification.getName());
        return classification;
    }

    private ContributionByClassification constructContributionClassification(String contributionType, String contributionTypeLabel, BigDecimal amount) {
        final ContributionByClassification contributionByClassification = new ContributionByClassification();
        contributionByClassification.setAmount(amount);
        contributionByClassification.setContributionType(contributionType);
        contributionByClassification.setContributionTypeLabel(contributionTypeLabel);
        return contributionByClassification;
    }

    private List<SuperAccountContributionCapsDto> constructContributionCaps() {
        ContributionCapDto dto1 = new ContributionCapDto();
        ContributionCapDto dto2 = new ContributionCapDto();
        dto1.setAmount(new BigDecimal(100));
        dto1.setContributionClassification(ContributionClassification.CONCESSIONAL.getAvaloqInternalId());
        dto1.setContributionClassificationLabel(ContributionClassification.CONCESSIONAL.getName());

        dto2.setAmount(new BigDecimal(50));
        dto2.setContributionClassification(ContributionClassification.NON_CONCESSIONAL.getAvaloqInternalId());
        dto2.setContributionClassificationLabel(ContributionClassification.NON_CONCESSIONAL.getName());
        List<ContributionCapDto> dtoList = new ArrayList<>();
        dtoList.add(dto1);
        dtoList.add(dto2);
        List<SuperAccountContributionCapsDto> capsList = new ArrayList<>();
        SuperAccountContributionCapsDto capsDto = new SuperAccountContributionCapsDto(new AccountKey(EncodedString.toPlainText("676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421")), dateTimeFormatter.parseLocalDate("2016-07-01"), dtoList);
        capsList.add(capsDto);
        return capsList;
    }

    @Test
    public void getHeaderInfoMessage() {
        getHeaderInfoMessage(true, "");
        getHeaderInfoMessage(false, "");
        getHeaderInfoMessage(false, "my info msg");
    }

    @Test
    public void getInfoMessageWhenNoData() {
        getInfoMessageWhenNoData(true, "");
        getInfoMessageWhenNoData(false, "");
        getInfoMessageWhenNoData(false, "my info msg");
    }

    @Test
    public void getReportType() {
        getReportType(null, "Contribution history");
        getReportType("", "Contribution history");
        getReportType("123", "Contribution history");
    }

    @Test
    public void getDisclaimer() {
        getDisclaimer(true, "");
        getDisclaimer(false, "");
        getDisclaimer(false, "my disclaimer text");
    }

    @Test
    public void getContributionsCap() {
        final Map<String, String> params = new HashMap<>();

        // By default, get the current FY start date
        params.put("date", getFYFormattedDate(new Date()));
        params.put("account-id", "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421");

        when(contributionCapsDtoService.search(any(AccountKey.class), Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class))).thenReturn(constructContributionCaps());

        List<BigDecimal> capList = contributionHistoryPdfReport.getContributionsCap(params);
        assertThat("contribution cap list", capList, notNullValue());
        assertThat("capList[0]", capList.get(0), equalTo(new BigDecimal(100)));
        assertThat("capList[1]", capList.get(1), equalTo(new BigDecimal(50)));
    }

    @Test
    public void getSummaryValue() {
        when(contributionHistoryDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class))).thenReturn(constructContributionHistory());

        String value = contributionHistoryPdfReport.getSummaryValue(paramsMap, dataCollections);
        assertThat(value, equalTo("$1,000.00"));
    }

    @Test
    public void getSummaryDescription() {
        final Date today = new Date();

        getSummaryDescription("Current FY summary", DateUtil.formatDate(today),
                "Total contributions - Current FY - As at " + DateUtil.formatDate(today, DateUtil.PERSON_FORMAT));
        getSummaryDescription("FY 2015/2016", "2016-04-12", "Total contributions - FY 2015/2016");
    }

    private void getSummaryDescription(String infoStr, String dateStr, String expectedSummary) {
        paramsMap.put("date", dateStr);

        assertThat(infoStr, contributionHistoryPdfReport.getSummaryDescription(paramsMap, dataCollections),
                equalTo(expectedSummary));
    }

    private void getHeaderInfoMessage(boolean nullContent, String infoMessage) {
        final ContentDto contentDto = nullContent ? null : new ContentDto("", infoMessage);

        reset(contentService);
        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);

        assertThat("infoMessage: nullContent = " + nullContent + "infoMessage = " + infoMessage,
                contributionHistoryPdfReport.getHeaderInfoMessage(null), equalTo(infoMessage));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));
    }

    private void getInfoMessageWhenNoData(boolean nullContent, String infoMessage) {
        final ContentDto contentDto = nullContent ? null : new ContentDto("", infoMessage);

        reset(contentService);
        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);

        assertThat("infoMessage: nullContent = " + nullContent + "infoMessage = " + infoMessage,
                contributionHistoryPdfReport.getInfoMessageWhenNoData(null), equalTo(infoMessage));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));
    }

    private void getDisclaimer(boolean nullContent, String disclaimerText) {
        final ContentDto contentDto = nullContent ? null : new ContentDto("DS-IP-0145", disclaimerText);

        reset(contentService);
        when(contentService.find(any(ContentKey.class), any(ServiceErrors.class))).thenReturn(contentDto);

        assertThat("disclaimerText: nullContent = " + nullContent + "disclaimerText = " + disclaimerText,
                contributionHistoryPdfReport.getDisclaimer(null), equalTo(disclaimerText));
        verify(contentService).find(any(ContentKey.class), any(ServiceErrors.class));
    }

    private void getReportType(String docId, String expectedReportType) {
        final Map<String, Object> params = new HashMap<>();

        params.put(PARAM_DOC_ID, docId);

        assertThat("report name: docId = " + docId, contributionHistoryPdfReport.getReportType(params, null),
                equalTo(expectedReportType));
    }
}
