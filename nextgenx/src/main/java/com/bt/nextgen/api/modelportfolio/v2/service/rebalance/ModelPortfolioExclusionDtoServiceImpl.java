package com.bt.nextgen.api.modelportfolio.v2.service.rebalance;

import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ExclusionStatus;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioExclusionDto;
import com.bt.nextgen.api.modelportfolio.v2.model.rebalance.ModelPortfolioExclusionsDto;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.modelportfolio.rebalance.RebalanceExclusionImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceExclusion;
import com.btfin.panorama.core.security.encryption.EncodedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("ModelPortfolioExclusionDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelPortfolioExclusionDtoServiceImpl implements ModelPortfolioExclusionDtoService {

    @Autowired
    private ModelPortfolioRebalanceIntegrationService rebalanceService;

    @Autowired
    private ModelPortfolioHelper helper;


    @Override
    public ModelPortfolioExclusionsDto submit(ModelPortfolioExclusionsDto exclusionsDto, ServiceErrors serviceErrors) {
        IpsKey ipsKey = IpsKey.valueOf(exclusionsDto.getKey().getModelId());
        BrokerKey broker = helper.getCurrentBroker(serviceErrors);

        List<RebalanceExclusion> exclusions = new ArrayList<>();
        for (ModelPortfolioExclusionDto exclusionDto : exclusionsDto.getExclusions()) {
            exclusions.add(new RebalanceExclusionImpl(
                    AccountKey.valueOf(EncodedString.toPlainText(exclusionDto.getKey().getAccountId())),
                    exclusionDto.getExclusionStatus() == ExclusionStatus.INCLUDED, exclusionDto.getExclusionReason()));
        }

        rebalanceService.updateRebalanceExclusions(broker, ipsKey, exclusions, serviceErrors);
        return exclusionsDto;
    }
}
