package com.bt.nextgen.service.integration.dashboard;

import java.math.BigDecimal;

public interface TopAccountsByValue
{
	public BigDecimal getCashValue();

	public BigDecimal getPortfolioValue();

	public String getOrderBy();
}
