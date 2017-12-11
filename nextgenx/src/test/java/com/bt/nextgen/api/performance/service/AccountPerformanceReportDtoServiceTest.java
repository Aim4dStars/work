package com.bt.nextgen.api.performance.service;

import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.AccountPerformanceReportDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.performance.PeriodicPerformance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceReportDtoServiceTest {
    @InjectMocks
    private AccountPerformanceReportDtoServiceImpl performanceReportService;

    @Mock
    private AccountPerformanceIntegrationService accountPeformanceService;

    @Mock
    private AccountPerformanceReportDtoServiceDataAggregatorImpl accountPerformanceReportDtoServiceAggregator;
    
    @Before
    public void setup() throws Exception {
        
       when(accountPeformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class), Mockito.any(String.class),
                Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                        .thenReturn(Mockito.mock(WrapAccountPerformanceImpl.class));

        when(accountPeformanceService.loadAccountPerformanceSummarySinceInception(Mockito.any(AccountKey.class),
                Mockito.any(String.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class)))
                        .thenReturn(Mockito.mock((PeriodicPerformance.class)));

        Mockito.when(accountPerformanceReportDtoServiceAggregator.buildReportDto(Mockito.any(AccountPerformanceKey.class),
                Mockito.any(WrapAccountPerformance.class), Mockito.any(Performance.class)))
                .thenReturn(Mockito.mock(AccountPerformanceReportDto.class));

    }

    @Test
    public void testFind() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2014-01-25");                

        AccountPerformanceKey accountPerformanceKey = new AccountPerformanceKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0", startDate, endDate, "1234");
        performanceReportService.find(accountPerformanceKey, new ServiceErrorsImpl());
    }
}
