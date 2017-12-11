package com.bt.nextgen.api.order.model;

import java.math.BigDecimal;

import com.bt.nextgen.core.api.model.BaseDto;

public class OrderInProgressDto extends BaseDto
{
	private final String orderType;
	private final BigDecimal amount;

	public OrderInProgressDto(String orderType, BigDecimal amount)
	{
		super();
		this.orderType = orderType;
		this.amount = amount;
	}

	public String getOrderType()
	{
		return orderType;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}
}
