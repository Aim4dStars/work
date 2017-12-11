package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public class AvaloqModelPortfolioIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	ModelPortfolioIntegrationService avaloqModelIntegrationService;

	@Test
	public void testLoadModels_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		BrokerKey investmentManagerKey = BrokerKey.valueOf("73127");
		Collection <ModelPortfolio> models = avaloqModelIntegrationService.loadModels(investmentManagerKey, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(models.iterator().next());
	}

	@Test
	public void testLoadModel_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
        IpsKey modelId = IpsKey.valueOf("77172");
		ModelPortfolio model = avaloqModelIntegrationService.loadModel(modelId, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(model);
	}
}
