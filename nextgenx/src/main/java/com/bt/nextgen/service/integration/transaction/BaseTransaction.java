package com.bt.nextgen.service.integration.transaction;

import com.bt.nextgen.service.integration.order.OrderType;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface BaseTransaction {
	public String getKey();

	public String getAccountId();

	public String getDocId();

	public String getOrderType();

	OrderType getOrderTypeCode();

	public String getTransactionId();

	public String getDescription();

	public BigDecimal getNetAmount();

	public DateTime getEffectiveDate();

	public DateTime getValDate();

	public void setValDate(DateTime valDate);

	public String getStatus();
}