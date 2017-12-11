package com.bt.nextgen.reports.modelportfolio;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioKey;
import com.bt.nextgen.api.modelportfolio.v2.util.common.ModelPortfolioHelper;
import com.bt.nextgen.api.performance.service.AccountPerformanceReportDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.product.ProductRelation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ModelPortfolioReportTest {

    private static final String MODEL_ID = "model-id";
    @InjectMocks
    private ModelPortfolioReport modelPortfolioReport;

    @Mock
    private AccountPerformanceReportDtoService performanceService;

    @Mock
    private ProductIntegrationService productService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private ModelPortfolioHelper helper;

    @Before
    public void setup() {

        ProductRelation productRelation = Mockito.mock(ProductRelation.class);
        Mockito.when(productRelation.getProductRelTo()).thenReturn("modelId");

        List<ProductRelation> productRelations = new ArrayList<ProductRelation>();
        productRelations.add(productRelation);

        Product product = Mockito.mock(Product.class);
        Mockito.when(product.getAssetId()).thenReturn("assetId");
        Mockito.when(product.getProductRelation()).thenReturn(productRelations);

        List<Product> products = new ArrayList<Product>();
        products.add(product);

        Mockito.when(productService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(products);
    }

    @Test
    public void testGetModelType_whenTailoredPortfolio_thenModelTypeIsCorrect() {
        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.TAILORED_PORTFOLIO);
        Mockito.when(assetService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(asset);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MODEL_ID, "modelId");

        Assert.assertEquals("TAILORED_PORTFOLIO", modelPortfolioReport.getModelType(params));
    }

    @Test
    public void testGetModelType_whenManagedPortfolio_thenModelTypeIsCorrect() {
        Asset asset = Mockito.mock(Asset.class);
        Mockito.when(asset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        Mockito.when(assetService.loadAsset(Mockito.any(String.class), Mockito.any(ServiceErrors.class))).thenReturn(asset);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MODEL_ID, "modelId");

        Assert.assertEquals("MANAGED_PORTFOLIO", modelPortfolioReport.getModelType(params));
    }

    @Test
    public void testGetModelType_whenUnidentifiedAsset_thenModelTypeIsDefault() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MODEL_ID, "modelIdOther");

        Assert.assertEquals("OTHER", modelPortfolioReport.getModelType(params));
    }

    @Test
    public void testGetModelConstruction_whenTailoredPortfolio_thenModelTypeIsCorrect() {
        IpsSummaryDetails details = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(details.getModelConstruction()).thenReturn(ConstructionType.FIXED);
        Mockito.when(helper.getIpsSummaryDetails(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MODEL_ID, "modelId");

        Assert.assertEquals("FIXED", modelPortfolioReport.getModelConstruction(params));
    }

    @Test
    public void testGetModelType_whenModelConstructionNotAvailable_thenEmptyStringReturned() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(MODEL_ID, "modelId");

        Assert.assertEquals("", modelPortfolioReport.getModelConstruction(params));

        IpsSummaryDetails details = Mockito.mock(IpsSummaryDetails.class);
        Mockito.when(helper.getIpsSummaryDetails(Mockito.any(ModelPortfolioKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(details);

        Assert.assertEquals("", modelPortfolioReport.getModelConstruction(params));
    }
}
