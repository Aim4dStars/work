package com.bt.nextgen.service.avaloq.order;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderResponse;

@ServiceBean(xpath = "/")
public class OrderResponseImpl extends AvaloqBaseResponseImpl implements OrderResponse
{
	@ServiceElementList(xpath = "//data/doc_list/doc", type = OrderImpl.class)
	private List <Order> orders;

	@Override
	public List <Order> getOrders()
	{
		return orders;
	}
}
