package com.bt.nextgen.payments.web.model;

import com.bt.nextgen.core.domain.BaseObject;
import com.bt.nextgen.payments.domain.PayeeType;
import com.bt.nextgen.payments.domain.PaymentFrequency;
import com.bt.nextgen.payments.domain.PaymentRepeatsEnd;
import com.bt.nextgen.web.validator.annotation.Date;
import com.bt.nextgen.web.validator.annotation.Date.Day;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class NewPaymentModel extends BaseObject
{
	private static final long serialVersionUID = 2922487292465960737L;

	private String payeeId;
	@Digits(integer = 20, fraction = 2)
	private BigDecimal amount;

	@Date(from = Day.Today)
	private String date;

	/*@Size(max = 18)
	private String payeeDescription;*/

	@Size(max = 18)
	private String description;

	private boolean recurring;

	@NotNull
	private PaymentFrequency frequency;

	@NotNull
	private PaymentRepeatsEnd endRepeat;

	@Date(from = Day.Today)
	private String repeatEndDate;

	@Digits(integer = 3, fraction = 0)
	private String repeatNumber;

	private String customerRefNo;

	private String payeeType = PayeeType.PAY_ANYONE.toString();

	public String getPayeeType()
	{
		return payeeType;
	}

	public void setPayeeType(String payeeType)
	{
		this.payeeType = payeeType;
	}

	public PaymentRepeatsEnd getEndRepeat()
	{
		return endRepeat;
	}

	public String getRepeatEndDate()
	{
		return repeatEndDate;
	}

	public PaymentFrequency getFrequency()
	{
		return frequency;
	}

	public void setFrequency(PaymentFrequency frequency)
	{
		this.frequency = frequency;
	}

	public void setEndRepeat(PaymentRepeatsEnd endRepeat)
	{
		this.endRepeat = endRepeat;
	}

	public void setRepeatEndDate(String repeatEndDate)
	{
		this.repeatEndDate = repeatEndDate;
	}

	public String getRepeatNumber()
	{
		return repeatNumber;
	}

	public void setRepeatNumber(String repeatNumber)
	{
		this.repeatNumber = repeatNumber;
	}

	public boolean isRecurring()
	{
		return recurring;
	}

	public void setRecurring(boolean recurring)
	{
		this.recurring = recurring;
	}

	public String getPayeeId()
	{
		return payeeId;
	}

	public void setPayeeId(String payeeId)
	{
		this.payeeId = payeeId;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

/*	public String getPayeeDescription()
	{
		return payeeDescription;
	}

	public void setPayeeDescription(String payeeDescription)
	{
		this.payeeDescription = payeeDescription;
	}*/

	public String getCustomerRefNo()
	{
		return customerRefNo;
	}

	public void setCustomerRefNo(String customerRefNo)
	{
		this.customerRefNo = customerRefNo;
	}

}
