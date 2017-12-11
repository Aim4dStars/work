package com.bt.nextgen.service.avaloq.cgt;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.cgt.CgtIntegrationService;
import com.bt.nextgen.service.integration.cgt.WrapCgtData;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class AvaloqCgtIntegrationServiceImplTest extends BaseSecureIntegrationTest
{
	@Value("${accountId}")
	protected String accountId;

	@Autowired
	CgtIntegrationService cgtIntegrationService;

	@Test
	public void testLoadRealisedCgtDetails_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		//AccountKey accKey = AccountKey.valueOf(accountId);
		DateTime endDate = new DateTime();
		DateTime startDate = endDate.minusMonths(1);
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		WrapCgtData cgtData = cgtIntegrationService.loadRealisedCgtDetails(accountId, startDate, endDate, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(cgtData);
	}

	@Test
	public void testLoadUnrealisedCgtDetails_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		//AccountKey accKey = AccountKey.valueOf(accountId);
		DateTime effDate = new DateTime();
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		WrapCgtData cgtData = cgtIntegrationService.loadUnrealisedCgtDetails(accountId, effDate, serviceErrors);

		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(cgtData);
	}

}
