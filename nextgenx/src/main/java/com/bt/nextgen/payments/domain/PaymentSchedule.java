package com.bt.nextgen.payments.domain;

import com.bt.nextgen.core.domain.BaseObject;

import java.util.Date;

public class PaymentSchedule extends BaseObject
{
	private boolean recurring = false;
	private Date paymentEffectiveDate;
	private RecurringSchedule recurringSchedule;

	public PaymentSchedule()
	{
		recurringSchedule = new RecurringSchedule();
	}

	public Date getPaymentEffectiveDate()
	{
		return paymentEffectiveDate;
	}

	public void setPaymentEffectiveDate(Date paymentEffectiveDate)
	{
		this.paymentEffectiveDate = paymentEffectiveDate;
	}

	public boolean isRecurring()
	{
		return recurring;
	}

	public void setRecurring(boolean isRecurring)
	{
		this.recurring = isRecurring;
	}

	public RecurringSchedule getRecurringSchedule()
	{
		return recurringSchedule;
	}

	public void setRecurringSchedule(RecurringSchedule recurringSchedule)
	{
		this.recurringSchedule = recurringSchedule;
	}

}
