package com.bt.nextgen.service.avaloq;

import java.math.BigDecimal;

public class PortfolioUtils
{
    //TODO - this should probably be centralised... switch to using Money?
	public static BigDecimal getValuationAsPercent(BigDecimal amount, BigDecimal totalPortfolio)
	{
		if (amount == null)
		{
			amount = new BigDecimal(0);
		}
		if (totalPortfolio == null)
		{
			totalPortfolio = new BigDecimal(0);
		}
		if (BigDecimal.ZERO.compareTo(amount) == 0 || BigDecimal.ZERO.compareTo(totalPortfolio) == 0)
		{
			return BigDecimal.ZERO;
		}
		else
		{
			return amount.divide(totalPortfolio, 6, BigDecimal.ROUND_HALF_UP);
		}
	}

}
