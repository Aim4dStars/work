package com.bt.nextgen.payments.domain;

public class PaymentMethod
{
	private Method method;
	private String narrative = "";

	public PaymentMethod(Method method, String narrative)
	{
		this.method = method;
		this.narrative = narrative;
	}

	public enum Method
	{
		DIRECT_DEBIT, DIRECT_CREDIT, CHEQUE, BPAY;
	}

	public Method getMethod()
	{
		return method;
	}

	public void setMethod(Method method)
	{
		this.method = method;
	}

	public String getNarrative()
	{
		return narrative;
	}

	public void setNarrative(String narrative)
	{
		this.narrative = narrative;
	}
}
