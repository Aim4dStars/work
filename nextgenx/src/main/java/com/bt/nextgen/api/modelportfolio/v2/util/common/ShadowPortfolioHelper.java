package com.bt.nextgen.api.modelportfolio.v2.util.common;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ShadowPortfolioHelper {

    @Autowired
    private ModelPortfolioIntegrationService modelPortfolioService;

    /**
     * Retrieve the asset-map for the ShadowPortfolio for the specified model. The map constructed will be using the asset-id as
     * the key, and floating-target allocation as the value.
     * 
     * @param key
     *            model-key
     * @param serviceErrors
     * @return
     */
    public Map<String, BigDecimal> getFloatingTargetAllocationMap(IpsKey key, ServiceErrors serviceErrors) {
        Map<String, BigDecimal> floatingMap = new HashMap<>();

        ShadowPortfolio shadowPortfolio = modelPortfolioService.loadShadowPortfolioModel(key, serviceErrors);
        if (shadowPortfolio != null && shadowPortfolio.getAssetSummaries() != null) {
            for (ShadowPortfolioAssetSummary assetSummary : shadowPortfolio.getAssetSummaries()) {
                if (assetSummary.getAssets() != null) {
                    for (ShadowPortfolioAsset spAsset : assetSummary.getAssets()) {
                        floatingMap.put(spAsset.getAssetId(), spAsset.getShadowDetail().getFloatingTargetPercent());
                    }
                }
            }
        }
        return floatingMap;
    }
}
