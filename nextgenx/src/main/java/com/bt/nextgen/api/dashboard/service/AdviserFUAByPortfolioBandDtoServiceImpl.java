package com.bt.nextgen.api.dashboard.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.dashboard.model.FUAByPortfolioBandDto;
import com.bt.nextgen.api.dashboard.model.PortfolioBandDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.dashboard.PortfolioValueByBandImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.dashboard.AdviserPerformanceIntegrationService;

@Service
@Transactional(value = "springJpaTransactionManager")
class AdviserFUAByPortfolioBandDtoServiceImpl implements AdviserFUAByPortfolioBandDtoService
{

	@Autowired
	private AdviserPerformanceIntegrationService adviserPerformanceIntegrationService;

	@Autowired
	private UserProfileService userProfileService;

	@Override
	public FUAByPortfolioBandDto findOne(ServiceErrors serviceErrors)
	{
		String oePositionId = userProfileService.getPositionId();
		BrokerKey brokerKey = BrokerKey.valueOf(oePositionId);
		PortfolioValueByBandImpl portfolioValueByBand = adviserPerformanceIntegrationService.loadPorfolioValueByBand(brokerKey,
			serviceErrors);
		FUAByPortfolioBandDto fuaByPortfolioBandDto = getPortfolioBandDetails(portfolioValueByBand);
		return fuaByPortfolioBandDto;
	}

	public FUAByPortfolioBandDto getPortfolioBandDetails(PortfolioValueByBandImpl portfolioValueByBand)
	{
		FUAByPortfolioBandDto fuaByPortfolioBandDto = null;

		if (portfolioValueByBand == null)
		{
			return new FUAByPortfolioBandDto(null, null, null, null, null, null);
		}

		List <PortfolioBandDto> portfolioBandDtos = new ArrayList <>();
		for (int i = 1; i <= 6; i++)
		{
			BigDecimal[] band = portfolioValueByBand.getPortfolioBand((i));
			PortfolioBandDto portfolioBandDto = new PortfolioBandDto(i, band[0], band[1]);
			portfolioBandDtos.add(portfolioBandDto);
		}

		fuaByPortfolioBandDto = new FUAByPortfolioBandDto(portfolioValueByBand.getPeriodSop(),
			portfolioValueByBand.getPeriodEop(),
			portfolioValueByBand.getAveragePortfolioValue(),
			portfolioValueByBand.getMedianPortfolioValue(),
			portfolioValueByBand.getPortfolioValue(),
			portfolioBandDtos);

		return fuaByPortfolioBandDto;
	}
}
