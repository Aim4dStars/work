package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerKey;

import java.util.List;


public interface SectorPortfolioIntegrationService {

    public List<SectorPortfolio> loadSectorPortfoliosForManager(BrokerKey brokerKey, ServiceErrors serviceErrors);
    
    public List<SectorPortfolio> loadSectorPortfolios(List<SectorPortfolioKey> sectorPortfolioKeys, ServiceErrors serviceErrors);
}
