package com.bt.nextgen.service.security;

import com.bt.nextgen.clients.web.model.ClientModel;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.login.web.model.AccountStatusModel;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class InvestorRepositoryIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	InvestorService investorService;

	@Test
	public void testActivationStatus() throws Exception
	{
		String userId = "1234";
		String accountId = "123";
		AccountStatusModel accountStatusModel = investorService.activationStatus(userId, accountId);

		assertThat(accountStatusModel.getAdviserId(), notNullValue());
		assertThat(accountStatusModel.getAccountType(), notNullValue());
		assertThat(accountStatusModel.getAccountDescription(), notNullValue());
		assertThat(accountStatusModel.getEmail(), notNullValue());
		assertThat(accountStatusModel.getInitiatedDate(), notNullValue());
		assertThat(accountStatusModel.getPhone(), notNullValue());

		assertThat(accountStatusModel.getClientList(), notNullValue());
		for (ClientModel client : accountStatusModel.getClientList())
		{
			assertThat(client.getSalutation(), notNullValue());
			assertThat(client.getFirstName(), notNullValue());
			assertThat(client.getLastName(), notNullValue());
			assertThat(client.getPhone(), notNullValue());
			assertThat(client.getEmail(), notNullValue());
			assertThat(client.getActivationStatus(), notNullValue());
		}
	}

	@Test
	public void testActivateAccount() throws Exception
	{
		String userId = "f";
		boolean result = investorService.activateAccount(userId);
		assertEquals(true, result);
	}

	@Test
	public void testIsExistingInvestor() throws Exception
	{
		String usercode = "Test";
		boolean result = investorService.isExistingInvestor(usercode);
		assertEquals(true, result);
	}

	@Test
	public void testIsValidPassword() throws Exception
	{
		String password = "password";
		boolean result = investorService.isValidPassword(password);
		assertEquals(true, result);
	}

	@Test
	public void testSendSmsCode() throws Exception
	{
		String registrationCode = "1";
		String lastName = "Test";
		String postcode = "1111";

		boolean result = investorService.sendSmsCode(registrationCode, lastName, postcode);
		assertEquals(true, result);
	}

	@Test
	public void testCreateInvestor() throws Exception
	{
		String resultExpected = ("MTIzNDU2Nw==");
		String usercode = "1";
		String password = "password";
		String result = investorService.createInvestor(usercode, password);
		assertEquals(resultExpected, result);
	}

}
