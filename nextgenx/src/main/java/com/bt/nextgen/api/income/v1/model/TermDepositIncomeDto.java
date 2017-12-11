package com.bt.nextgen.api.income.v1.model;

import java.math.BigDecimal;

import org.joda.time.DateTime;

@Deprecated
public class TermDepositIncomeDto extends IncomeDto
{
	private final DateTime maturityDate;
	private final String term;
	private final String paymentFrequency;

	public TermDepositIncomeDto(String name, String code, DateTime paymentDate, DateTime maturityDate, BigDecimal amount,
		String term, String paymentFrequency)
	{
		super(name, code, paymentDate, amount);
		this.maturityDate = maturityDate;
		this.term = term;
		this.paymentFrequency = paymentFrequency;
	}

	public DateTime getMaturityDate()
	{
		return maturityDate;
	}

	public String getTerm()
	{
		return term;
	}

	public String getPaymentFrequency()
	{
		return paymentFrequency;
	}
}
