package com.bt.nextgen.service.avaloq.order;

import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.order.OrderInProgress;
import com.bt.nextgen.service.integration.order.OrderInProgressResponse;

@ServiceBean(xpath = "/")
public class OrderInProgressResponseImpl extends AvaloqBaseResponseImpl implements OrderInProgressResponse
{
	@ServiceElementList(xpath = "//data/bp_list/bp/cont_list/cont/pos_list/pos/evt_list/evt", type = OrderInProgressImpl.class)
	private List <OrderInProgress> orders;

	@Override
	public List <OrderInProgress> getOrders()
	{
		return orders;
	}
}
