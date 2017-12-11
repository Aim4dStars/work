package com.bt.nextgen.service.avaloq.order;

import com.btfin.panorama.core.security.integration.order.OrderIdentifier;

public class OrderIdentifierImpl implements OrderIdentifier
{

	private String orderId;

	public String getOrderId()
	{
		return orderId;
	}

	public void setOrderId(String orderId)
	{
		this.orderId = orderId;
	}

}
