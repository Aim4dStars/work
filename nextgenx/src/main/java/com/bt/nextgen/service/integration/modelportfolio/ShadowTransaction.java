package com.bt.nextgen.service.integration.modelportfolio;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ShadowTransaction
{
	public String getTransactionId();

	public String getTransactionType();

	public String getAssetId();

	public String getAssetHolding();

	public String getStatus();

	public DateTime getTradeDate();

	public DateTime getValueDate();

	public DateTime getPerformanceDate();

	public BigDecimal getAmount();

    public BigDecimal getUnitPrice();

	public String getDescription();
}
