package com.bt.nextgen.payments.domain;

import com.bt.nextgen.core.domain.BaseObject;

import java.util.Date;

public class RecurringSchedule extends BaseObject
{
	private Date paymentStartDate;
	private Date paymentEndDate;
	private Date nextPaymentDate;
	private Date lastPaymentDate;
	private PaymentFrequency paymentFrequency;
	private int numberOfPayments;

	public Date getPaymentStartDate()
	{
		return paymentStartDate;
	}

	public void setPaymentStartDate(Date paymentStartDate)
	{
		this.paymentStartDate = paymentStartDate;
	}

	public Date getPaymentEndDate()
	{
		return paymentEndDate;
	}

	public void setPaymentEndDate(Date paymentEndDate)
	{
		this.paymentEndDate = paymentEndDate;
	}

	public Date getNextPaymentDate()
	{
		return nextPaymentDate;
	}

	public void setNextPaymentDate(Date nextPaymentDate)
	{
		this.nextPaymentDate = nextPaymentDate;
	}

	public Date getLastPaymentDate()
	{
		return lastPaymentDate;
	}

	public void setLastPaymentDate(Date lastPaymentDate)
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
