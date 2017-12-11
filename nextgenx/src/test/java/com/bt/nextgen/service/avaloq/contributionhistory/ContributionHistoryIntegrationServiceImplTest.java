package com.bt.nextgen.service.avaloq.contributionhistory;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests {@link ContributionHistoryIntegrationServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContributionHistoryIntegrationServiceImplTest {
    private static final AccountKey ACCOUNT_KEY = new AccountKey("123");
    private static final DateTime FINANCIAL_YEAR_START_DATE = new DateTime();
    private static final DateTime FINANCIAL_YEAR_END_DATE = new DateTime();

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private ContributionHistoryImpl history;

    private ContributionHistoryIntegrationServiceImpl service;


    @Before
    public void init() throws Exception {
        final Field field;

        service = new ContributionHistoryIntegrationServiceImpl();

        // simulate autowire
        field = service.getClass().getDeclaredField("avaloqExecute");
        field.setAccessible(true);
        field.set(service, avaloqExecute);
    }


    @Test
    public void getContributionHistory() {
        final ContributionHistory result;

        when(avaloqExecute.executeReportRequestToDomain(any(AvaloqReportRequest.class),
                eq(ContributionHistoryImpl.class), any(ServiceErrors.class))).thenReturn(history);

        result = service.getContributionHistory(ACCOUNT_KEY, FINANCIAL_YEAR_START_DATE, FINANCIAL_YEAR_END_DATE);

        verify(avaloqExecute).executeReportRequestToDomain(any(AvaloqReportRequest.class),
                eq(ContributionHistoryImpl.class), any(ServiceErrors.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionHistoryWithNullAccountKey() {
        service.getContributionHistory(null, FINANCIAL_YEAR_START_DATE, FINANCIAL_YEAR_END_DATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionHistoryWithNullFinancialYearStartDate() {
        service.getContributionHistory(ACCOUNT_KEY, null, FINANCIAL_YEAR_END_DATE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionHistoryWithNullFinancialYearEndDate() {
        service.getContributionHistory(ACCOUNT_KEY, FINANCIAL_YEAR_START_DATE, null);
    }
}
