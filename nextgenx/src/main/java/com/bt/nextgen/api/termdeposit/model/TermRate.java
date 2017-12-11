package com.bt.nextgen.api.termdeposit.model;

import java.math.BigDecimal;

public class TermRate 
{
	//asset_irc: Tier Id of asset
    private String assetIrcId;
	//intr_rate: Interest rate
    private BigDecimal rate;
    //qty_from
    private BigDecimal minInvest;
    //qty_to
    private BigDecimal maxInvest;
    
    /**
     *  Not used
     */
    
    //is_spec: Flag to check whether rate is special or not. 
    private boolean isSpecial;
    //stores whether it is SPECIAL or NON_SPECIAL rate
    private String rateType;
    //asset_irc: Tier Value of asset
    private String assetIrcName;
    //prio: priority of asset
    private long priority;
    
	public BigDecimal getRate()
	{
		return rate;
	}
	public void setRate(BigDecimal rate)
	{
		this.rate = rate;
	}
	public BigDecimal getMinInvest()
	{
		return minInvest;
	}
	public void setMinInvest(BigDecimal minInvest)
	{
		this.minInvest = minInvest;
	}
	public BigDecimal getMaxInvest()
	{
		return maxInvest;
	}
	public void setMaxInvest(BigDecimal maxInvest)
	{
		this.maxInvest = maxInvest;
	}
	public boolean isSpecial()
	{
		return isSpecial;
	}
	public void setSpecial(boolean isSpecial)
	{
		this.isSpecial = isSpecial;
	}
	public String getRateType()
	{
		return rateType;
	}
	public void setRateType(String rateType)
	{
		this.rateType = rateType;
	}
	public String getAssetIrcId()
	{
		return assetIrcId;
	}
	public void setAssetIrcId(String assetIrcId)
	{
		this.assetIrcId = assetIrcId;
	}
	public String getAssetIrcName()
	{
		return assetIrcName;
	}
	public void setAssetIrcName(String assetIrcName)
	{
		this.assetIrcName = assetIrcName;
	}
	public long getPriority()
	{
		return priority;
	}
	public void setPriority(long priority)
	{
		this.priority = priority;
	}
}
