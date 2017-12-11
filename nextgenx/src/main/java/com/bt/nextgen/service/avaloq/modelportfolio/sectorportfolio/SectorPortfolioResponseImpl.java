package com.bt.nextgen.service.avaloq.modelportfolio.sectorportfolio;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;

import java.util.List;

@ServiceBean(xpath = "/")
public class SectorPortfolioResponseImpl extends AvaloqBaseResponseImpl implements SectorPortfolioResponse {
    
    @ServiceElementList(xpath = "//data/mpf_list/mpf", type = SectorPortfolioImpl.class)
    private List<SectorPortfolio> sectorPortfolios;

    public List<SectorPortfolio> getSectorPortfolios() {
        return sectorPortfolios;
    }
        
}
