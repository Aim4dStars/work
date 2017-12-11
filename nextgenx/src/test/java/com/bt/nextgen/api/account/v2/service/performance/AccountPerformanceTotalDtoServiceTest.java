package com.bt.nextgen.api.account.v2.service.performance;

import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;
import com.bt.nextgen.api.account.v2.model.performance.AccountPerformanceTotalDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceTotalDtoServiceTest {

    @InjectMocks
    private AccountPerformanceTotalDtoServiceImpl performanceTotalService;

    @Mock
    private AccountPerformanceIntegrationService accountPerformanceService;

    private WrapAccountPerformance wap;
    private DateTime startDate;
    private DateTime endDate;
    private DateRangeAccountKey key;

    @Before
    public void setup() {
        wap = createPerformanceTestData();
        startDate = DateTime.parse("2015-01-01");
        endDate = DateTime.parse("2015-03-03");
        key = new DateRangeAccountKey(EncodedString.fromPlainText("accountKey").toString(), startDate, endDate);
    }

    @Test
    public void testFind_whenMethodInvoked_ThenCorrectArgumentsArePassed() {
        Mockito.when(
                accountPerformanceService.loadAccountTotalPerformance(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenAnswer(
                new Answer<WrapAccountPerformance>() {

                    @Override
                    public WrapAccountPerformance answer(InvocationOnMock invocation) throws Throwable {
                        Assert.assertEquals(AccountKey.valueOf("accountKey"), invocation.getArguments()[0]);
                        Assert.assertEquals(startDate, invocation.getArguments()[2]);
                        Assert.assertEquals(endDate, invocation.getArguments()[3]);
                        return wap;
                    }
                });

        performanceTotalService.find(key, new FailFastErrorsImpl());
    }

    @Test
    public void testFind_whenValuesAreFetchedFromIntegration_ThenModelIsConstructed() {

        Mockito.when(
                accountPerformanceService.loadAccountTotalPerformance(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                wap);

        AccountPerformanceTotalDto dto = performanceTotalService.find(key, new FailFastErrorsImpl());

        Assert.assertEquals(key.getAccountId(), dto.getKey().getAccountId());

        Assert.assertEquals(BigDecimal.valueOf(100), dto.getPerformanceBeforeFeesDollars());
        Assert.assertEquals(BigDecimal.valueOf(0.015), dto.getPerformanceBeforeFeesPercent());
        Assert.assertEquals(BigDecimal.valueOf(90), dto.getPerformanceAfterFeesDollars());
        Assert.assertEquals(BigDecimal.valueOf(0.012), dto.getPerformanceAfterFeesPercent());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceGrowthPercent());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceIncomePercent());
    }

    @Test
    public void testFind_whenEmptyValuesAreFetchedFromIntegration_ThenEmptyModelCreated() {

        Mockito.when(
                accountPerformanceService.loadAccountTotalPerformance(Mockito.any(AccountKey.class), Mockito.any(String.class),
                        Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class))).thenReturn(
                null);

        AccountPerformanceTotalDto dto = performanceTotalService.find(key, new FailFastErrorsImpl());

        Assert.assertNotNull(dto);

        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceBeforeFeesDollars());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceBeforeFeesPercent());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceAfterFeesDollars());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceAfterFeesPercent());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceGrowthPercent());
        Assert.assertEquals(BigDecimal.ZERO, dto.getPerformanceIncomePercent());

    }

    private WrapAccountPerformance createPerformanceTestData() {

        PerformanceImpl performance = new PerformanceImpl();
        performance.setPerformanceBeforeFee(BigDecimal.valueOf(100));
        performance.setTwrrGross(BigDecimal.valueOf(1.5));
        performance.setPerformanceAfterFee(BigDecimal.valueOf(90));
        performance.setPerformance(BigDecimal.valueOf(1.2));
        performance.setIncomeRtn(null);
        performance.setCapitalGrowth(null);

        WrapAccountPerformanceImpl wap = new WrapAccountPerformanceImpl();
        wap.setPeriodPerformanceData(performance);

        return wap;
    }
}
