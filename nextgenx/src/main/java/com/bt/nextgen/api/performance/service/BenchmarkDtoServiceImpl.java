package com.bt.nextgen.api.performance.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.performance.model.BenchmarkDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Benchmark;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;

@Service
class BenchmarkDtoServiceImpl implements BenchmarkDtoService
{

	@Autowired
	private AccountPerformanceIntegrationService accountPerformanceIntegrationService;

	@Override
	public List <BenchmarkDto> findAll(ServiceErrors serviceErrors)
	{
		List <BenchmarkDto> benchmarks = new ArrayList <>();
		BenchmarkDto benchmarkDto = null;
		Map <String, Benchmark> availableBenchmarkObj = accountPerformanceIntegrationService.loadAvailableBenchmarks(serviceErrors);
		Set <Map.Entry <String, Benchmark>> benchmarkSet = availableBenchmarkObj.entrySet();
		Iterator <Entry <String, Benchmark>> benchmarkIterator = benchmarkSet.iterator();
		while (benchmarkIterator.hasNext())
		{
			Entry <String, Benchmark> benchmarkEntry = benchmarkIterator.next();
			Benchmark benchmark = benchmarkEntry.getValue();
			benchmarkDto = new BenchmarkDto(benchmark.getId(), benchmark.getName(), benchmark.getSymbol());
			benchmarks.add(benchmarkDto);
		}
		return benchmarks;
	}
}
