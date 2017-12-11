package com.bt.nextgen.api.modelportfolio.v2.service.sectorportfolio;

import com.bt.nextgen.api.modelportfolio.v2.model.sector.SectorPortfolioDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolio;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioIntegrationService;
import com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio.SectorPortfolioKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("SectorPortfolioDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class SectorPortfolioDtoServiceImpl implements SectorPortfolioDtoService {

    @Autowired
    private SectorPortfolioIntegrationService sectorPortfolioIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Override
    public List<SectorPortfolioDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {

        List<SectorPortfolioKey> sectorPortfolioKeys = new ArrayList<>();
        for (ApiSearchCriteria criteria : criteriaList) {
            sectorPortfolioKeys.add(SectorPortfolioKey.valueOf(criteria.getValue()));
        }

        List<SectorPortfolio> sectorPortfolios = sectorPortfolioIntegrationService.loadSectorPortfolios(sectorPortfolioKeys,
                serviceErrors);
        return buildSectorPortfolioDtos(sectorPortfolios);
    }

    @Override
    public List<SectorPortfolioDto> findAll(ServiceErrors serviceErrors) {

        List<Broker> brokers = getBrokers(serviceErrors);
        List<SectorPortfolio> sectorPortfolios = new ArrayList<>();

        for (Broker broker : brokers) {
            List<SectorPortfolio> resultList = sectorPortfolioIntegrationService.loadSectorPortfoliosForManager(broker.getKey(),
                    serviceErrors);
            sectorPortfolios.addAll(resultList);
        }

        return buildSectorPortfolioDtos(sectorPortfolios);
    }

    private List<Broker> getBrokers(ServiceErrors serviceErrors) {
        return brokerService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
    }

    protected List<SectorPortfolioDto> buildSectorPortfolioDtos(List<SectorPortfolio> sectorPortfolios) {

        List<SectorPortfolioDto> sectorPortfolioDtos = new ArrayList<SectorPortfolioDto>();

        for (SectorPortfolio sectorPortfolio : sectorPortfolios) {

            if (sectorPortfolio != null) {
                sectorPortfolioDtos.add(new SectorPortfolioDto(sectorPortfolio));
            }
        }

        return sectorPortfolioDtos;
    }

}
