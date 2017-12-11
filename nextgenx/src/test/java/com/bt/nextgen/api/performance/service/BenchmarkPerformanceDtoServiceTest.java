package com.bt.nextgen.api.performance.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.avaloq.portfolio.performance.WrapAccountPerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class BenchmarkPerformanceDtoServiceTest
{
	@InjectMocks
	private BenchmarkPerformanceDtoServiceImpl benchmarkPerformanceService;

	@Mock
	private AccountPerformanceIntegrationService accountPerformanceService;

	@Mock
	private BenchmarkDtoService benchmarkService;

	@Before
	public void setup() throws Exception
	{

	}

	@Test
	public void testGetBenchmarkPerformance_When_NoBenchmark_Present()
	{
		List <Performance> periodPerformance = new ArrayList <>();
		List <ApiSearchCriteria> criterias = new ArrayList <>();
		String key = EncodedString.fromPlainText("45646").toString();

		criterias.add(new ApiSearchCriteria("accountId", SearchOperation.EQUALS, key, OperationType.STRING));
		criterias.add(new ApiSearchCriteria("effectiveDate", SearchOperation.EQUALS, "2014-09-24", OperationType.STRING));
		criterias.add(new ApiSearchCriteria("effectiveDate", SearchOperation.EQUALS, "2014-09-24", OperationType.STRING));
		criterias.add(new ApiSearchCriteria("id", SearchOperation.EQUALS, "12", OperationType.STRING));

		WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
		accountPerformance.setPeriodPerformanceData(null);

		Mockito.when(accountPerformanceService.loadAccountTotalPerformance(Mockito.any(AccountKey.class),
			Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);

		List <BenchmarkPerformanceDto> benchmarks = benchmarkPerformanceService.search(criterias, new ServiceErrorsImpl());

		Assert.assertEquals(0, benchmarks.size());
	}

	@Test
	public void testGetBenchmarkPerformance_When_BenchmarkPresent()
	{
		PerformanceImpl performance = new PerformanceImpl();
		performance.setBmrkRor(BigDecimal.valueOf(0.20));
		String key = EncodedString.fromPlainText("45646").toString();

		List <ApiSearchCriteria> criterias = new ArrayList <>();
		criterias.add(new ApiSearchCriteria("accountId", SearchOperation.EQUALS, key, OperationType.STRING));
		criterias.add(new ApiSearchCriteria("effectiveDate", SearchOperation.EQUALS, "2014-09-24", OperationType.STRING));
		criterias.add(new ApiSearchCriteria("effectiveDate", SearchOperation.EQUALS, "2014-09-24", OperationType.STRING));
		criterias.add(new ApiSearchCriteria("id", SearchOperation.EQUALS, "12", OperationType.STRING));

		WrapAccountPerformanceImpl accountPerformance = new WrapAccountPerformanceImpl();
		accountPerformance.setPeriodPerformanceData(performance);

		Mockito.when(accountPerformanceService.loadAccountTotalPerformance(Mockito.any(AccountKey.class),
			Mockito.any(String.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(accountPerformance);

		List <BenchmarkDto> benchmarks = new ArrayList <>();
		BenchmarkDto benchmark = new BenchmarkDto("12", "ASX20", "ASX20");
		benchmarks.add(benchmark);

		Mockito.when(benchmarkService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(benchmarks);

		List <BenchmarkPerformanceDto> benchmarkPerformanceDtos = benchmarkPerformanceService.search(criterias,
			new ServiceErrorsImpl());

		Assert.assertNotNull(benchmarkPerformanceDtos);
		Assert.assertEquals(1, benchmarkPerformanceDtos.size());
	}
}
