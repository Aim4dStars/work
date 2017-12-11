package com.bt.nextgen.api.dashboard.model;

import java.math.BigDecimal;

public class PortfolioBandDto
{
	private Integer band;
	private BigDecimal accounts;
	private BigDecimal fua;

	public PortfolioBandDto(Integer band, BigDecimal accounts, BigDecimal fua)
	{
		this.band = band;
		this.accounts = accounts;
		this.fua = fua;
	}

	public Integer getBand()
	{
		return band;
	}

	public BigDecimal getAccounts()
	{
		return accounts;
	}

	public BigDecimal getFua()
	{
		return fua;
	}

}
