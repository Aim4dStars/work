package com.bt.nextgen.service.integration.order;

import java.math.BigDecimal;

public interface OrderInProgress
{
	public OrderType getOrderType();

	public BigDecimal getAmount();
}
