package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.btfin.panorama.core.validation.Validator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class WrapAccountValuationImplIntegrationTest extends BaseSecureIntegrationTest
{
	@Autowired
	private Validator validator;

	@Before
	public void setup()
	{}

	@Test
	public void testValidation_whenAccountIdIsNull_thenServiceErrors()
	{
		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountName("accountName");
		account.setOpenDate(new DateTime());
		account.setAccountStructureType(AccountStructureType.Individual);
		account.setAdviserPersonId(ClientKey.valueOf("clientId"));
		account.setSignDate(new DateTime());
		account.setAdviserKey(BrokerKey.valueOf("brokerId"));

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(account, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("accountKey may not be null", serviceErrors.getErrorList().iterator().next().getReason());
	}

	@Test
	public void testValidation_whenAccountNameIsNull_thenServiceErrors()
	{
		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountKey(AccountKey.valueOf("accountId"));
		account.setOpenDate(new DateTime());
		account.setAccountStructureType(AccountStructureType.Individual);
		account.setAdviserPersonId(ClientKey.valueOf("clientId"));
		account.setSignDate(new DateTime());
		account.setAdviserKey(BrokerKey.valueOf("brokerId"));

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(account, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("accountName may not be null", serviceErrors.getErrorList().iterator().next().getReason());

	}

	@Test
	public void testValidation_whenOpenDateIsEmpty_thenServiceErrors()
	{
		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountKey(AccountKey.valueOf("accountId"));
		account.setAccountName("accountName");
		account.setAccountStructureType(AccountStructureType.Individual);
		account.setAdviserPersonId(ClientKey.valueOf("clientId"));
		account.setSignDate(new DateTime());
		account.setAdviserKey(BrokerKey.valueOf("brokerId"));

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(account, serviceErrors);
		Assert.assertTrue(serviceErrors.hasErrors());
		Assert.assertEquals("openDate may not be null", serviceErrors.getErrorList().iterator().next().getReason());

	}

	@Test
	public void testValidation_whenValid_thenNoServiceError()
	{
		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountKey(AccountKey.valueOf("accountId"));
		account.setAccountName("accountName");
		account.setOpenDate(new DateTime());
		account.setAccountStructureType(AccountStructureType.Individual);
		account.setAdviserPersonId(ClientKey.valueOf("clientId"));
		account.setAdviserKey(BrokerKey.valueOf("brokerId"));
		account.setSignDate(new DateTime());

		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		validator.validate(account, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
	}
}
