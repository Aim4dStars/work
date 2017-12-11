package com.bt.nextgen.api.performance.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.api.performance.model.BenchmarkPerformanceDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.performance.Performance;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.bt.nextgen.service.integration.portfolio.performance.WrapAccountPerformance;

@Service
public class BenchmarkPerformanceDtoServiceImpl implements BenchmarkPerformanceDtoService
{

	@Autowired
	private BenchmarkDtoService benchmarkDtoService;

	@Autowired
	private AccountPerformanceIntegrationService accountPerformanceService;

	private final String SEARCH_CRITERIA = "id";

	@Override
	public List <BenchmarkPerformanceDto> search(List <ApiSearchCriteria> criterias, ServiceErrors serviceErrors)
	{
		List <BenchmarkPerformanceDto> benchmarks = new ArrayList <>();
		BigDecimal benchmarkPercentage = BigDecimal.ZERO;
		String accountId = null;
		String startDate = null;
		String endDate = null;

		ApiSearchCriteria accountCriteria = criterias.get(0);
		ApiSearchCriteria startDateCriteria = criterias.get(1);
		ApiSearchCriteria endDateCriteria = criterias.get(2);

		accountId = EncodedString.toPlainText(accountCriteria.getValue());
		startDate = startDateCriteria.getValue();
		endDate = endDateCriteria.getValue();

		for (ApiSearchCriteria criteria : criterias)
		{
			if (criteria.getProperty().equals(SEARCH_CRITERIA))
			{
				WrapAccountPerformance accountPerformance = accountPerformanceService.loadAccountTotalPerformance(AccountKey.valueOf(accountId),
					criteria.getValue(),
					new DateTime(startDate),
					new DateTime(endDate),
					serviceErrors);

				if (accountPerformance != null && accountPerformance.getPeriodPerformanceData() != null)
				{
					Performance performance = accountPerformance.getPeriodPerformanceData();

					if (performance.getBmrkRor() != null)
					{
						benchmarkPercentage = performance.getBmrkRor();
					}

					String benchmarkName = getBenchmarkName(criteria.getValue(), serviceErrors);

					BenchmarkPerformanceDto benchmarkPerformanceDto = new BenchmarkPerformanceDto(benchmarkName,
						criteria.getValue(),
						benchmarkPercentage.divide(BigDecimal.valueOf(100)));

					benchmarks.add(benchmarkPerformanceDto);
				}
			}
		}

		return benchmarks;
	}

	public String getBenchmarkName(String id, ServiceErrors serviceErrors)
	{
		List <BenchmarkDto> benchmarks = benchmarkDtoService.findAll(serviceErrors);
		for (BenchmarkDto benchmark : benchmarks)
		{
			if (id.equals(benchmark.getId()))
			{
				return benchmark.getName();
			}
		}
		return null;
	}
}