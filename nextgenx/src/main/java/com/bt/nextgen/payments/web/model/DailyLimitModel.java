package com.bt.nextgen.payments.web.model;

import java.util.List;

public class DailyLimitModel
{
	private String  payAnyoneLimit;
    private String  bpayLimit;
    private String  linkedLimit;
	private String  remainingPayAnyoneLimit = "0.000000";
    private String  remainingBpayLimit = "0.000000";
    private String  remainingLinkedLimit = "0.000000";
    private List<String> bpayLimits;
    private List<String> linkedLimits;
	private List<String> payAnyoneLimits;
	private String errorMessage;
	private String maxLimit;
	private boolean analyze;

    public String getLinkedLimit() {
        return linkedLimit;
    }

    public void setLinkedLimit(String linkedLimit) {
        this.linkedLimit = linkedLimit;
    }

    public String getRemainingLinkedLimit() {
        return remainingLinkedLimit;
    }

    public void setRemainingLinkedLimit(String remainingLinkedLimit) {
        this.remainingLinkedLimit = remainingLinkedLimit;
    }

    public List<String> getLinkedLimits() {
        return linkedLimits;
    }

    public void setLinkedLimits(List<String> linkedLimits) {
        this.linkedLimits = linkedLimits;
    }

    public String getPayAnyoneLimit()
	{
		return payAnyoneLimit;
	}
	public void setPayAnyoneLimit(String payAnyoneLimit) 
	{
		this.payAnyoneLimit = payAnyoneLimit;
	}
	public String getRemainingPayAnyoneLimit()
	{
		return remainingPayAnyoneLimit;
	}
	public void setRemainingPayAnyoneLimit(String remainingPayAnyoneLimit)
	{
		this.remainingPayAnyoneLimit = remainingPayAnyoneLimit;
	}
	public List<String> getPayAnyoneLimits() 
	{
		return payAnyoneLimits;
	}
	public void setPayAnyoneLimits(List<String> payAnyoneLimits)
	{
		this.payAnyoneLimits = payAnyoneLimits;
	}
	public String getBpayLimit()
	{
		return bpayLimit;
	}
	public void setBpayLimit(String bpayLimit)
	{
		this.bpayLimit = bpayLimit;
	}
	public String getRemainingBpayLimit()
	{
		return remainingBpayLimit;
	}
	public void setRemainingBpayLimit(String remainingBpayLimit) 
	{
		this.remainingBpayLimit = remainingBpayLimit;
	}
	public List<String> getBpayLimits()
	{
		return bpayLimits;
	}
	public void setBpayLimits(List<String> bpayLimits)
	{
		this.bpayLimits = bpayLimits;
	}
	public String getErrorMessage() 
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) 
	{
		this.errorMessage = errorMessage;
	}
	public String getMaxLimit() 
	{
		return maxLimit;
	}
	public void setMaxLimit(String maxLimit) 
	{
		this.maxLimit = maxLimit;
	}

	public boolean isAnalyze() {
		return analyze;
	}

	public void setAnalyze(boolean analyze) {
		this.analyze = analyze;
	}
	
	
	
}
