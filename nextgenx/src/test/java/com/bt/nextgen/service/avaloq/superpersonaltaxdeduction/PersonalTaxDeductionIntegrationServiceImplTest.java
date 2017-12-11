package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqExecute;
import com.bt.nextgen.service.avaloq.gateway.AvaloqReportRequest;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by L081224 on 24/10/2016.
 */

@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionIntegrationServiceImplTest {
    private static final String ACCOUNT_NUMBER = "123";
    private static final DateTime FINANCIAL_YEAR_START_DATE = new DateTime();
    private static final DateTime FINANCIAL_YEAR_END_DATE = new DateTime();

    @Mock
    private AvaloqExecute avaloqExecute;

    @Mock
    private PersonalTaxDeductionImpl taxdeduction;

    @InjectMocks
    private PersonalTaxDeductionIntegrationServiceImpl service;

    @Mock
    private ServiceErrors serviceErrors;

    @Test
    public void getPersonalTaxDeductionNotices() {
        final PersonalTaxDeduction result;

        when(avaloqExecute.executeReportRequestToDomain(any(AvaloqReportRequest.class),
                eq(PersonalTaxDeductionImpl.class), any(ServiceErrors.class))).thenReturn(taxdeduction);

        result = service.getPersonalTaxDeductionNotices(ACCOUNT_NUMBER, null, FINANCIAL_YEAR_START_DATE, FINANCIAL_YEAR_END_DATE, serviceErrors);

        verify(avaloqExecute).executeReportRequestToDomain(any(AvaloqReportRequest.class),
                eq(PersonalTaxDeductionImpl.class), eq(serviceErrors));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionHistoryWithNullAccountKey() {
        service.getPersonalTaxDeductionNotices(null, null, FINANCIAL_YEAR_START_DATE, FINANCIAL_YEAR_END_DATE, serviceErrors);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionHistoryWithNullFinancialYearStartDate() {
        service.getPersonalTaxDeductionNotices(ACCOUNT_NUMBER, null, null, FINANCIAL_YEAR_END_DATE, serviceErrors);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getContributionHistoryWithNullFinancialYearEndDate() {
        service.getPersonalTaxDeductionNotices(ACCOUNT_NUMBER, null, FINANCIAL_YEAR_START_DATE, null, serviceErrors);
    }

}
