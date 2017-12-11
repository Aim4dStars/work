package com.bt.nextgen.api.modelportfolio.v2.service.defaultparams;

import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.DealerParameterKey;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.ModelPortfolioType;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.PreferredPortfolioDefaultParamsDto;
import com.bt.nextgen.api.modelportfolio.v2.model.defaultparams.TailoredPortfolioDefaultParamsDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.dealergroupparams.DealerGroupParams;
import com.bt.nextgen.service.avaloq.dealergroupparams.DealerGroupParamsIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ModelPortfolioDefaultParamsDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelPortfolioDefaultParamsDtoServiceImpl implements ModelPortfolioDefaultParamsDtoService {

    @Autowired
    private DealerGroupParamsIntegrationService dgParamsIntegrationService;

    @Override
    public ModelPortfolioDefaultParamsDto find(DealerParameterKey key, ServiceErrors serviceErrors) {
        DealerGroupParams params = dgParamsIntegrationService.loadCustomerAccountObjects(key, serviceErrors).get(0);

        ModelPortfolioType portfolioType = ModelPortfolioType.forIntlId(key.getPortfolioType());
        if (ModelPortfolioType.TAILORED.equals(portfolioType)) {
            return new TailoredPortfolioDefaultParamsDto(key, params.getMinimumInitialInvestmentAmount(),
                    params.getMinimumCashAllocationPercentage());

        } else if (ModelPortfolioType.PREFERRED.equals(portfolioType)) {
            return new PreferredPortfolioDefaultParamsDto(key, params.getPPDefaultAssetTolerance(),
                    params.getPPMinimumInvestmentAmount(), params.getPPMinimumTradeAmount());
        }

        return null;
    }

}
