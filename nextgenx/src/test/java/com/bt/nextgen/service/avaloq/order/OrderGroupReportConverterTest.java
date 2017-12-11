package com.bt.nextgen.service.avaloq.order;

import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.avaloq.abs.screen_rep.hira.btfg$ui_doc_list_doc.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class OrderGroupReportConverterTest
{
	OrderGroupReportConverter orderGroupConverter = new OrderGroupReportConverter();

	@Before
	public void setup()
	{}

	@Test
	public void testToModelOrderGroup_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/OrderGroupLoadResponse_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		List <OrderGroupImpl> result = orderGroupConverter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		OrderGroupImpl orderGroup = result.get(0);
		Assert.assertEquals("1234", orderGroup.getOrderGroupId());
		Assert.assertEquals("Bob's Transaction", orderGroup.getReference());
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
		Assert.assertEquals("2014-05-28", formatter.print(orderGroup.getLastUpdateDate()));
		Assert.assertEquals("gilbyr", orderGroup.getOwnerName());
		Assert.assertEquals("0", orderGroup.getOrderType());
	}

	@Test
	public void testToModelOrderGroup_whenSuppliedWithEmptyResponse_thenEmptyResultAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/OrderGroupLoadEmptyResponse_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <OrderGroupImpl> result = orderGroupConverter.toModel(report, serviceErrors);
		Assert.assertNotNull(result);
		Assert.assertEquals(0, result.size());
	}
}
