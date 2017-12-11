package com.bt.nextgen.service.avaloq.dashboard;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AvaloqAdviserPerformanceServiceImplIntegrationTest extends BaseSecureIntegrationTest
{
	protected BrokerKey brokerKey;

	@Autowired
	AdviserPerformanceIntegrationService performanceService;

	@Before
	public void setup()
	{
		brokerKey = BrokerKey.valueOf("66435");
	}

	@Test
	public void testLoadCurrentFinancialYearPerformance_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		AdviserPerformanceImpl performance = performanceService.loadCurrentFinancialYearPerformanceData(brokerKey, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(performance);
	}

	@Test
	public void testLoadCurrentQuarterPerformance_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		AdviserPerformanceImpl performance = performanceService.loadCurrentQuarterPerformanceData(brokerKey, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(performance);
	}

	@Test
	public void testLoadCurrentYearPerformance_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		AdviserPerformanceImpl performance = performanceService.loadCurrentYearPerformanceData(brokerKey, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(performance);
	}

	@Test
	public void testLoadLastFinYearPerformance_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		AdviserPerformanceImpl performance = performanceService.loadLastFinancialYearPerformanceData(brokerKey, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(performance);
	}

	@Test
	public void testLoadMonthlyPerformance_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		AdviserPerformanceImpl performance = performanceService.loadCurrentMonthPerformanceData(brokerKey, serviceErrors);
		Assert.assertFalse(serviceErrors.hasErrors());
		Assert.assertNotNull(performance);
	}

	@Test
	public void testLoadTopAccountsByCash_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <TopAccountsByValueImpl> topAccounts = performanceService.loadTopAccountsByCash(brokerKey, serviceErrors);
		Assert.assertNotNull(topAccounts);
	}

	@Test
	public void testLoadTopAccountsByPortfolio_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		List <TopAccountsByValueImpl> topAccounts = performanceService.loadTopAccountsByPortfolio(brokerKey, serviceErrors);
		Assert.assertNotNull(topAccounts);
	}

	@Test
	public void testLoadPortfolioValueByBand_whenValidResponse_thenObjectCreatedAndNoServiceErrors() throws Exception
	{
		ServiceErrors serviceErrors = new ServiceErrorsImpl();
		PortfolioValueByBandImpl portfolioValueByBand = performanceService.loadPorfolioValueByBand(brokerKey, serviceErrors);
		Assert.assertNotNull(portfolioValueByBand);
	}
}
