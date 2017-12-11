package com.bt.nextgen.service.integration.history;

import java.math.BigDecimal;

import org.joda.time.DateTime;

public interface InterestDate
{
	public DateTime getEffectiveDate();
	public BigDecimal getInterestRate();
}
