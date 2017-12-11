package com.bt.nextgen.service.integration.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistory;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionHistoryIntegrationService;
import com.bt.nextgen.service.avaloq.contributionhistory.ContributionSummaryByType;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;

import static com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification.CONCESSIONAL;
import static com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification.NON_CONCESSIONAL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by M022641 on 11/06/2016.
 */
public class ContributionHistoryIntegrationServiceTest extends BaseSecureIntegrationTest {
    private static final String CONTRIBUTION_TYPE_EMPLOYER = "1";
    private static final String CONTRIBUTION_TYPE_OTHER = "13";

    @Autowired
    @Qualifier("ContributionHistoryIntegrationServiceImpl")
    private ContributionHistoryIntegrationService service;


    @Test
    public void getContributionHistory() {
        final DateTime startOfFinancialYear = new DateTime("2015-07-01T00:00:00+10:00");
        final DateTime endOfFinancialYear = startOfFinancialYear.plusYears(1).minusDays(1);
        final ContributionHistory contributionHistory;
        final List<ContributionSummaryByType> summaries;
        ContributionSummaryByType summary;

        contributionHistory = service.getContributionHistory(new AccountKey("123"), startOfFinancialYear, endOfFinancialYear);
        assertThat("financial year", contributionHistory.getFinancialYearStartDate(), equalTo(startOfFinancialYear));

        summaries = contributionHistory.getContributionSummariesByType();
        assertThat("contribution types exist",summaries, notNullValue());
        assertThat("number of contribution types", summaries.size(), equalTo(2));

        summary = summaries.get(0);
        assertThat("contribution summary 0 - docId", summary.getDocId(), equalTo(new Long(5889876)));
        assertThat("contribution summary 0 - contributionClassification", summary.getContributionClassification(), equalTo(CONCESSIONAL));
        assertThat("contribution summary 0 - contributionType id", summary.getContributionType().getId(), equalTo("employer"));
        assertThat("contribution summary 0 - contributionType label", summary.getContributionType().getLabel(), equalTo("Employer - Super Guarantee (SG)"));
        assertThat("contribution summary 0 - lastContributionDate", summary.getLastContributionDate(), equalTo(new DateTime("2016-05-21T00:00:00+10:00")));
        assertThat("contribution summary 0 - amount", summary.getAmount(), equalTo(new BigDecimal("4000")));

        summary = summaries.get(1);
        assertThat("contribution summary 1 - docId", summary.getDocId(), equalTo(new Long(3667930)));
        assertThat("contribution summary 1 - contributionClassification", summary.getContributionClassification(), equalTo(NON_CONCESSIONAL));
        assertThat("contribution summary 1 - contributionType id", summary.getContributionType(), nullValue());
        assertThat("contribution summary 1 - contributionType label", summary.getContributionType(), nullValue());
        assertThat("contribution summary 1 - lastContributionDate", summary.getLastContributionDate(), equalTo(new DateTime("2016-04-05T00:00:00+10:00")));
        assertThat("contribution summary 1 - amount", summary.getAmount(), equalTo(new BigDecimal("8500")));
    }
}
