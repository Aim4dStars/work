package com.bt.nextgen.api.income.v2.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public class TermDepositIncomeDto extends AbstractIncomeDto
{
	private final DateTime maturityDate;
	private final String term;
	private final String paymentFrequency;
    private Boolean wrapTermDeposit;

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

	public Boolean isWrapTermDeposit() { return wrapTermDeposit; }

    public void setWrapTermDeposit(Boolean wrapTermDeposit) {
        this.wrapTermDeposit = wrapTermDeposit;
    }
}
