package com.bt.nextgen.api.account.v2.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.bt.nextgen.api.account.v2.service.BglDtoServiceImpl;

import com.bt.nextgen.api.account.v2.model.BglDataDto;
import com.bt.nextgen.api.account.v2.model.DateRangeAccountKey;

import java.io.IOException;
import java.util.Scanner;

import org.joda.time.DateTime;
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
import com.bt.nextgen.clients.domain.AccountType;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class BglDtoServiceTest
{
	@InjectMocks
	private BglDtoServiceImpl bglDtoService;

	@Mock
	private AccountIntegrationService accountIntegrationService;

	@Mock
	private BankDateIntegrationService bankDateService;

	private DateRangeAccountKey dateRangeKey = new DateRangeAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
		new DateTime().minusMonths(3),
		new DateTime());

	private static final String BGL_DATA = "BglData";

	@Before
	public void setup() throws Exception
	{
		DateTime bankDate = new DateTime("2020-01-01");
		Mockito.when(bankDateService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(bankDate);
	}

	@Test
	public void testBglDtoServiceImpl_whenBglSearched_thenStreamDataContainsMatchingString() throws IOException
	{
		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));
		account.setOpenDate(new DateTime("2000-01-01"));
		Assert.assertEquals(account.getAccountStructureType().name(), AccountType.SMSF.getName());
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(account);

		Mockito.when(accountIntegrationService.loadAccountBglData(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenReturn(BGL_DATA);

		BglDataDto bgl = bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());
		assertNotNull(bgl);
		String bglData = new Scanner(bgl.getStream(), "UTF-8").useDelimiter("\\A").next();
		Assert.assertEquals(BGL_DATA, bglData);
	}

	@Test
	public void testBglDtoServiceImpl_whenBglSearchedWithNullAccountType_thenReturnNull() throws IOException
	{
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(null);
		BglDataDto bgl = bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());
		assertNull(bgl);
	}

	@Test
	public void testBglDtoServiceImpl_whenBglSearchedWithNoSMSFAccountType_thenReturnNull() throws IOException
	{
		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("I"));
		account.setOpenDate(new DateTime("2000-01-01"));
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(account);

		BglDataDto bgl = bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());
		assertNull(bgl);
	}

	@Test
	public void testBglDtoServiceImpl_whenSearchRangeInsideAccountBoundaries_searchForRequested() throws IOException
	{
		final DateTime accountOpenDate = new DateTime("2000-01-01");
		final DateTime startDate = accountOpenDate.plusDays(1);
		final DateTime endDate = accountOpenDate.plusDays(10);

		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));
		account.setOpenDate(accountOpenDate);
		Assert.assertEquals(account.getAccountStructureType().name(), AccountType.SMSF.getName());

		DateRangeAccountKey dateRangeKey = new DateRangeAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
			startDate,
			endDate);

		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(account);

		Mockito.when(accountIntegrationService.loadAccountBglData(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <String>()
		{
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable
			{
				Assert.assertEquals(startDate, invocation.getArguments()[1]);
				Assert.assertEquals(endDate, invocation.getArguments()[2]);
				return BGL_DATA;
			}

		});
		bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());
	}

	@Test
	public void testBglDtoServiceImpl_whenSearchRangeStartBeforeAccountBoundaries_searchFromAccountOpenDate() throws IOException
	{
		final DateTime accountOpenDate = new DateTime("2000-01-01");
		final DateTime startDate = accountOpenDate.minusDays(1);
		final DateTime endDate = accountOpenDate.plusDays(10);

		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));
		account.setOpenDate(accountOpenDate);
		Assert.assertEquals(account.getAccountStructureType().name(), AccountType.SMSF.getName());

		DateRangeAccountKey dateRangeKey = new DateRangeAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
			startDate,
			endDate);

		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(account);

		Mockito.when(accountIntegrationService.loadAccountBglData(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <String>()
		{
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable
			{
				Assert.assertEquals(accountOpenDate, invocation.getArguments()[1]);
				Assert.assertEquals(endDate, invocation.getArguments()[2]);
				return BGL_DATA;
			}

		});
		bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());
	}

	@Test
	public void testBglDtoServiceImpl_whenSearchRangeEndAfterAccountBoundaries_searchForEndDateFromAccountOpenDate()
		throws IOException
	{
		final DateTime accountOpenDate = new DateTime("2000-01-01");
		final DateTime startDate = accountOpenDate.minusDays(10);
		final DateTime endDate = accountOpenDate.minusDays(1);

		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));
		account.setOpenDate(accountOpenDate);
		Assert.assertEquals(account.getAccountStructureType().name(), AccountType.SMSF.getName());

		DateRangeAccountKey dateRangeKey = new DateRangeAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
			startDate,
			endDate);

		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(account);

		Mockito.when(accountIntegrationService.loadAccountBglData(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <String>()
		{
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable
			{
				Assert.assertEquals(accountOpenDate, invocation.getArguments()[1]);
				Assert.assertEquals(accountOpenDate, invocation.getArguments()[2]);
				return BGL_DATA;
			}

		});
		bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());
	}

	@Test
	public void testBglDtoServiceImpl_whenSearchRangeEndAfterAccountClosure_searchForEndDateFromAccountCloseDate()
		throws IOException
	{
		final DateTime accountOpenDate = new DateTime("2000-01-01");
		final DateTime accountCloseDate = accountOpenDate.plusDays(10);
		final DateTime startDate = accountOpenDate.plusDays(1);
		final DateTime endDate = accountOpenDate.plusDays(20);

		WrapAccountDetailImpl account = new WrapAccountDetailImpl();
		account.setAccountStructureType(AccountStructureType.fromAvaloqStaticCode("S"));
		account.setOpenDate(accountOpenDate);
		account.setClosureDate(accountCloseDate);
		Assert.assertEquals(account.getAccountStructureType().name(), AccountType.SMSF.getName());

		DateRangeAccountKey dateRangeKey = new DateRangeAccountKey("975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0",
			startDate,
			endDate);

		Mockito.when(accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class),
			Mockito.any(ServiceErrors.class))).thenReturn(account);

		Mockito.when(accountIntegrationService.loadAccountBglData(Mockito.any(AccountKey.class),
			Mockito.any(DateTime.class),
			Mockito.any(DateTime.class),
			Mockito.any(ServiceErrors.class))).thenAnswer(new Answer <String>()
		{
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable
			{
				Assert.assertEquals(startDate, invocation.getArguments()[1]);
				Assert.assertEquals(accountCloseDate, invocation.getArguments()[2]);
				return BGL_DATA;
			}

		});
		bglDtoService.find(dateRangeKey, new FailFastErrorsImpl());

	}

}
