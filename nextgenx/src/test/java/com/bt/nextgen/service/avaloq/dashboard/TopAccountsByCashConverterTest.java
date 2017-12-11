package com.bt.nextgen.service.avaloq.dashboard;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_bp_top_bp_cash.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class TopAccountsByCashConverterTest
{
	@InjectMocks
	TopAccountsByCashConverter converter = new TopAccountsByCashConverter();

	@Test
	public void testToModelTopAccountsByCash_whenSuppliedWithValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/DashboardTopBpAccountsByCash_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		List <TopAccountsByValueImpl> topAccounts = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(topAccounts);
		TopAccountsByValueImpl topAccount = topAccounts.get(0);
		Assert.assertEquals(BigDecimal.valueOf(20), topAccount.getCashValue());
	}
}
