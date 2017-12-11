package com.bt.nextgen.service.avaloq.order;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.Origin;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.order.Order;
import com.bt.nextgen.service.integration.order.OrderStatus;
import com.bt.nextgen.service.integration.order.OrderType;

@RunWith(MockitoJUnitRunner.class)
public class OrderConverterTest
{
	@InjectMocks
	OrderConverter orderConverter = new OrderConverter();

	@Mock
	private StaticIntegrationService staticService;

	@Before
	public void setup()
	{
		Mockito.when(staticService.loadCode(Mockito.any(CodeCategory.class),
			Mockito.anyString(),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <Object>()
		{
			@Override
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				if (CodeCategory.ORDER_TYPE.equals(args[0]) && "780".equals(args[1]))
				{
					return new CodeImpl("780", "PLACE", "Purchase", "fidd_place");
				}
				else if (CodeCategory.ORDER_STATUS.equals(args[0]) && "10".equals(args[1]))
				{
					return new CodeImpl("10", "IN_PROGRESS", "In progress", "in_progress");
				}
				else if (CodeCategory.MEDIUM.equals(args[0]) && "2012".equals(args[1]))
				{
					return new CodeImpl("2012", "BO", "Back Office", "btfg$bo");
				}
				else
				{
					return null;
				}
			}
		});
	}

	@Test
	public void testToModelOrder_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Rep report = JaxbUtil.unmarshall("/webservices/response/OrderLoadSingleResponseV1_UT.xml",
			com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <Order> result = orderConverter.toOrderLoadRequest(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(5, result.size());
		Order order = result.get(0);
		Assert.assertEquals("65020", order.getOrderId());
		Assert.assertEquals("65021", order.getDisplayOrderId());
		DateTime dateTime = DateTime.parse("2014-06-10T06:22:20+10:00", ISODateTimeFormat.dateTimeParser());
		Assert.assertEquals(dateTime.getMillis(), order.getCreateDate().getMillis());
		Assert.assertEquals(OrderType.PURCHASE, order.getOrderType());
		Assert.assertEquals(Origin.BACK_OFFICE, order.getOrigin());
		Assert.assertEquals("36846", order.getAccountId());
		Assert.assertEquals("28126", order.getAssetId());
		Assert.assertEquals(BigDecimal.valueOf(-450000), order.getAmount());
		Assert.assertEquals(OrderStatus.IN_PROGRESS, order.getStatus());
		Assert.assertFalse(order.getCancellable());
	}

	@Test
	public void testToModelOrder_whenSuppliedWithEmptyResponse_thenEmptyResultAndNoServiceErrors() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Rep report = JaxbUtil.unmarshall("/webservices/response/OrderLoadSingleEmptyResponse_UT.xml",
			com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_bp_cont_pos_evt.Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <Order> result = orderConverter.toOrderLoadRequest(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}

}
