package com.bt.nextgen.api.termdeposit.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;

public class TermDepositAssetRateImpl implements TermDepositAssetRate
{
	private static final BigDecimal BIGDECIMAL_DEFAULT = new BigDecimal("0.0");
	  private static final Logger logger = LoggerFactory.getLogger(TermDepositAssetRate.class);
	 
	private String id;
    private Brand brand;
    //period: Term Deposit period
    private Term term;
    //intr_pay_freq: Interest payment frequency
    private String paymentFrequency;
    //stores the dealerGroup name to which the td rate belongs to
    private String dealerGroup;
    //stores the dealerGroup id's attached to asset
    private List<String> dealerGroupIds;
    private TermRate termRate;
    private List<TermRate> ratePool;
    //is_spec: Flag to check whether rate is special or not. 
    private boolean isSpecial;
    //stores whether it is SPECIAL or NON_SPECIAL rate
    private String rateType;
  //asset: Asset Id
    private String assetId;
    //asset: Asset descriptive value
    private String assetName;
    
    private String monthlyAssetIrcId;
    private String yearlyAssetIrcId;
    private String maturityAssetIrcId;
    
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public Term getTerm()
	{
		return term;
	}
	public void setTerm(Term term)
	{
		this.term = term;
	}
	public String getPaymentFrequency()
	{
		return paymentFrequency;
	}
	public void setPaymentFrequency(String paymentFrequency)
	{
		this.paymentFrequency = paymentFrequency;
	}
	public String getDealerGroup()
	{
		return dealerGroup;
	}
	public void setDealerGroup(String dealerGroup)
	{
		this.dealerGroup = dealerGroup;
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
	public TermRate getTermRate() 
	{
		return termRate;
	}
	public void setTermRate(TermRate termRate) 
	{
		this.termRate = termRate;
	}
	public List<TermRate> getRatePool() 
	{
		return ratePool;
	}
	public void setRatePool(List<TermRate> ratePool) 
	{
		this.ratePool = ratePool;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) 
	{
		this.assetId = assetId;
	}
	public String getAssetName() 
	{
		return assetName;
	}
	public void setAssetName(String assetName) 
	{
		this.assetName = assetName;
	}

	/**
	 * Method to return the priority list for the particular asset in the price range.
	 */
	public List<BigDecimal> getPriorityList() 
	{
		List<BigDecimal> priorityList = new ArrayList<>();
		for(TermRate termRate : ratePool)
		{
			priorityList.add(new BigDecimal(termRate.getPriority()));
		}
		return priorityList;
	}
	
	public BigDecimal getYearlyRate() 
	{
		for(TermRate termRate : ratePool)
		{
			if(new BigDecimal(termRate.getPriority()).compareTo(getTopPriority()) == 0 && (getPaymentFrequency().equalsIgnoreCase(PaymentFrequency.AT_MATURITY.toString()) || getPaymentFrequency().equalsIgnoreCase(PaymentFrequency.ANNUALLY.toString())))
			{
				logger.info("Yearly Rate picked having assetIrcId:{}, Priority:{}, Rate:{}, RateSpecial:{}", termRate.getAssetIrcId(), termRate.getPriority(), termRate.getRate(), termRate.isSpecial());
				setYearlyAssetIrcId(termRate.getAssetIrcId());
				return termRate.getRate();
			}
		}
		return BIGDECIMAL_DEFAULT;
	}
	
	public BigDecimal getMonthlyRate() 
	{
		for(TermRate termRate : ratePool)
		{
			if(new BigDecimal(termRate.getPriority()).compareTo(getTopPriority()) == 0 && getPaymentFrequency().equalsIgnoreCase(PaymentFrequency.MONTHLY.toString()))
			{
				logger.info("Motnhly Rate picked having assetIrcId:{}, Priority:{}, Rate:{}, RateSpecial:{}", termRate.getAssetIrcId(), termRate.getPriority(), termRate.getRate(), termRate.isSpecial());
				setMonthlyAssetIrcId(termRate.getAssetIrcId());
				return termRate.getRate();
			}
		}
		return BIGDECIMAL_DEFAULT;
	}
	
	public BigDecimal getTopPriority() 
	{
		return Collections.max(getPriorityList());
	}
	public String getMonthlyAssetIrcId() 
	{
		return monthlyAssetIrcId;
	}
	public void setMonthlyAssetIrcId(String monthlyAssetIrcId) 
	{
		this.monthlyAssetIrcId = monthlyAssetIrcId;
	}
	public String getYearlyAssetIrcId() 
	{
		return yearlyAssetIrcId;
	}
	public void setYearlyAssetIrcId(String yearlyAssetIrcId) 
	{
		this.yearlyAssetIrcId = yearlyAssetIrcId;
	}
	public String getMaturityAssetIrcId() 
	{
		return maturityAssetIrcId;
	}
	public void setMaturityAssetIrcId(String maturityAssetIrcId) 
	{
		this.maturityAssetIrcId = maturityAssetIrcId;
	}
	public List<String> getDealerGroupIds() 
	{
		return dealerGroupIds;
	}
	public void setDealerGroupIds(List<String> dealerGroupIds) 
	{
		this.dealerGroupIds = dealerGroupIds;
	}
	public Brand getBrand() 
	{
		return brand;
	}
	public void setBrand(Brand brand) 
	{
		this.brand = brand;
	}
}
