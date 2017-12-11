package com.bt.nextgen.api.performance.service;

import com.bt.nextgen.api.performance.model.AccountPerformanceKey;
import com.bt.nextgen.api.performance.model.SubAccountPerformanceReportDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.ManagedPortfolioPerformance;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.SubAccountPerformanceIntegrationService;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class SubAccountPerformanceReportDtoServiceTest
{
	@InjectMocks
	private SubAccountPerformanceReportDtoServiceImpl performanceReportService;

	@Mock
	private SubAccountPerformanceIntegrationService accountPerformanceService;

	@Mock
	private BenchmarkDtoService benchmarkService;

	@Mock
	private AssetIntegrationService assetIntegrationService;

	@Before
	public void setup() throws Exception
	{

	}

	@Test
	public void testGetWeeklyPerformanceReportDto()
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

		ManagedPortfolioPerformance accountPerformance = new ManagedPortfolioPerformance();
		accountPerformance.setPeriodPerformanceData(performance);
        accountPerformance.setDailyPerformanceData(q);
		accountPerformance.setWeeklyPerformanceData(q);
		accountPerformance.setStartDate(startDate);
		accountPerformance.setEndDate(endDate);
		accountPerformance.setAssetId("111");
		accountPerformance.setBenchmarkId("99999");

        Mockito.when(accountPerformanceService.loadPerformanceData(Mockito.any(SubAccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);
        Mockito.when(accountPerformanceService.loadPerformanceSinceInceptionData(Mockito.any(SubAccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(performance);

		Map <String, Asset> assetMap = new HashMap <>();
		AssetImpl asset = new AssetImpl();
		asset.setAssetCode("222");
		asset.setAssetId("111");
		asset.setAssetName("Test asset");
		assetMap.put("111", asset);
		Mockito.when(assetIntegrationService.loadAssets(Mockito.any(Collection.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(assetMap);

		AccountPerformanceKey key = new AccountPerformanceKey(EncodedString.fromPlainText("31442").toString(),
			startDate,
			endDate,
			"1234");
		SubAccountPerformanceReportDto reportDto = performanceReportService.find(key, new ServiceErrorsImpl());
		Assert.assertNotNull(reportDto);
		Assert.assertTrue(reportDto.getColHeaders().size() == 6);
		Assert.assertTrue(reportDto.getColHeaders().get(0).equals("WK1^"));
		Assert.assertTrue(reportDto.getColHeaders().get(1).equals("WK2"));
		Assert.assertTrue(reportDto.getColHeaders().get(2).equals("WK3"));
		Assert.assertTrue(reportDto.getColHeaders().get(3).equals("WK4^"));
		Assert.assertTrue(reportDto.getColHeaders().get(4).equals("Period<br/>return"));
		Assert.assertTrue(reportDto.getColHeaders().get(5).equals("Since<br/>inception"));

		// Validate benchmark
		Assert.assertTrue(reportDto.getAccountPerformanceKey().getBenchmarkId().equals(accountPerformance.getBenchmarkId()));

		// Validate asset details
		Assert.assertTrue(reportDto.getAssetId().equals(asset.getAssetId()));
		Assert.assertTrue(reportDto.getInvestmentName().equals(asset.getAssetName()));

		//test net return data

		Assert.assertEquals(BigDecimal.valueOf(200000), reportDto.getNetReturnData().get(0).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(20), reportDto.getNetReturnData().get(1).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(40), reportDto.getNetReturnData().get(2).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(4000), reportDto.getNetReturnData().get(3).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(2500), reportDto.getNetReturnData().get(4).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(5000), reportDto.getNetReturnData().get(5).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(60000), reportDto.getNetReturnData().get(6).getDataPeriod1());
		Assert.assertEquals(BigDecimal.valueOf(400), reportDto.getNetReturnData().get(7).getDataPeriod1());
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
