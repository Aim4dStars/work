package com.bt.nextgen.api.performance.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.BenchmarkImpl;
import com.bt.nextgen.service.integration.asset.Benchmark;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class BenchmarkDtoServiceTest
{
	@InjectMocks
	private BenchmarkDtoServiceImpl benchmarkService;

	@Mock
	private AccountPerformanceIntegrationService accountPerformanceService;

	@Before
	public void setup() throws Exception
	{

	}

	@Test
	public void testGetAvailableBenchmarks_When_BenchmarksNotPresent()
	{
		Map <String, Benchmark> benchmarkMap = new HashMap <String, Benchmark>();
		Mockito.when(accountPerformanceService.loadAvailableBenchmarks(Mockito.any(ServiceErrors.class)))
			.thenReturn(benchmarkMap);

		List <BenchmarkDto> benchmarks = benchmarkService.findAll(new ServiceErrorsImpl());
		Assert.assertNotNull(benchmarks);
		Assert.assertEquals(0, benchmarks.size());
	}

	@Test
	public void testGetAvailableBenchmarks_When_BenchmarksPresent()
	{
		BenchmarkImpl benchmark1 = new BenchmarkImpl();
		benchmark1.setId("70275");
		benchmark1.setName("S&P/ASX 20 Index");
		benchmark1.setSymbol("ASX20");

		BenchmarkImpl benchmark2 = new BenchmarkImpl();
		benchmark2.setId("70276");
		benchmark2.setName("S&P/ASX 50 Index");
		benchmark2.setSymbol("ASX50");

		Map <String, Benchmark> benchmarkMap = new HashMap <String, Benchmark>();
		benchmarkMap.put("70275", benchmark1);
		benchmarkMap.put("70276", benchmark2);

		Mockito.when(accountPerformanceService.loadAvailableBenchmarks(Mockito.any(ServiceErrors.class)))
			.thenReturn(benchmarkMap);
		List <BenchmarkDto> benchmarks = benchmarkService.findAll(new ServiceErrorsImpl());

		Assert.assertNotNull(benchmarks);
		Assert.assertEquals(2, benchmarks.size());
		Assert.assertEquals("70275", benchmarks.get(0).getId());
		Assert.assertEquals("S&P/ASX 20 Index", benchmarks.get(0).getName());
		Assert.assertEquals("ASX20", benchmarks.get(0).getSymbol());
	}
}
