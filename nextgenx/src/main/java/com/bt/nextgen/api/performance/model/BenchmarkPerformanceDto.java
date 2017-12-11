package com.bt.nextgen.api.performance.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;

public class BenchmarkPerformanceDto extends BaseDto
{
	private String name;
	private String id;
	private BigDecimal performance;

	public BenchmarkPerformanceDto(String name, String id, BigDecimal performance)
	{
		this.name = name;
		this.id = id;
		this.performance = performance;
	}

	public BigDecimal getPerformance()
	{
		return performance;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}
}
