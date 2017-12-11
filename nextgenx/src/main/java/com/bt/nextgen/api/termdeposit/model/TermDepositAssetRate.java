package com.bt.nextgen.api.termdeposit.model;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.service.integration.asset.Term;

public interface TermDepositAssetRate
{
	List<TermRate> getRatePool(); 
	void setRatePool(List<TermRate> termRate); 
	String getAssetId();
	void setAssetId(String assetId); 
	String getPaymentFrequency();
	void setPaymentFrequency(String paymentFrequency);
	
	/**
	 * Not Used
	 */
	String getId();
	void setId(String id);
	Brand getBrand();
	void setBrand(Brand brand);
	Term getTerm();
	void setTerm(Term term);
	String getDealerGroup();
	void setDealerGroup(String dealerGroup);
	TermRate getTermRate(); 
	void setTermRate(TermRate termRate); 
	String getAssetName();
	void setAssetName(String assetName);
	BigDecimal getYearlyRate();
	BigDecimal getMonthlyRate();
	String getMonthlyAssetIrcId(); 
	String getYearlyAssetIrcId(); 
	List<String> getDealerGroupIds();
	void setDealerGroupIds(List<String> dealerGroupIds);
}
