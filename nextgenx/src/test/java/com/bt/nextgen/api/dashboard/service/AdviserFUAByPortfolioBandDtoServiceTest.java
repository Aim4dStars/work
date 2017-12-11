package com.bt.nextgen.api.dashboard.service;

import java.math.BigDecimal;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.dashboard.model.FUAByPortfolioBandDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.dashboard.PortfolioValueByBandImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;

@RunWith(MockitoJUnitRunner.class)
public class AdviserFUAByPortfolioBandDtoServiceTest
{

	@InjectMocks
	private AdviserFUAByPortfolioBandDtoServiceImpl adviserFUAByPortfolioBandDtoService;

	@Mock
	AdviserPerformanceIntegrationService adviserPerformanceIntegrationService;

	@Mock
	UserProfileService userProfileService;

	BrokerKey brokerKey = BrokerKey.valueOf("34343");

	@Test
	public void testGetPorfolioValueByBand_WhenByPortfolioBandSelected_thenFUAReturnedAsExpected()
	{
		PortfolioValueByBandImpl portfolioBandImpl = new PortfolioValueByBandImpl();
		portfolioBandImpl.setPeriodSop(new DateTime());
		portfolioBandImpl.setPeriodEop(new DateTime());
		portfolioBandImpl.setPortfolioValue(BigDecimal.valueOf(10000));
		portfolioBandImpl.setAveragePortfolioValue(BigDecimal.valueOf(8750));
		portfolioBandImpl.setMedianPortfolioValue(BigDecimal.valueOf(8650));
		portfolioBandImpl.setBandOneAccounts(BigDecimal.valueOf(20));
		portfolioBandImpl.setBandOnePortfolioValue(BigDecimal.valueOf(6000));
		portfolioBandImpl.setBandTwoAccounts(BigDecimal.valueOf(20));
		portfolioBandImpl.setBandTwoPortfolioValue(BigDecimal.valueOf(6000));
		portfolioBandImpl.setBandThreeAccounts(BigDecimal.valueOf(20));
		portfolioBandImpl.setBandThreePortfolioValue(BigDecimal.valueOf(6000));
		portfolioBandImpl.setBandFourAccounts(BigDecimal.valueOf(20));
		portfolioBandImpl.setBandFourPortfolioValue(BigDecimal.valueOf(6000));
		portfolioBandImpl.setBandFiveAccounts(BigDecimal.valueOf(20));
		portfolioBandImpl.setBandFivePortfolioValue(BigDecimal.valueOf(6000));
		portfolioBandImpl.setBandSixAccounts(BigDecimal.valueOf(20));
		portfolioBandImpl.setBandSixPortfolioValue(BigDecimal.valueOf(6000));
		Mockito.when(adviserPerformanceIntegrationService.loadPorfolioValueByBand(Mockito.any(BrokerKey.class),
			Mockito.any(ServiceErrorsImpl.class))).thenReturn(portfolioBandImpl);

		Mockito.when(userProfileService.getPositionId()).thenReturn("34343");

		FUAByPortfolioBandDto fuaByPortfolioBandDto = adviserFUAByPortfolioBandDtoService.findOne(new ServiceErrorsImpl());

		Assert.assertNotNull(fuaByPortfolioBandDto);
		Assert.assertEquals(BigDecimal.valueOf(10000), fuaByPortfolioBandDto.getTotalPortfolioValue());
		Assert.assertEquals(BigDecimal.valueOf(8750), fuaByPortfolioBandDto.getAveragePortfolioValue());
		Assert.assertEquals(BigDecimal.valueOf(8650), fuaByPortfolioBandDto.getMedianPortfolioValue());
		Assert.assertEquals(BigDecimal.valueOf(20), fuaByPortfolioBandDto.getPortfolioBands().get(0).getAccounts());
		Assert.assertEquals(BigDecimal.valueOf(6000), fuaByPortfolioBandDto.getPortfolioBands().get(0).getFua());
	}
}
