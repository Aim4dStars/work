package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.base.AvaloqAccountIntegrationServiceImpl;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.base.ThirdPartyDetails;
import com.bt.nextgen.service.integration.cashcategorisation.model.ContributionClassification;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.btfin.panorama.wrap.model.Contribution;
import com.btfin.panorama.wrap.service.ContributionHistoryService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by M044576 on 22/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class WrapContributionHistoryServiceTest {

    @Mock
    private ContributionHistoryService contributionHistoryService;

    @Mock
    private AvaloqAccountIntegrationServiceImpl avaloqAccountIntegrationService;

    @InjectMocks
    private WrapContributionHistoryIntegrationServiceImpl contributionHistoryIntegrationService;

    @Mock
    private WrapContributionTypeConverter wrapContributionTypeConverter;

    @Before
    public void init() throws Exception {
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();
        thirdPartyDetails.setSystemType(SystemType.WRAP);
        thirdPartyDetails.setMigrationKey("M02744476");
        thirdPartyDetails.setMigrationDate(new DateTime());
        when(avaloqAccountIntegrationService.getThirdPartySystemDetails(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(thirdPartyDetails);

        List<Contribution> contributions = new ArrayList<>();

        ContributionHistory history = new ContributionHistoryImpl();
        ContributionSummaryByTypeImpl summary = new ContributionSummaryByTypeImpl();
        summary.setAmount(new BigDecimal("543.56"));
        summary.setContributionClassification(ContributionClassification.CONCESSIONAL);
        summary.setContributionType(new ContributionType("pers_injury_nclaim","Structured Settlement"));
        history.getContributionSummariesByType().add(summary);


        when(contributionHistoryService.getContributionsHistoryForClient(any(String.class), any(Date.class), any(Date.class),
                any(String.class), any(ServiceErrors.class))).thenReturn(contributions);

        when(wrapContributionTypeConverter.toContributionHistory(any(List.class), any(StaticIntegrationService.class))).thenReturn(history);

    }

    @Test
    public void getContributionHistory()
    {
        DateTime fromDate = DateTime.parse("01/07/2008 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));
        DateTime toDate = DateTime.parse("16/05/2011 00:00:00", DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss"));

        AccountKey accountKey = new AccountKey("M00000027");
        ContributionHistory contributionHistory = contributionHistoryIntegrationService.getContributionHistory(accountKey, fromDate, toDate);

        assertThat(contributionHistory, notNullValue());
        assertThat(contributionHistory.getContributionSummariesByType().size(), is(1));
        ContributionSummaryByType contributionSummaryByType = contributionHistory.getContributionSummariesByType().get(0);
        assertThat(contributionSummaryByType.getAmount(), is(new BigDecimal("543.56")));
        assertThat(contributionSummaryByType.getContributionClassification(), is(ContributionClassification.CONCESSIONAL));
        assertThat(contributionSummaryByType.getContributionType().getId(), is("pers_injury_nclaim"));
        assertThat(contributionSummaryByType.getContributionType().getLabel(), is("Structured Settlement"));

    }
}
