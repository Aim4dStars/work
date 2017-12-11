package com.bt.nextgen.api.account.v2.service;

import static org.junit.Assert.*;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.PerformanceDto;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.performance.PerformanceImpl;
import com.bt.nextgen.service.integration.portfolio.performance.AccountPerformanceIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class AccountPerformanceDtoServiceImplTest
{
	@InjectMocks
	private AccountPerformanceDtoServiceImpl performanceDtoServiceImpl;
	@Mock
	AccountPerformanceIntegrationService accountService;

	PerformanceImpl performanceModel;
	AccountKey key;
	ServiceErrors serviceErrors;

	@Before
	public void setup() throws Exception
	{
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
}
