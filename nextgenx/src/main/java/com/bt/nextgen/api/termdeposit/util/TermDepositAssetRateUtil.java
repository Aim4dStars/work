package com.bt.nextgen.api.termdeposit.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class TermDepositAssetRateUtil 
{
	private static final BigDecimal PER_MONTH_PERCENT = new BigDecimal(1200);
	
	public static BigDecimal getTotalInterestEarned(BigDecimal bestRate, BigDecimal amount, int term)
	{
		return amount.multiply(bestRate).multiply(new BigDecimal(term)).divide(PER_MONTH_PERCENT, MathContext.DECIMAL32);
	}
	public static BigDecimal getTotalAmountAtMaturity(BigDecimal bestRate, BigDecimal amount, int term)
	{
		return amount.add(amount.multiply(bestRate).multiply(new BigDecimal(term)).divide(PER_MONTH_PERCENT, MathContext.DECIMAL32));
	}
	public static BigDecimal getInterestPerMonth(BigDecimal bestRate, BigDecimal amount)
	{
		return (amount.multiply(bestRate)).divide(PER_MONTH_PERCENT, MathContext.DECIMAL32);
	}
}
