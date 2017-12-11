package com.bt.nextgen.service.avaloq.order;

import java.math.BigDecimal;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.order.OrderInProgress;
import com.bt.nextgen.service.integration.order.OrderType;

@ServiceBean(xpath = "evt", type = ServiceBeanType.CONCRETE)
public class OrderInProgressImpl implements OrderInProgress
{
	private static final String XML_HEADER = "evt_head_list/evt_head/";

	@ServiceElement(xpath = XML_HEADER + "order_type_id/val", staticCodeCategory = "ORDER_TYPE")
	private OrderType orderType;

	@ServiceElement(xpath = XML_HEADER + "qty/val")
	private BigDecimal amount;

	@Override
	public OrderType getOrderType()
	{
		return orderType;
	}

	@Override
	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setOrderType(OrderType orderType)
	{
		this.orderType = orderType;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}
}
