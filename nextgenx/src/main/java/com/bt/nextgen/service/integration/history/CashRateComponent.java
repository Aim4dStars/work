package com.bt.nextgen.service.integration.history;

import java.math.BigDecimal;
import java.util.List;

public interface CashRateComponent 
{
	public String getCashRateComponentId();
	public String getCashRateComponentName();
	public List<InterestDate> getInterestDates();
	public BigDecimal getSummatedRate();
}
