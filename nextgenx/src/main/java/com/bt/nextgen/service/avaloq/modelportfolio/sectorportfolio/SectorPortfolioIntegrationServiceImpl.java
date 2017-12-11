package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.service.AvaloqReportService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("avaloqSectorPortfolioIntegrationService")
public class SectorPortfolioIntegrationServiceImpl extends AbstractAvaloqIntegrationService implements
        SectorPortfolioIntegrationService {

    @Autowired
    private AvaloqReportService avaloqService;

    @Override
    public List<SectorPortfolio> loadSectorPortfoliosForManager(BrokerKey brokerKey, ServiceErrors serviceErrors) {

        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(SectorPortfolioTemplate.SECTOR_PORTFOLIOS_FOR_IM).forParam(
                SectorPortfolioParams.PARAM_INVESTMENT_MANAGER_ID, brokerKey.getId());
        SectorPortfolioResponse response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                SectorPortfolioResponseImpl.class, serviceErrors);
        return response.getSectorPortfolios();

    }

    @Override
    public List<SectorPortfolio> loadSectorPortfolios(List<SectorPortfolioKey> sectorPortfolioKeys, ServiceErrors serviceErrors) {

        final List<String> strKeyList = new ArrayList<>();
        for (SectorPortfolioKey key : sectorPortfolioKeys) {
            strKeyList.add(key.getId());
        }
        AvaloqRequest avaloqRequest = new AvaloqReportRequestImpl(SectorPortfolioTemplate.SECTOR_PORTFOLIOS).forParam(
                SectorPortfolioParams.PARAM_MPF_LIST, strKeyList);
        SectorPortfolioResponse response = avaloqService.executeReportRequestToDomain(avaloqRequest,
                SectorPortfolioResponseImpl.class, serviceErrors);
        return response.getSectorPortfolios();
    }

}
