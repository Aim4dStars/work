package com.bt.nextgen.api.dashboard.model;

import java.util.List;

import com.bt.nextgen.core.api.model.BaseDto;

public class TopAccountsByValueDto extends BaseDto
{
	private List <TopAccountDto> topAccountsByCash;
	private List <TopAccountDto> topAccountsByPortfolio;

	public TopAccountsByValueDto(List <TopAccountDto> topAccountsByCash, List <TopAccountDto> topAccountsByPortfolio)
	{
		this.topAccountsByCash = topAccountsByCash;
		this.topAccountsByPortfolio = topAccountsByPortfolio;
	}

	public List <TopAccountDto> getTopAccountsByCash()
	{
		return topAccountsByCash;
	}

	public List <TopAccountDto> getTopAccountsByPortfolio()
	{
		return topAccountsByPortfolio;
	}
}
