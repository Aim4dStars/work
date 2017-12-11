package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceChartDto;
import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
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
public class AccountPerformanceChartDtoServiceTest
{
	@InjectMocks
	private AccountPerformanceChartDtoServiceImpl performanceChartService;

	@Mock
	private AccountPerformanceIntegrationService accountPerformanceService;

	@Mock
	private BenchmarkDtoService benchmarkService;

	@Before
	public void setup() throws Exception
	{

	}

	@Test
	public void testGetWeeklyPerformanceChartDto()
	{
		DateTime startDate = DateTime.parse("2014-01-01");
		DateTime endDate = DateTime.parse("2014-01-25");

		// Period performance data.
		Performance performance = createPerformanceTestData(startDate, endDate);

		List <Performance> q = new ArrayList <>();
		q.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-05")));
		q.add(createPerformanceTestData(DateTime.parse("2014-01-06"), DateTime.parse("2014-01-12")));
		q.add(createPerformanceTestData(DateTime.parse("2014-01-13"), DateTime.parse("2014-01-19")));
		q.add(createPerformanceTestData(DateTime.parse("2014-01-20"), DateTime.parse("2014-01-25")));

		WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setAccountKey(AccountKey.valueOf("AccountKey"));		
		accountPerformance.setPeriodPerformanceData(performance);
		accountPerformance.setDailyPerformanceData(q);
		accountPerformance.setWeeklyPerformanceData(q);
		accountPerformance.setStartDate(startDate);
		accountPerformance.setEndDate(endDate);

		Mockito.when(accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class),
			Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);

		AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(),
			startDate,
			endDate,
			"1234");
		AccountPerformanceChartDto chartDto = performanceChartService.find(key, new ServiceErrorsImpl());
		Assert.assertNotNull(chartDto);
		Assert.assertTrue(chartDto.getColHeaders().size() == 4);
		Assert.assertTrue(chartDto.getColHeaders().get(0).equals("WK1^"));
		Assert.assertTrue(chartDto.getColHeaders().get(1).equals("WK2"));
		Assert.assertTrue(chartDto.getColHeaders().get(2).equals("WK3"));
		Assert.assertTrue(chartDto.getColHeaders().get(3).equals("WK4^"));
	}

	@Test
	public void testGetMonthlyPerformanceChartDto()
	{
		DateTime startDate = DateTime.parse("2014-01-01");
		DateTime endDate = DateTime.parse("2014-12-31");

		// Period performance data.
		Performance performance = createPerformanceTestData(startDate, endDate);

		List <Performance> monthly = new ArrayList <>();
		monthly.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-01-31")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-02-01"), DateTime.parse("2014-02-28")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-03-01"), DateTime.parse("2014-03-31")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-04-01"), DateTime.parse("2014-04-30")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-05-01"), DateTime.parse("2014-05-31")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-06-01"), DateTime.parse("2014-06-30")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-07-01"), DateTime.parse("2014-07-31")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-08-01"), DateTime.parse("2014-08-31")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-09-01"), DateTime.parse("2014-09-30")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-10-01"), DateTime.parse("2014-10-31")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-11-01"), DateTime.parse("2014-11-30")));
		monthly.add(createPerformanceTestData(DateTime.parse("2014-12-01"), DateTime.parse("2014-12-31")));

		WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setAccountKey(AccountKey.valueOf("AccountKey"));		
		accountPerformance.setPeriodPerformanceData(performance);
		accountPerformance.setMonthlyPerformanceData(monthly);
        accountPerformance.setWeeklyPerformanceData(monthly);
		accountPerformance.setStartDate(startDate);
		accountPerformance.setEndDate(endDate);

		Mockito.when(accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class),
			Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);

		AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(),
			startDate,
			endDate,
			"1234");
		AccountPerformanceChartDto chartDto = performanceChartService.find(key, new ServiceErrorsImpl());
		Assert.assertNotNull(chartDto);
		Assert.assertTrue(chartDto.getColHeaders().get(0).equals("Jan 14"));
		Assert.assertTrue(chartDto.getColHeaders().get(1).equals("Feb 14"));
		Assert.assertTrue(chartDto.getColHeaders().get(2).equals("Mar 14"));
		Assert.assertTrue(chartDto.getColHeaders().get(10).equals("Nov 14"));
		Assert.assertTrue(chartDto.getColHeaders().get(11).equals("Dec 14"));
	}

	@Test
	public void testGetQuarterlyPerformanceReportDto()
	{
		DateTime startDate = DateTime.parse("2014-01-01");
		DateTime endDate = DateTime.parse("2015-02-28");

		// Period performance data.
		Performance performance = createPerformanceTestData(startDate, endDate);

		List <Performance> q = new ArrayList <>();
		q.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-03-31")));
		q.add(createPerformanceTestData(DateTime.parse("2014-04-01"), DateTime.parse("2014-06-30")));
		q.add(createPerformanceTestData(DateTime.parse("2014-07-01"), DateTime.parse("2014-09-30")));
		q.add(createPerformanceTestData(DateTime.parse("2014-10-01"), DateTime.parse("2014-12-31")));
		q.add(createPerformanceTestData(DateTime.parse("2015-01-01"), DateTime.parse("2015-02-28")));

		WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
        accountPerformance.setAccountKey(AccountKey.valueOf("AccountKey"));		
		accountPerformance.setPeriodPerformanceData(performance);
		accountPerformance.setQuarterlyPerformanceData(q);
		accountPerformance.setWeeklyPerformanceData(q);
		accountPerformance.setStartDate(startDate);
		accountPerformance.setEndDate(endDate);

		Mockito.when(accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class),
			Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);

		AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(),
			startDate,
			endDate,
			"1234");
		AccountPerformanceChartDto chartDto = performanceChartService.find(key, new ServiceErrorsImpl());
		Assert.assertNotNull(chartDto);
		Assert.assertTrue(chartDto.getColHeaders().size() == 5);
		Assert.assertTrue(chartDto.getColHeaders().get(0).equals("2014 Q1"));
		Assert.assertTrue(chartDto.getColHeaders().get(1).equals("2014 Q2"));
		Assert.assertTrue(chartDto.getColHeaders().get(2).equals("2014 Q3"));
		Assert.assertTrue(chartDto.getColHeaders().get(3).equals("2014 Q4"));
		Assert.assertTrue(chartDto.getColHeaders().get(4).equals("2015 Q1^"));
	}

	@Test
	public void testGetYearlyPerformanceReportDto()
	{
		DateTime startDate = DateTime.parse("2014-01-01");
		DateTime endDate = DateTime.parse("2016-01-30");

		// Period performance data.
		Performance performance = createPerformanceTestData(startDate, endDate);

		List <Performance> q = new ArrayList <>();
		q.add(createPerformanceTestData(DateTime.parse("2014-01-01"), DateTime.parse("2014-12-31")));
		q.add(createPerformanceTestData(DateTime.parse("2015-01-01"), DateTime.parse("2015-12-31")));
		q.add(createPerformanceTestData(DateTime.parse("2016-01-01"), DateTime.parse("2016-01-30")));

		WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
		accountPerformance.setAccountKey(AccountKey.valueOf("AccountKey"));
		accountPerformance.setPeriodPerformanceData(performance);
		accountPerformance.setYearlyPerformanceData(q);
		accountPerformance.setWeeklyPerformanceData(q);
		accountPerformance.setStartDate(startDate);
		accountPerformance.setEndDate(endDate);

		Mockito.when(accountPerformanceService.loadAccountPerformanceReport(Mockito.any(AccountKey.class),
			Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);

		AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(),
			startDate,
			endDate,
			"1234");
		AccountPerformanceChartDto chartDto = performanceChartService.find(key, new ServiceErrorsImpl());
		Assert.assertNotNull(chartDto);
		Assert.assertTrue(chartDto.getColHeaders().size() == 3);
		Assert.assertTrue(chartDto.getColHeaders().get(0).equals("2014"));
		Assert.assertTrue(chartDto.getColHeaders().get(1).equals("2015"));
		Assert.assertTrue(chartDto.getColHeaders().get(2).equals("2016^"));
	}

	private Performance createPerformanceTestData(DateTime startDate, DateTime endDate)
	{
		PerformanceImpl p2 = new PerformanceImpl();
		p2.setBmrkRor(random());
		p2.setPerformance(random());
		p2.setCapitalGrowth(random());
		p2.setIncomeRtn(random());
		p2.setBmrkRor(random());
		p2.setActiveRor(random());
		p2.setPeriodSop(startDate);
		p2.setPeriodEop(endDate);

		p2.setOpeningBalance(BigDecimal.valueOf(200000));
		p2.setInflows(BigDecimal.valueOf(20));
		p2.setOutflows(BigDecimal.valueOf(40));
		p2.setIncome(BigDecimal.valueOf(4000));
		p2.setExpenses(BigDecimal.valueOf(2500));
		p2.setMktMvt(BigDecimal.valueOf(5000));
		p2.setBalanceBeforeFee(BigDecimal.valueOf(50000));
		p2.setFee(BigDecimal.valueOf(20));
		p2.setClosingBalanceAfterFee(BigDecimal.valueOf(60000));
		p2.setNetGainLoss(BigDecimal.valueOf(400));

		return p2;
	}

	private BigDecimal random()
	{
		double max = 0.99;
		double min = -0.99;
		double r = Math.random();
		if (r < 0.5)
		{
			return new BigDecimal(((1 - Math.random()) * (max - min) + min));
		}
		return new BigDecimal((Math.random() * (max - min) + min));
	}
}
