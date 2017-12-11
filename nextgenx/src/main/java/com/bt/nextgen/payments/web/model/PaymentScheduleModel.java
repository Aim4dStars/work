package com.bt.nextgen.payments.web.model;

public class PaymentScheduleModel
{
	private boolean recurring;
	private String paymentEffectiveDate;
	private RecurringScheduleModel recurringSchedule;

	public PaymentScheduleModel()
	{
		recurringSchedule = new RecurringScheduleModel();
	}

	public boolean isRecurring()
	{
		return recurring;
	}

	public void setRecurring(boolean isRecurring)
	{
		this.recurring = isRecurring;
	}

	public String getPaymentEffectiveDate()
	{
		return paymentEffectiveDate;
	}

	public void setPaymentEffectiveDate(String paymentEffectiveDate)
	{
		this.paymentEffectiveDate = paymentEffectiveDate;
	}

	public RecurringScheduleModel getRecurringSchedule()
	{
		return recurringSchedule;
	}

	public void setRecurringSchedule(RecurringScheduleModel recurringSchedule)
	{
		this.recurringSchedule = recurringSchedule;
	}

}
