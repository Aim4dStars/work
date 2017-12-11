package com.bt.nextgen.payments.domain;

public class Bsb {
	private String bsbCode;
	public Bsb()
	{
	}
	public Bsb(String bsbCode)
	{
		this.bsbCode = bsbCode;
	}

	public String getBsbCode()
	{
		return bsbCode;
	}

	public void setBsbCode(String bsbCode)
	{
		this.bsbCode = bsbCode;
	}

}
