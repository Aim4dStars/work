package com.bt.nextgen.api.portfolio.v3.service.performance;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.model.performance.PerformanceDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.base.SystemType;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceDtoServiceImplTest
{
	@InjectMocks
	private AccountPerformanceDtoServiceImpl performanceDtoServiceImpl;
	@Mock
	AccountPerformanceIntegrationService accountService;

	@Mock
	@Qualifier("avaloqAccountIntegrationService")
	private AccountIntegrationService accountIntegrationService;

	PerformanceImpl performanceModel;
	AccountKey key;
	ServiceErrors serviceErrors;
	WrapAccountDetailImpl account;

	@Before
	public void setup() throws Exception
	{
		account = new WrapAccountDetailImpl();
		account.setMigrationDate(null);
		account.setMigrationKey(null);
		key = new AccountKey(EncodedString.fromPlainText("36846").toString());
		serviceErrors = new ServiceErrorsImpl();
		performanceModel = new PerformanceImpl();
		performanceModel.setCapitalGrowth(BigDecimal.valueOf(0.0322));
		performanceModel.setIncome(BigDecimal.valueOf(0.002));
		performanceModel.setPerformance(BigDecimal.valueOf(0.078));
        performanceModel.setExpenses(BigDecimal.valueOf(0.045));
        performanceModel.setMktMvt(BigDecimal.valueOf(0.034));
        performanceModel.setPeriodSop(new DateTime("2014-12-09"));
        performanceModel.setPeriodEop(new DateTime("2014-10-09"));
        performanceModel.setPerformanceBeforeFee(BigDecimal.valueOf(0.076));
        performanceModel.setPerformanceAfterFee(BigDecimal.valueOf(0.080));
		Mockito.when(accountService.loadAccountQuarterlyPerformance(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
			.thenReturn(performanceModel);
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
	}

	@Test
	public void testGetPortfolioPerformance_When_Not_Null()
	{
		PerformanceDto performanceDto = performanceDtoServiceImpl.convertToDto(performanceModel, key);
		assertNotNull(performanceDto);
	}

	@Test
	public void testGetPortfolioPerformance_ValueMatches()
	{
		PerformanceDto performanceDto = performanceDtoServiceImpl.convertToDto(performanceModel, key);
		assertEquals(performanceModel.getPerformance(), performanceDto.getPerformance());
		assertEquals(performanceModel.getCapitalGrowth(), performanceDto.getCapitalGrowth());
		assertEquals(performanceModel.getIncome(), performanceDto.getIncome());
        assertEquals(performanceModel.getExpenses(), performanceDto.getExpenses());
        assertEquals(performanceModel.getMktMvt(), performanceDto.getMktMvt());
        assertEquals(performanceModel.getPeriodSop(), performanceDto.getPeriodSop());
        assertEquals(performanceModel.getPeriodEop(), performanceDto.getPeriodEop());
        assertEquals(performanceModel.getPerformanceBeforeFee(), performanceDto.getPerformanceBeforeFee());
        assertEquals(performanceModel.getPerformanceAfterFee(), performanceDto.getPerformanceAfterFee());
	}

	@Test
	public void testFindSingle_matchesPerformanceModelWhenPortfolioKeyPassed() throws Exception
	{
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertEquals(performanceModel.getPerformance(), performanceDto.getPerformance());
		assertEquals(performanceModel.getCapitalGrowth(), performanceDto.getCapitalGrowth());
		assertEquals(performanceModel.getIncome(), performanceDto.getIncome());
        assertEquals(performanceModel.getExpenses(), performanceDto.getExpenses());
        assertEquals(performanceModel.getMktMvt(), performanceDto.getMktMvt());
        assertEquals(performanceModel.getPeriodSop(), performanceDto.getPeriodSop());
        assertEquals(performanceModel.getPeriodEop(), performanceDto.getPeriodEop());
        assertEquals(performanceModel.getPerformanceBeforeFee(), performanceDto.getPerformanceBeforeFee());
        assertEquals(performanceModel.getPerformanceAfterFee(), performanceDto.getPerformanceAfterFee());
	}



	@Test
	public void testGetPortfolioPerformanceWhenNonMigratedAccount()
	{
		//Migration key and date both null
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertTrue(performanceDto.isAccountOverviewPerformanceAvailable());
	}

	@Test
	public void testGetPortfolioPerformanceWhenMigrationDateNull()
	{
		//Migration date null
		account.setMigrationKey("M00721465");
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertTrue(performanceDto.isAccountOverviewPerformanceAvailable());
	}

	@Test
	public void testGetPortfolioPerformanceWhenMigrationKeyNull()
	{
		//Migration Key null
		account.setMigrationDate(new DateTime());
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertTrue(performanceDto.isAccountOverviewPerformanceAvailable());
	}

	@Test
	public void testGetPortfolioPerformanceWhenDataMigratedAndAvailable()
	{
		// Account migrated but the quarters are not consecutive
		DateTime migrationDate = new DateTime().minusMonths(9).withTimeAtStartOfDay();
		account = new WrapAccountDetailImpl();
		account.setMigrationSourceId(SystemType.WRAP);
		account.setMigrationDate(migrationDate);
		account.setMigrationKey("M00721465");
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertTrue(performanceDto.isAccountOverviewPerformanceAvailable());
	}

	@Test
	public void testGetPortfolioPerformanceWhenDataMigratedInSameQuarter()
	{
		// Account migrated and dates in same quarter
		DateTime migrationDate = new DateTime().withTimeAtStartOfDay();
		account.setMigrationSourceId(SystemType.WRAP);
		account.setMigrationDate(migrationDate);
		account.setMigrationKey("M00721465");
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertFalse(performanceDto.isAccountOverviewPerformanceAvailable());
	}

	@Test
	public void testGetPortfolioPerformanceWhenDataMigratedInPreviousQuarter()
	{
		// Account migrated and migration date in previous quarter
		DateTime migrationDate = new DateTime().minusMonths(4).withTimeAtStartOfDay();
		account.setMigrationSourceId(SystemType.WRAP);
		account.setMigrationDate(migrationDate);
		account.setMigrationKey("M00721465");
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(account);
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertFalse(performanceDto.isAccountOverviewPerformanceAvailable());
	}

	@Test
	public void testGetPortfolioPerformanceAccountNull()
	{
		//Account null
		Mockito.when(accountIntegrationService.loadWrapAccountDetail(any(com.bt.nextgen.service.integration.account.AccountKey.class), any(ServiceErrors.class))).thenReturn(null);
		PerformanceDto performanceDto = performanceDtoServiceImpl.find(key, serviceErrors);
		assertNotNull(performanceDto);
		assertTrue(performanceDto.isAccountOverviewPerformanceAvailable());
	}

}
