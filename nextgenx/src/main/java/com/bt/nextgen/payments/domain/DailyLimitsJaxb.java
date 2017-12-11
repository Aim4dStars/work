package com.bt.nextgen.payments.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DailyLimitsJaxb
{
	private List<String> payanyoneLimits;
	private List<String> bpayLimits;

	public List<String> getPayanyoneLimits()
	{
		return payanyoneLimits;
	}
	public void setPayanyoneLimits(List<String> payanyoneLimits)
	{
		this.payanyoneLimits = payanyoneLimits;
	}
	public List<String> getBpayLimits()
	{
		return bpayLimits;
	}
	public void setBpayLimits(List<String> bpayLimits)
	{
		this.bpayLimits = bpayLimits;
	}

}
