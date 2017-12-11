package com.bt.nextgen.service.integration.history;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.service.integration.asset.AssetKey;



public interface CashReport 
{
	public AssetKey getAssetKey();
	public String getAssetName();
	public String getComponentTypeId();
	public String getComponentTypeName();
	public CashRateComponent getBaseCashRateComponent();
	public CashRateComponent getMarginCashRateComponent();
	public CashRateComponent getSpecialCashRateComponent();
	public BigDecimal getBaseRate();
	public BigDecimal getCurrentRate();
	List<InterestDate> getInterestRates();
}
