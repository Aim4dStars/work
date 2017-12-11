package com.bt.nextgen.payments.web.model;

public class SafiPaymentModel 
{
	private boolean analyze;
	private String payeeId;
	private long startTime;
	
	public boolean isAnalyze()
	{
		return analyze;
	}
	public void setAnalyze(boolean analyze)
	{
		this.analyze = analyze;
	}
	public String getPayeeId()
	{
		return payeeId;
	}
	public void setPayeeId(String payeeId)
	{
		this.payeeId = payeeId;
	}
	public long getStartTime() 
	{
		return startTime;
	}
	public void setStartTime(long startTime) 
	{
		this.startTime = startTime;
	}
}
