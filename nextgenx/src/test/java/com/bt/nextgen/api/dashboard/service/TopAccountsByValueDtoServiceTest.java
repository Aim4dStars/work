package com.bt.nextgen.api.dashboard.service;

import com.bt.nextgen.api.dashboard.model.TopAccountDto;
import com.bt.nextgen.api.dashboard.model.TopAccountsByValueDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.dashboard.TopAccountsByValueImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TopAccountsByValueDtoServiceTest
{
	private static final String CASH = "cash";

	@InjectMocks
	private TopAccountsByValueDtoServiceImpl topAccountsDtoService;

	@Mock
	private AdviserPerformanceIntegrationService adviserService;

	@Mock
	private UserProfileService userProfileService;

	@Mock
	private AccountIntegrationService accountService;

	@Before
	public void setup() throws Exception
	{
		List <TopAccountsByValueImpl> topAccountsByCash = new ArrayList <>();
		List <TopAccountsByValueImpl> topAccountsByPortfolio = new ArrayList <>();

		TopAccountsByValueImpl topAccount = new TopAccountsByValueImpl();
		topAccount.setAccountId("12345");
		topAccount.setOrderBy(CASH);
		topAccount.setCashValue(BigDecimal.valueOf(4000));
		topAccount.setPortfolioValue(BigDecimal.valueOf(100));

		TopAccountsByValueImpl topAccount2 = new TopAccountsByValueImpl();
		topAccount2.setAccountId("54321");
		topAccount2.setOrderBy(CASH);
		topAccount2.setCashValue(BigDecimal.valueOf(50));
		topAccount2.setPortfolioValue(BigDecimal.valueOf(20000));

		topAccountsByCash.add(topAccount);
		topAccountsByCash.add(topAccount2);

		topAccountsByPortfolio.add(topAccount2);
		topAccountsByPortfolio.add(topAccount);

		//====

        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();

		WrapAccountImpl testAccount = new WrapAccountImpl();
		testAccount.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("12345"));
		testAccount.setAccountName("Testy Test");

		WrapAccountImpl testAccount2 = new WrapAccountImpl();
		testAccount2.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("54321"));
		testAccount2.setAccountName("Timmy Snark");

		WrapAccountImpl testAccount3 = new WrapAccountImpl();
		testAccount3.setAccountKey(com.bt.nextgen.service.integration.account.AccountKey.valueOf("99999"));
		testAccount3.setAccountName("This wedding is a sham");

        accountMap.put(testAccount.getAccountKey(), testAccount);
        accountMap.put(testAccount2.getAccountKey(), testAccount2);
        accountMap.put(testAccount3.getAccountKey(), testAccount3);

		// Connect service calls to mock data
		Mockito.when(adviserService.loadTopAccountsByCash(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(topAccountsByCash);

		Mockito.when(adviserService.loadTopAccountsByPortfolio(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
			.thenReturn(topAccountsByPortfolio);

        Mockito.when(accountService.loadWrapAccountWithoutContainers(Mockito.any(ServiceErrors.class))).thenReturn(accountMap);

		Mockito.when(userProfileService.getPositionId()).thenReturn("34343");
	}

	@Test
	public void testGetTopAccounts_WhenServiceCalled_thenTopPortfolioAccountsDataReturnedAsExpected()
	{
		TopAccountsByValueDto topAccounts = topAccountsDtoService.findOne(new ServiceErrorsImpl());

		List <TopAccountDto> topCash = topAccounts.getTopAccountsByCash();

		Assert.assertEquals(2, topCash.size());

		TopAccountDto cash = topCash.get(0);
		Assert.assertEquals("12345", EncodedString.toPlainText(cash.getEncodedAccountKey().getId()));
		Assert.assertEquals("Testy Test", cash.getAccountName());
		Assert.assertEquals(BigDecimal.valueOf(4000), cash.getCashBalance());
		Assert.assertEquals(BigDecimal.valueOf(100), cash.getPortfolioValue());

		TopAccountDto cash2 = topCash.get(1);
		Assert.assertEquals("54321", EncodedString.toPlainText(cash2.getEncodedAccountKey().getId()));
		Assert.assertEquals("Timmy Snark", cash2.getAccountName());
		Assert.assertEquals(BigDecimal.valueOf(50), cash2.getCashBalance());
		Assert.assertEquals(BigDecimal.valueOf(20000), cash2.getPortfolioValue());
	}

	@Test
	public void testGetTopAccounts_WhenServiceCalled_thenTopCashAccountsDataReturnedAsExpected()
	{
		TopAccountsByValueDto topAccounts = topAccountsDtoService.findOne(new ServiceErrorsImpl());

		List <TopAccountDto> topPortfolio = topAccounts.getTopAccountsByPortfolio();

		Assert.assertEquals(2, topPortfolio.size());

		TopAccountDto portfolio = topPortfolio.get(0);
		Assert.assertEquals("54321", EncodedString.toPlainText(portfolio.getEncodedAccountKey().getId()));
		Assert.assertEquals("Timmy Snark", portfolio.getAccountName());
		Assert.assertEquals(BigDecimal.valueOf(50), portfolio.getCashBalance());
		Assert.assertEquals(BigDecimal.valueOf(20000), portfolio.getPortfolioValue());

		TopAccountDto portfolio2 = topPortfolio.get(1);
		Assert.assertEquals("12345", EncodedString.toPlainText(portfolio2.getEncodedAccountKey().getId()));
		Assert.assertEquals("Testy Test", portfolio2.getAccountName());
		Assert.assertEquals(BigDecimal.valueOf(4000), portfolio2.getCashBalance());
		Assert.assertEquals(BigDecimal.valueOf(100), portfolio2.getPortfolioValue());
	}

    @Test
    public void testLoadTopAccountsByCash_correctServiceCalled() {
        topAccountsDtoService.loadTopAccountsByCash(null, null).call();
        verify(adviserService, times(1)).loadTopAccountsByCash(any(BrokerKey.class), any(ServiceErrors.class));
    }

    @Test
    public void testLoadTopAccountsByPortfolio_correctServiceCalled() {
        topAccountsDtoService.loadTopAccountsByPortfolio(null, null).call();
        verify(adviserService, times(1)).loadTopAccountsByPortfolio(any(BrokerKey.class), any(ServiceErrors.class));
    }
}
