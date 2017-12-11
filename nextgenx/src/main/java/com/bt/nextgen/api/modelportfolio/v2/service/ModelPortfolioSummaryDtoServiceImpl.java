package com.bt.nextgen.api.modelportfolio.v2.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductRelation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ModelPortfolioSummaryDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class ModelPortfolioSummaryDtoServiceImpl implements ModelPortfolioSummaryDtoService {
    @Autowired
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private ProductIntegrationService productService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Override
	public List <ModelPortfolioSummaryDto> findAll(ServiceErrors serviceErrors)
	{
		List <Broker> brokers = brokerService.getBrokersForJob(userProfileService.getActiveProfile(), serviceErrors);
		BrokerKey brokerKey = null;
        if (brokers != null && !brokers.isEmpty()) {
            brokerKey = getBrokerKey(brokers.get(0));
		}
		if (brokerKey != null)
		{
			return toModelPortfolioSummaryDto(modelPortfolioSummaryService.loadModels(brokerKey, serviceErrors), serviceErrors);
		}
		return Collections.emptyList();
	}

	private BrokerKey getBrokerKey(Broker broker)
	{
		BrokerKey brokerKey = null;
		if (BrokerType.DEALER.equals(broker.getBrokerType()) || BrokerType.INVESTMENT_MANAGER.equals(broker.getBrokerType()))
		{
			brokerKey = broker.getKey();
		}
		else if (BrokerType.ADVISER.equals(broker.getBrokerType()))
		{
            brokerKey = broker.getDealerKey();
            if (Properties.getSafeBoolean("feature.model.advisermodel")) {
                brokerKey = broker.getKey();
            }
		}
		return brokerKey;
	}

    protected List<ModelPortfolioSummaryDto> toModelPortfolioSummaryDto(List<ModelPortfolioSummary> modelSummaries,
            ServiceErrors serviceErrors) {
        List<ModelPortfolioSummaryDto> modelPortfolioSummaryDtos = new ArrayList<>();
        List<Product> products = (List<Product>) productService.loadProducts(serviceErrors);
        
        List<String> modelIds = Lambda.extract(modelSummaries, Lambda.on(ModelPortfolioSummary.class).getModelKey().getId());
        Map<String, String> modelAssetMap = getModelAssetMap(modelIds, products);
        Collection<String> assetIds = modelAssetMap.values();
        assetIds.removeAll(Collections.singleton(null));

        Map<String, Asset> assetMap = assetService.loadAssets(assetIds, serviceErrors);

        for (ModelPortfolioSummary model : modelSummaries) {
            String assetId = modelAssetMap.get(model.getModelKey().getId());
            AssetType assetType = assetId == null ? AssetType.OTHER : assetMap.get(assetId).getAssetType();
            modelPortfolioSummaryDtos.add(new ModelPortfolioSummaryDto(model, assetType));
        }
        return modelPortfolioSummaryDtos;
    }

    private Map<String, String> getModelAssetMap(List<String> modelIds, List<Product> products) {
        Map<String, String> modelAssetMap = new HashMap<String, String>();
        for (String modelId : modelIds) {
            String assetId = loadAssetIdFromProductRelations(modelId, products);
            modelAssetMap.put(modelId, assetId);
        }
        return modelAssetMap;
    }

    private String loadAssetIdFromProductRelations(String modelId, List<Product> products) {

        for (Product product : products) {
            List<ProductRelation> productRelations = product.getProductRelation();
            if (productRelations != null) {
                for (ProductRelation productRelation : productRelations) {
                    if (productRelation != null && productRelation.getProductRelTo() != null
                            && productRelation.getProductRelTo().equals(modelId)) {
                        return product.getAssetId();
                    }
                }
            }
        }
        return null;
    }
}
