package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.payments.domain.PaymentFrequency;

public class RecurringScheduleModel
{
	private String line1;
	private String line2;
	private String paymentStartDate;
	private String paymentEndDate;
	private String nextPaymentDate;
	private String lastPaymentDate;
	private PaymentFrequency paymentFrequency;
	private int numberOfPayments;

	public String getLine1()
	{
		return line1;
	}

	public void setLine1(String line1)
	{
		this.line1 = line1;
	}

	public String getLine2()
	{
		return line2;
	}

	public void setLine2(String line2)
	{
		this.line2 = line2;
	}

	public String getPaymentStartDate()
	{
		return paymentStartDate;
	}

	public void setPaymentStartDate(String paymentStartDate)
	{
		this.paymentStartDate = paymentStartDate;
	}

	public String getPaymentEndDate()
	{
		return paymentEndDate;
	}

	public void setPaymentEndDate(String paymentEndDate)
	{
		this.paymentEndDate = paymentEndDate;
	}

	public String getNextPaymentDate()
	{
		return nextPaymentDate;
	}

	public void setNextPaymentDate(String nextPaymentDate)
	{
		this.nextPaymentDate = nextPaymentDate;
	}

	public String getLastPaymentDate()
	{
		return lastPaymentDate;
	}

	public void setLastPaymentDate(String lastPaymentDate)
	{
		this.lastPaymentDate = lastPaymentDate;
	}

	public PaymentFrequency getPaymentFrequency()
	{
		return paymentFrequency;
	}

	public void setPaymentFrequency(PaymentFrequency paymentFrequency)
	{
		this.paymentFrequency = paymentFrequency;
	}

	public int getNumberOfPayments()
	{
		return numberOfPayments;
	}

	public void setNumberOfPayments(int numberOfPayments)
	{
		this.numberOfPayments = numberOfPayments;
	}

}
