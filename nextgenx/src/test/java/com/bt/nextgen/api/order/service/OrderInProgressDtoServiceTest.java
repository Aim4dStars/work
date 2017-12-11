package com.bt.nextgen.api.order.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.order.model.OrderInProgressDto;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.order.OrderInProgressImpl;
import com.bt.nextgen.service.integration.order.OrderInProgress;
import com.bt.nextgen.service.integration.order.OrderType;

@RunWith(MockitoJUnitRunner.class)
public class OrderInProgressDtoServiceTest
{
	@InjectMocks
	private OrderInProgressDtoServiceImpl orderInProgressDtoService;
	List <OrderInProgress> emptyOrderList = new ArrayList <OrderInProgress>();
	OrderInProgressImpl order1;
	OrderInProgressImpl order2;
	OrderInProgressImpl order3;
	OrderInProgressImpl order4;
	OrderInProgressImpl order5;
	List <OrderInProgress> orderList = new ArrayList <OrderInProgress>();

	@Before
	public void setup() throws Exception
	{
		order1 = new OrderInProgressImpl();
		order1.setOrderType(OrderType.PURCHASE);
		order1.setAmount(new BigDecimal("2000"));
		order2 = new OrderInProgressImpl();
		order2.setOrderType(OrderType.PARTIAL_REDEMPTION);
		order2.setAmount(new BigDecimal("-3000"));
		order3 = new OrderInProgressImpl();
		order3.setOrderType(OrderType.INCREASE);
		order3.setAmount(new BigDecimal("5000"));
		order4 = new OrderInProgressImpl();
		order4.setOrderType(OrderType.FULL_REDEMPTION);
		order4.setAmount(new BigDecimal("-5000"));
		order5 = new OrderInProgressImpl();
		order5.setOrderType(null);
		order5.setAmount(new BigDecimal("-1000"));
		orderList.add(order1);
		orderList.add(order2);
		orderList.add(order3);
		orderList.add(order4);
		orderList.add(order5);

	}

	@Test
	public void testToOrderDto_orderListEmpty()
	{
		List <OrderInProgressDto> managedOrder = orderInProgressDtoService.toOrderInProgressDto(emptyOrderList,
			new ServiceErrorsImpl());
		Assert.assertEquals(2, managedOrder.size());
	}

	@Test
	public void testToOrderDto()
	{
		List <OrderInProgressDto> managedOrder = orderInProgressDtoService.toOrderInProgressDto(orderList,
			new ServiceErrorsImpl());
		Assert.assertEquals(2, managedOrder.size());
		assertEquals(-7000, managedOrder.get(0).getAmount().intValue());
		assertEquals(8000, managedOrder.get(1).getAmount().intValue());
	}
}
