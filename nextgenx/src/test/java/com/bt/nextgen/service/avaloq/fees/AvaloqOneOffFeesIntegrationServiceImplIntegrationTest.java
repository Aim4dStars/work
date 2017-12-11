package com.bt.nextgen.service.avaloq.fees;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.fees.OneOffFees;
import com.bt.nextgen.service.integration.fees.OneOffFeesIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public class AvaloqOneOffFeesIntegrationServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Value("${accountId}")
	protected String accountId;

	@Autowired
	OneOffFeesIntegrationService feesIntegrationService;
	ServiceErrors serviceErrors;
	OneOffFeesImpl oneOffFeesReq;

	@Before
	public void setup()
	{
		serviceErrors = new ServiceErrorsImpl();
		oneOffFeesReq = new OneOffFeesImpl();
		oneOffFeesReq.setAccountKey(AccountKey.valueOf(accountId));
		oneOffFeesReq.setDescription("Test Description");
		oneOffFeesReq.setFees(new BigDecimal(150));
	}

	@Test
	public void testGetChargedFees_whenValidResponse_thenObjectCreatedAndNoServiceErrors()
	{
		OneOffFees feesInterface = feesIntegrationService.getChargedFees(AccountKey.valueOf(accountId), serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(feesInterface);
	}

	@Test
	public void testValidateAdviceFees_whenValidResponse_thenObjectCreatedAndNoServiceErrors()
	{
		OneOffFees feesInterface = feesIntegrationService.validateAdviceFees(oneOffFeesReq, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(feesInterface);

	}

	@Test
	public void testSubmitAdviceFees_whenValidResponse_thenObjectCreatedAndNoServiceErrors()
	{
		OneOffFees feesInterface = feesIntegrationService.submitAdviceFees(oneOffFeesReq, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(feesInterface);
	}
}
