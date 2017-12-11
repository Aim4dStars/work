package com.bt.nextgen.reports.modelportfolio;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioDto;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioDtoService;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.core.reporting.BaseReport;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductRelation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Report("modelPortfolioReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
public class ModelPortfolioReport extends BaseReport {

    private static final String MODEL_ID = "model-id";

    @Autowired
    private ModelPortfolioDtoService modelPortfolioService;

    @Autowired
    private ProductIntegrationService productService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private ModelPortfolioHelper helper;

    public ModelPortfolioDto getModelPortfolio(ModelPortfolioKey modelKey, ServiceErrors serviceErrors) {

        return modelPortfolioService.find(modelKey, serviceErrors);
    }

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String modelId = (String) params.get(MODEL_ID);
        ModelPortfolioKey modelKey = new ModelPortfolioKey(modelId);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        return Collections.singletonList(getModelPortfolio(modelKey, serviceErrors));
    }

    @ReportBean("modelType")
    @SuppressWarnings("squid:S1172")
    public String getModelType(Map<String, Object> params) {
        String modelId = (String) params.get(MODEL_ID);
        String assetId = loadAssetIdFromProductRelations(modelId);
        if(assetId == null){
            return AssetType.OTHER.name();
        }
        Asset asset = loadAsset(assetId);        
        return asset.getAssetType().name();
    }

    @ReportBean("modelConstruction")
    public String getModelConstruction(Map<String, Object> params) {
        String modelId = (String) params.get(MODEL_ID);
        IpsSummaryDetails details = helper.getIpsSummaryDetails(new ModelPortfolioKey(modelId), new FailFastErrorsImpl());
        if (details == null || details.getModelConstruction() == null) {
            return "";
        }
        return details.getModelConstruction().name();
    }

    private String loadAssetIdFromProductRelations(String modelId) {

        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        List<Product> products = (List<Product>) productService.loadProducts(serviceErrors);

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

    private Asset loadAsset(String assetId) {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        return assetService.loadAsset(assetId, serviceErrors);
    }
}
