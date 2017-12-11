package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.performance.AccountBenchmarkPerformanceDto;
import com.bt.nextgen.api.account.v2.model.performance.AccountBenchmarkPerformanceKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AccountBenchmarkPerformanceChartDtoServiceTest {
    @InjectMocks
    private AccountBenchmarkPerformanceChartDtoServiceImpl performanceChartService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    private WrapAccountPerformanceImpl accountPerformance;

    @Before
    public void setup() throws Exception {
        accountPerformance = new WrapAccountPerformanceImpl();
        List<Performance> dailyPerformance = new ArrayList<>();
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-01")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-02"), DateTime.parse("2014-01-02")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-07"), DateTime.parse("2014-01-07")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-07"), DateTime.parse("2014-01-07")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-14"), DateTime.parse("2014-01-14")));
        dailyPerformance.add(createPerformanceTestData(DateTime.parse("2014-01-24"), DateTime.parse("2014-01-24")));
        accountPerformance.setDailyPerformanceData(dailyPerformance);
        Mockito.when(
                accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                accountPerformance);
    }

    @Test
    public void testGetBenchmarkPerformance() {
        DateTime startDate = DateTime.parse("2014-01-01");
        DateTime endDate = DateTime.parse("2014-01-25");
        AccountBenchmarkPerformanceKey key = new AccountBenchmarkPerformanceKey(EncodedString.fromPlainText("31442").toString(),
                startDate, endDate, "11970");
        AccountBenchmarkPerformanceDto accountBenchmarkPerformanceDto = performanceChartService
                .find(key, new ServiceErrorsImpl());
        Assert.assertNotNull(accountBenchmarkPerformanceDto);
        Assert.assertNotNull(accountBenchmarkPerformanceDto.getBenchmarkData().get(0));
        Assert.assertNotNull(accountBenchmarkPerformanceDto.getBenchmarkData().get(0).getValue());
    }

    private Performance createPerformanceTestData(DateTime startDate, DateTime endDate) {
        PerformanceImpl p2 = new PerformanceImpl();
        p2.setBmrkRor(random());
        return p2;
    }

    private BigDecimal random() {
        double max = 0.99;
        double min = -0.99;
        double r = Math.random();
        if (r < 0.5) {
            return new BigDecimal(((1 - Math.random()) * (max - min) + min));
        }
        return new BigDecimal((Math.random() * (max - min) + min));
    }
}
