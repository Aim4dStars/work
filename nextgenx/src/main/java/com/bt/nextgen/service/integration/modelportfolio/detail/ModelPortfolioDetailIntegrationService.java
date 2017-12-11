package com.bt.nextgen.service.integration.modelportfolio.detail;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.service.ServiceErrors;

public interface ModelPortfolioDetailIntegrationService {

    public ModelPortfolioDetail validateModelPortfolio(ModelPortfolioDetail model, ServiceErrors serviceErrors);

    public ModelPortfolioDetail submitModelPortfolio(ModelPortfolioDetail model, ServiceErrors serviceErrors);

    public ModelPortfolioDetail loadModelPortfolio(ModelPortfolioKey modelKey, ServiceErrors serviceErrors);

}
