package com.bt.nextgen.service.avaloq.dashboard;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.avaloq.abs.screen_rep.hira.btfg$ui_perf_list_prd_prddet_avsr_dshbrd_pv_band.Rep;
import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioValueByBandConverterTest
{
	@InjectMocks
	PortfolioValueByBandConverter converter = new PortfolioValueByBandConverter();

	@Test
	public void testToModelPortfolioValueByBand_whenSuppliedWithValidResponse_thenObjectCreatedWithNoServiceErrors()
		throws Exception
	{
		Rep report = JaxbUtil.unmarshall("/webservices/response/AdviserDashboardPortfolioValueByBand_UT.xml", Rep.class);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();

		PortfolioValueByBandImpl portfolioValueByBand = converter.toModel(report, serviceErrors);
		Assert.assertNotNull(portfolioValueByBand);
		Assert.assertEquals(BigDecimal.valueOf(261179049.96), portfolioValueByBand.getPortfolioValue());
	}
}
