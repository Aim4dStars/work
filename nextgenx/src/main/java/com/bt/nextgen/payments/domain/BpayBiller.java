package com.bt.nextgen.payments.domain;

public class BpayBiller {
	private String billerCode;
	private String billerName;
	private String crnCheckDigitRoutine;
	private String crnFixedDigitsMask;
	private String crnValidLengths;
	private CRNType crnType;

	public BpayBiller()
	{}

	public BpayBiller(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getBillerCode()
	{
		return billerCode;
	}

	public void setBillerCode(String billerCode)
	{
		this.billerCode = billerCode;
	}

	public String getBillerName()
	{
		return billerName;
	}

	public void setBillerName(String billerName)
	{
		this.billerName = billerName;
	}

	public String getCrnCheckDigitRoutine()
	{
		return crnCheckDigitRoutine;
	}

	public void setCrnCheckDigitRoutine(String crnCheckDigitRoutine)
	{
		this.crnCheckDigitRoutine = crnCheckDigitRoutine;
	}

	public String getCrnFixedDigitsMask()
	{
		return crnFixedDigitsMask;
	}

	public void setCrnFixedDigitsMask(String crnFixedDigitsMask)
	{
		this.crnFixedDigitsMask = crnFixedDigitsMask;
	}

	public String getCrnValidLengths()
	{
		return crnValidLengths;
	}

	public void setCrnValidLengths(String crnValidLengths)
	{
		this.crnValidLengths = crnValidLengths;
	}

	public CRNType getCrnType()
	{
		return crnType;
	}

	public void setCrnType(CRNType crnType)
	{
		this.crnType = crnType;
	}

}
