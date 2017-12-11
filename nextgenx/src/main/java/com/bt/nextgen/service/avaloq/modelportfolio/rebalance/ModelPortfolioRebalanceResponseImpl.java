package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.AvaloqBaseResponseImpl;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/")
public class ModelPortfolioRebalanceResponseImpl extends AvaloqBaseResponseImpl {
    
        @ServiceElementList(xpath = "//data/ips_list/ips", type = ModelPortfolioRebalanceImpl.class)
        private List <ModelPortfolioRebalance> modelPortfolioRebalances;

        public List<ModelPortfolioRebalance> getModelPortfolioRebalances() {
            if (modelPortfolioRebalances == null)
                return Collections.emptyList();            
            return modelPortfolioRebalances;
        }
        
}
