package com.bt.nextgen.api.performance.model;

import com.bt.nextgen.core.api.model.BaseDto;

public class BenchmarkDto extends BaseDto
{
	private String id;
	private String name;
	private String symbol;

	public BenchmarkDto(String id, String name, String symbol)
	{
		this.id = id;
		this.name = name;
		this.symbol = symbol;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getSymbol()
	{
		return symbol;
	}
}
