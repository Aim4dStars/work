package com.bt.nextgen.payments.web.model;

public class BpayBillerModel
{
	public BpayBillerModel(String billerCode, String billerName)
	{
		this.billerCode = billerCode;
		this.billerName = billerName;
	}

	public final String billerCode;
	public final String billerName;

}
