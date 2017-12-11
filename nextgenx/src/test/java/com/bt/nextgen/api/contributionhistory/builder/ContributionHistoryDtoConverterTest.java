package com.bt.nextgen.api.contributionhistory.builder;

import com.bt.nextgen.api.contributionhistory.model.ContributionByClassification;
import com.bt.nextgen.api.contributionhistory.model.ContributionHistoryDto;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummary;
import com.bt.nextgen.api.contributionhistory.model.ContributionSummaryClassification;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionSummaryByType;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionType;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.Code;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification.CONCESSIONAL;
import static com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification.NON_CONCESSIONAL;
import static com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification.OTHER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by M022641 on 13/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContributionHistoryDtoConverterTest {
    private static final ContributionType CONTRIBUTION_TYPE_1 = new ContributionType("Type_id_1", "Type label 1");
    private static final ContributionType CONTRIBUTION_TYPE_2 = new ContributionType("Type_id_2", "Type label 2");
    private static final ContributionType CONTRIBUTION_TYPE_3 = new ContributionType("Type_id_3", "Type label 3");
    private static final ContributionType CONTRIBUTION_TYPE_4 = new ContributionType("Type_id_4", "Type label 4");

    private class MockContributionSummaryByType implements ContributionSummaryByType {
        Long docId;
        ContributionClassification contributionClassification;
        ContributionType contributionType;
        DateTime lastContributionDate;
        BigDecimal amount;

        @Override
        public Long getDocId() {
            return docId;
        }

        @Override
        public ContributionClassification getContributionClassification() {
            return contributionClassification;
        }

        @Override
        public ContributionType getContributionType() {
            return contributionType;
        }

        @Override
        public DateTime getLastContributionDate() {
            return lastContributionDate;
        }

        @Override
        public BigDecimal getAmount() {
            return amount;
        }
    }


    private class MockContributionHistory implements ContributionHistory {
        DateTime financialYearStartDate;
        List<ContributionSummaryByType> contributionSummariesByType;
        BigDecimal maxAmount;

        @Override
        public DateTime getFinancialYearStartDate() {
            return financialYearStartDate;
        }

        @Override
        public BigDecimal getMaxAmount() {
            return maxAmount;
        }

        @Override
        public List<ContributionSummaryByType> getContributionSummariesByType() {
            return contributionSummariesByType;
        }
    }


    @Test
    public void getAsContributionHistoryDtoForOneContribution() {
        final DateTime financialYearStartDate = new DateTime("2015-07-01T00:00:00+10:00");
        final ContributionHistoryDtoConverter dtoConverter = new ContributionHistoryDtoConverter();
        final ContributionHistory contributionHistory;
        final Collection<Code> categoryCodes = new ArrayList<>();
        ContributionHistoryDto dto;
        ContributionSummary summary;
        ContributionByClassification cbc;
        ContributionSummaryClassification summaryClassification;
        int index;
        String infoStr;

        contributionHistory = makeContributionHistory(financialYearStartDate,
                makeSummaryByType(111L, NON_CONCESSIONAL, CONTRIBUTION_TYPE_1,
                        new DateTime("2015-12-09T00:00:00+10:00"), new BigDecimal("20.023")));

        Code code1 = new CodeImpl("1", "EMPLOYER", "Employer - Super Guarantee (SG)", "employer");
        Code code2 = new CodeImpl("4", "SACRIFICE", "Employer - Salary Sacrifice", "sacrifice");
        categoryCodes.add(code1);
        categoryCodes.add(code2);

        dto = dtoConverter.getAsContributionHistoryDto(contributionHistory,categoryCodes);
        assertThat("financial year start date", dto.getFinancialYearStartDate(), nullValue());
        assertThat("number of contribution classifications", dto.getContributionByClassifications().size(), equalTo(1));

        // ContributionByClassifications
        index = 0;
        infoStr = "contributionByClassifications[" + index + "] -";
        cbc = dto.getContributionByClassifications().get(index);
        assertThat(infoStr + " contribution type", cbc.getContributionType(), equalTo(CONTRIBUTION_TYPE_1.getId()));
        assertThat(infoStr + " contribution label", cbc.getContributionTypeLabel(),
                equalTo(CONTRIBUTION_TYPE_1.getLabel()));
        assertThat(infoStr + " amount", cbc.getAmount(), equalTo(new BigDecimal("20.02")));


        // summary
        summary = dto.getContributionSummary();
        assertThat("summary - lastContributionTime", summary.getLastContributionTime(),
                equalTo(new DateTime("2015-12-09T00:00:00+10:00")));
        assertThat("summary - lastContributionAmount", summary.getLastContributionAmount(),
                equalTo(new BigDecimal("20.02")));
        assertThat("summary - totalContributions", summary.getTotalContributions(),
                equalTo(new BigDecimal("20.02")));
        assertThat("summary - number of classifications",
                summary.getContributionSummaryClassifications().size(), equalTo(3));
        assertThat("summary - last contribution type",
                summary.getLastContributionType(), equalTo(CONTRIBUTION_TYPE_1.getLabel()));

        // ContributionSummaryClassifications
        index = 0;
        infoStr = "contributionSummaryClassifications[" + index + "] -";
        summaryClassification = summary.getContributionSummaryClassifications().get(index);
        assertThat(infoStr + " contributionClassification",
                summaryClassification.getContributionClassification(), equalTo(CONCESSIONAL.getAvaloqInternalId()));
        assertThat(infoStr + " contributionClassificationLabel",
                summaryClassification.getContributionClassificationLabel(), equalTo(CONCESSIONAL.getName()));
        assertThat(infoStr + " total", summaryClassification.getTotal(), nullValue());
        assertThat(infoStr + " availableBalance", summaryClassification.getAvailableBalance(), nullValue());

        index = 1;
        infoStr = "contributionSummaryClassifications[" + index + "] -";
        summaryClassification = summary.getContributionSummaryClassifications().get(index);
        assertThat(infoStr + " contributionClassification",
                summaryClassification.getContributionClassification(), equalTo(NON_CONCESSIONAL.getAvaloqInternalId()));
        assertThat(infoStr + " contributionClassificationLabel",
                summaryClassification.getContributionClassificationLabel(), equalTo(NON_CONCESSIONAL.getName()));
        assertThat(infoStr + " total", summaryClassification.getTotal(), equalTo(new BigDecimal("20.02")));
        assertThat(infoStr + " availableBalance", summaryClassification.getAvailableBalance(), nullValue());

        index = 2;
        infoStr = "contributionSummaryClassifications[" + index + "] -";
        summaryClassification = summary.getContributionSummaryClassifications().get(index);
        assertThat(infoStr + "contributionClassification",
                summaryClassification.getContributionClassification(), equalTo(OTHER.getAvaloqInternalId()));
        assertThat(infoStr + "contributionClassificationLabel",
                summaryClassification.getContributionClassificationLabel(), equalTo(OTHER.getName()));
        assertThat(infoStr + "total", summaryClassification.getTotal(), nullValue());
        assertThat(infoStr + "total", summaryClassification.getAvailableBalance(), nullValue());
    }


    @Test
    public void getAsContributionHistoryDtoForMultipleContributions() {
        final DateTime financialYearStartDate = new DateTime("2015-07-01T00:00:00+10:00");
        final ContributionHistoryDtoConverter dtoConverter = new ContributionHistoryDtoConverter();
        final ContributionHistory contributionHistory;
        ContributionHistoryDto dto;
        final Collection<Code> categoryCodes = new ArrayList<>();
        ContributionSummary summary;
        ContributionByClassification cbc;
        ContributionSummaryClassification summaryClassification;
        int index;
        String infoStr;


        Code code1 = new CodeImpl("1", "EMPLOYER", "Employer - Super Guarantee (SG)", "employer");
        Code code2 = new CodeImpl("4", "SACRIFICE", "Employer - Salary Sacrifice", "sacrifice");
        categoryCodes.add(code1);
        categoryCodes.add(code2);

        contributionHistory = makeContributionHistory(financialYearStartDate,
                makeSummaryByType(111L, NON_CONCESSIONAL, CONTRIBUTION_TYPE_2,
                        new DateTime("2016-05-07T00:00:00+10:00"), new BigDecimal("20.023")),
                // contributions made within the same transaction (same docId) summed up in lastContributionAmount
                makeSummaryByType(557L, CONCESSIONAL, CONTRIBUTION_TYPE_2,
                        new DateTime("2016-06-02T00:00:00+10:00"), new BigDecimal("70.07")),
                makeSummaryByType(557L, CONCESSIONAL, CONTRIBUTION_TYPE_1,
                        new DateTime("2016-06-02T00:00:00+10:00"), new BigDecimal("50.05")),
                makeSummaryByType(555L, CONCESSIONAL, CONTRIBUTION_TYPE_1,
                        new DateTime("2016-05-07T00:00:00+10:00"), new BigDecimal("10.01")),
                // contributions made within the same transaction (same docId)
                makeSummaryByType(333L, CONCESSIONAL, CONTRIBUTION_TYPE_3,
                        new DateTime("2016-04-01T00:00:00+10:00"), new BigDecimal("30.03")),
                makeSummaryByType(333L, CONCESSIONAL, CONTRIBUTION_TYPE_2,
                        new DateTime("2016-04-01T00:00:00+10:00"), new BigDecimal("40.04")),
                makeSummaryByType(222L, OTHER, CONTRIBUTION_TYPE_4,
                        new DateTime("2016-05-02T00:00:00+10:00"), new BigDecimal("60.06")));

        dto = dtoConverter.getAsContributionHistoryDto(contributionHistory, categoryCodes);
        assertThat("financial year start date", dto.getFinancialYearStartDate(), nullValue());
        assertThat("number of contribution classifications", dto.getContributionByClassifications().size(), equalTo(4));

        // ContributionByClassifications
        index = 0;
        infoStr = "contributionByClassifications[" + index + "] -";
        cbc = dto.getContributionByClassifications().get(index);
        assertThat(infoStr + " contribution type", cbc.getContributionType(), equalTo(CONTRIBUTION_TYPE_1.getId()));
        assertThat(infoStr + " contribution label", cbc.getContributionTypeLabel(),
                equalTo(CONTRIBUTION_TYPE_1.getLabel()));
        assertThat(infoStr + " amount", cbc.getAmount(), equalTo(new BigDecimal("60.06")));

        index = 1;
        infoStr = "contributionByClassifications[" + index + "] -";
        cbc = dto.getContributionByClassifications().get(index);
        assertThat(infoStr + " contribution type", cbc.getContributionType(), equalTo(CONTRIBUTION_TYPE_2.getId()));
        assertThat(infoStr + " contribution label", cbc.getContributionTypeLabel(),
                equalTo(CONTRIBUTION_TYPE_2.getLabel()));
        assertThat(infoStr + " amount", cbc.getAmount(), equalTo(new BigDecimal("130.13")));

        index = 2;
        infoStr = "contributionByClassifications[" + index + "] -";
        cbc = dto.getContributionByClassifications().get(index);
        assertThat(infoStr + " contribution type", cbc.getContributionType(), equalTo(CONTRIBUTION_TYPE_3.getId()));
        assertThat(infoStr + " contribution label", cbc.getContributionTypeLabel(),
                equalTo(CONTRIBUTION_TYPE_3.getLabel()));
        assertThat(infoStr + " amount", cbc.getAmount(), equalTo(new BigDecimal("30.03")));

        index = 3;
        infoStr = "contributionByClassifications[" + index + "] -";
        cbc = dto.getContributionByClassifications().get(index);
        assertThat(infoStr + " contribution type", cbc.getContributionType(), equalTo(CONTRIBUTION_TYPE_4.getId()));
        assertThat(infoStr + " contribution label", cbc.getContributionTypeLabel(),
                equalTo(CONTRIBUTION_TYPE_4.getLabel()));
        assertThat(infoStr + " amount", cbc.getAmount(), equalTo(new BigDecimal("60.06")));


        // summary
        summary = dto.getContributionSummary();
        assertThat("summary - lastContributionTime", summary.getLastContributionTime(),
                equalTo(new DateTime("2016-06-02T00:00:00+10:00")));
        assertThat("summary - lastContributionAmount", summary.getLastContributionAmount(),
                equalTo(new BigDecimal("120.12")));
        assertThat("summary - totalContributions", summary.getTotalContributions(),
                equalTo(new BigDecimal("280.28")));
        assertThat("summary - number of classifications", summary.getContributionSummaryClassifications().size(),
                equalTo(3));

        // ContributionSummaryClassifications
        index = 0;
        infoStr = "contributionSummaryClassifications[" + index + "] -";
        summaryClassification = summary.getContributionSummaryClassifications().get(index);
        assertThat(infoStr + " contributionClassification", summaryClassification.getContributionClassification(),
                equalTo(CONCESSIONAL.getAvaloqInternalId()));
        assertThat(infoStr + " contributionClassificationLabel",
                summaryClassification.getContributionClassificationLabel(),
                equalTo(CONCESSIONAL.getName()));
        assertThat(infoStr + " total", summaryClassification.getTotal(), equalTo(new BigDecimal("200.20")));
        assertThat(infoStr + " availableBalance", summaryClassification.getAvailableBalance(), nullValue());

        index = 1;
        infoStr = "contributionSummaryClassifications[" + index + "] -";
        summaryClassification = summary.getContributionSummaryClassifications().get(index);
        assertThat(infoStr + " contributionClassification", summaryClassification.getContributionClassification(),
                equalTo(NON_CONCESSIONAL.getAvaloqInternalId()));
        assertThat(infoStr + " contributionClassificationLabel",
                summaryClassification.getContributionClassificationLabel(),
                equalTo(NON_CONCESSIONAL.getName()));
        assertThat(infoStr + " total", summaryClassification.getTotal(), equalTo(new BigDecimal("20.02")));
        assertThat(infoStr + " availableBalance", summaryClassification.getAvailableBalance(), nullValue());

        index = 2;
        infoStr = "contributionSummaryClassifications[" + index + "] -";
        summaryClassification = summary.getContributionSummaryClassifications().get(index);
        assertThat(infoStr + "contributionClassification", summaryClassification.getContributionClassification(),
                equalTo(OTHER.getAvaloqInternalId()));
        assertThat(infoStr + "contributionClassificationLabel",
                summaryClassification.getContributionClassificationLabel(),
                equalTo(OTHER.getName()));
        assertThat(infoStr + "total", summaryClassification.getTotal(), equalTo(new BigDecimal("60.06")));
        assertThat(infoStr + "total", summaryClassification.getAvailableBalance(), nullValue());
    }


    private ContributionHistory makeContributionHistory(DateTime financialYearStartDate,
                                                        ContributionSummaryByType... summaries) {
        final MockContributionHistory retval = new MockContributionHistory();

        retval.financialYearStartDate = financialYearStartDate;
        retval.contributionSummariesByType = Arrays.asList(summaries);

        return retval;
    }

    private ContributionSummaryByType makeSummaryByType(Long docId,
                                                        ContributionClassification contributionClassification,
                                                        ContributionType contributionType,
                                                        DateTime lastContributionDate,
                                                        BigDecimal amount) {
        final MockContributionSummaryByType retval = new MockContributionSummaryByType();

        retval.docId = docId;
        retval.contributionClassification = contributionClassification;
        retval.contributionType = contributionType;
        retval.lastContributionDate = lastContributionDate;
        retval.amount = amount;

        return retval;
    }
}
