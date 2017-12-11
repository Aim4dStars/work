package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AvaloqModelPortfolioSummaryIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	ModelPortfolioSummaryIntegrationService avaloqModelIntegrationService;

	@Test
	public void testLoadModels_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<ModelPortfolioSummary> models;
        models = avaloqModelIntegrationService.loadModels(BrokerKey.valueOf("73332"), serviceErrors);
        Assert.assertNotNull(models.get(0));
        Assert.assertFalse(serviceErrors.hasErrors());
	}
}
