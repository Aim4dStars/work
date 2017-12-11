package com.bt.nextgen.payments.domain;

public enum PaymentRepeats
{
	REPEAT_NO_END("noDate"), REPEAT_END_DATE("setDate"), REPEAT_NUMBER("setNumber");

	private String repeatValue;

	PaymentRepeats(String args)
	{

		this.repeatValue = args;
	}

	public String getRepeatValue()
	{

		return repeatValue;
	}

	public static String getPaymentRepeat(String values)
	{

		for (PaymentRepeats paymentRepeats : PaymentRepeats.values())
		{

			if (null != paymentRepeats.getRepeatValue() && paymentRepeats.getRepeatValue().equalsIgnoreCase(values))
			{

				return paymentRepeats.toString();
			}
		}

		return PaymentRepeats.REPEAT_NO_END.toString();
	}
}
