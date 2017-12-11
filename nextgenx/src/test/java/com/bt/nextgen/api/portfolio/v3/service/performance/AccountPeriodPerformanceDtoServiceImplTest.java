package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceReportDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class AccountPeriodPerformanceDtoServiceImplTest {


    @InjectMocks
    private AccountPeriodPerformanceDtoServiceImpl performanceDtoServiceImpl;
    @Mock
    AccountPerformanceIntegrationService accountService;

    PerformanceImpl performanceModel;
    WrapAccountPerformanceImpl wrapAccountPerformanceModel;
    DateRangeAccountKey key;
    ServiceErrors serviceErrors;

    @Before
    public void setup() throws Exception {
        key = new DateRangeAccountKey(EncodedString.fromPlainText("36846").toString(), new DateTime("2015-04-01"), new DateTime("2015-09-01"));
        serviceErrors = new ServiceErrorsImpl();
        performanceModel = new PerformanceImpl();
        wrapAccountPerformanceModel = new WrapAccountPerformanceImpl();
        performanceModel.setCapitalGrowth(BigDecimal.valueOf(0.0322));
        performanceModel.setIncome(BigDecimal.valueOf(0.002));
        performanceModel.setPerformance(BigDecimal.valueOf(0.078));
        wrapAccountPerformanceModel.setPeriodPerformanceData(performanceModel);
        Mockito.when(accountService.loadAccountTotalPerformance(Mockito.any(AccountKey.class), Mockito.anyString(), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(wrapAccountPerformanceModel);
    }

    @Test
    public void testFindSuccess() throws Exception {
        PerformanceReportDto performanceReportDto = performanceDtoServiceImpl.find(key, serviceErrors);
        assertNotNull(performanceReportDto);
        assertEquals(performanceModel.getPerformance(), performanceReportDto.getPerformance());
        assertEquals(performanceModel.getCapitalGrowth(), performanceReportDto.getCapitalGrowth());
        assertEquals(performanceModel.getIncome(), performanceReportDto.getIncome());
    }

    @Test
    public void testFindFailure() throws Exception {
        Mockito.when(accountService.loadAccountTotalPerformance(Mockito.any(AccountKey.class), Mockito.anyString(), Mockito.any(DateTime.class),
                Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(null);

        PerformanceReportDto performanceReportDto = performanceDtoServiceImpl.find(key, serviceErrors);
        assertNull(performanceReportDto);
    }
}
