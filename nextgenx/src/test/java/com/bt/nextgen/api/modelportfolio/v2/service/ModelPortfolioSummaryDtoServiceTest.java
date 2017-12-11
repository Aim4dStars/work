package com.bt.nextgen.api.modelportfolio.v2.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioSummaryImpl;
import com.bt.nextgen.service.avaloq.product.ProductImpl;
import com.bt.nextgen.service.avaloq.product.ProductRelation;
import com.bt.nextgen.service.avaloq.product.ProductRelationImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummaryIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.userprofile.JobProfileIdentifier;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.btfin.panorama.service.integration.broker.BrokerType;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Properties.class })
public class ModelPortfolioSummaryDtoServiceTest {
    @InjectMocks
    private final ModelPortfolioSummaryDtoServiceImpl modelPortfolioSummaryDtoService = new ModelPortfolioSummaryDtoServiceImpl();

    @Mock
    private ModelPortfolioSummaryIntegrationService modelPortfolioSummaryService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    private ProductIntegrationService productService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private UserProfileService userProfileService;

    IpsKey emptyModelKey;
    IpsKey modelKey;
    List<ModelPortfolioSummary> emptyModelPortfolioList;
    ModelPortfolioSummaryImpl model1;
    ModelPortfolioSummaryImpl model2;
    List<ModelPortfolioSummary> modelPortfolioList;
    List<Product> products;
    List<ProductRelation> productRelations1;
    List<ProductRelation> productRelations2;
    ProductImpl product1;
    ProductRelationImpl productRelationImpl1;
    ProductImpl product2;
    ProductRelationImpl productRelationImpl2;
    Map<String, Asset> assetMap;
    AssetImpl asset1;
    AssetImpl asset2;

    @Before
    public void setup() throws Exception {
        modelPortfolioList = new ArrayList<>();
        emptyModelPortfolioList = new ArrayList<>();
        emptyModelKey = IpsKey.valueOf("99999");
        modelKey = IpsKey.valueOf("11111");

        model1 = new ModelPortfolioSummaryImpl();
        model1.setModelKey(modelKey);
        model1.setModelName("Balanced portfolio");
        model1.setModelCode("ACM123456");
        model1.setLastUpdateDate(new DateTime());
        model1.setLastUpdatedBy("Frank Wong");
        model1.setAssetClass("Australian Shares");
        model1.setInvestmentStyle("Income");
        model1.setStatus(IpsStatus.PENDING);
        model1.setFum(new BigDecimal(27493738.56));
        model1.setApirCode("WS40567");
        model1.setAccountType(ModelType.INVESTMENT);
        model1.setModelConstruction(ConstructionType.FIXED);
        model1.setModelDescription("modelDescription");
        model1.setOpenDate(new DateTime());
        model1.setNumAccounts(0);

        model2 = new ModelPortfolioSummaryImpl();
        model2.setModelKey(IpsKey.valueOf("22222"));
        model2.setModelName("Growth portfolio");
        model2.setModelCode("ACM123457");
        model2.setLastUpdateDate(new DateTime());
        model2.setLastUpdatedBy("Ivan Matthews");
        model2.setAssetClass("Diversified");
        model2.setInvestmentStyle("Growth");
        model2.setStatus(IpsStatus.OPEN);
        model2.setFum(new BigDecimal(949827543.34));
        model2.setModelConstruction(null);
        model2.setModelDescription("modelDescription");
        model2.setOpenDate(new DateTime());
        model2.setNumAccounts(10);

        modelPortfolioList.add(model1);
        modelPortfolioList.add(model2);

        products = new ArrayList<>();
        productRelations1 = new ArrayList<>();
        productRelations2 = new ArrayList<>();

        product1 = new ProductImpl();
        productRelationImpl1 = new ProductRelationImpl("productRelSubType1", "11111");
        productRelations1.add(productRelationImpl1);
        product1.setProductRelation(productRelations1);
        product1.setAssetId("11111");

        product2 = new ProductImpl();
        productRelationImpl2 = new ProductRelationImpl("productRelSubType2", "22222");
        productRelations2.add(productRelationImpl2);
        product2.setProductRelation(productRelations2);
        product2.setAssetId("22222");

        asset1 = new AssetImpl();
        asset1.setAssetId("11111");
        asset1.setAssetType(AssetType.TAILORED_PORTFOLIO);

        asset2 = new AssetImpl();
        asset2.setAssetId("22222");
        asset2.setAssetType(AssetType.MANAGED_PORTFOLIO);

        assetMap = new HashMap<>();
        assetMap.put("11111", asset1);
        assetMap.put("22222", asset2);

        products.add(product1);
        products.add(product2);

        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelPortfolioList);

        Mockito.when(productService.loadProducts(Mockito.any(ServiceErrors.class))).thenReturn(products);

        Mockito.when(assetService.loadAssets(Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);
    }

    @Test
    public void testToModelPortfolioDto_modelListEmpty() {
        List<ModelPortfolioSummaryDto> models = modelPortfolioSummaryDtoService.toModelPortfolioSummaryDto(
                emptyModelPortfolioList, new ServiceErrorsImpl());
        Assert.assertEquals(0, models.size());
    }

    @Test
    public void testToModelPortfolioDto_sizeMatches() {
        List<ModelPortfolioSummaryDto> models = modelPortfolioSummaryDtoService.toModelPortfolioSummaryDto(modelPortfolioList,
                new ServiceErrorsImpl());
        assertNotNull(models);
        Assert.assertEquals(2, models.size());
    }

    @Test
    public void testToModelPortfolioDto_valueMatches() {
        List<ModelPortfolioSummaryDto> models = modelPortfolioSummaryDtoService.toModelPortfolioSummaryDto(modelPortfolioList,
                new ServiceErrorsImpl());
        Assert.assertEquals(modelPortfolioList.get(0).getModelKey().getId(), models.get(0).getKey().getModelId());
        Assert.assertEquals(modelPortfolioList.get(0).getModelName(), models.get(0).getModelName());
        Assert.assertEquals(modelPortfolioList.get(0).getModelCode(), models.get(0).getModelCode());
        Assert.assertEquals(modelPortfolioList.get(0).getLastUpdateDate(), models.get(0).getLastUpdateDate());
        Assert.assertEquals(modelPortfolioList.get(0).getLastUpdatedBy(), models.get(0).getLastUpdatedBy());
        Assert.assertEquals(modelPortfolioList.get(0).getAssetClass(), models.get(0).getAssetClass());
        Assert.assertEquals(modelPortfolioList.get(0).getInvestmentStyle(), models.get(0).getInvestmentStyle());
        Assert.assertEquals(ModelType.INVESTMENT, ModelType.forName(models.get(0).getAccountType()));
        Assert.assertEquals(modelPortfolioList.get(0).getApirCode(), models.get(0).getApirCode());
        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(), models.get(0).getAccountType());
        Assert.assertEquals(ConstructionType.FIXED.getDisplayValue(), models.get(0).getModelConstruction());
        Assert.assertEquals(modelPortfolioList.get(0).getModelDescription(), models.get(0).getModelDescription());
        Assert.assertEquals(modelPortfolioList.get(0).getOpenDate(), models.get(0).getOpenDate());
        Assert.assertEquals(modelPortfolioList.get(0).getNumAccounts(), models.get(0).getTotalAccountsCount());

        Assert.assertEquals(StringUtils.capitalize(modelPortfolioList.get(0).getStatus().getName().toLowerCase()), models.get(0)
                .getStatus());
        Assert.assertEquals(modelPortfolioList.get(0).getFum(), models.get(0).getFum());
        Assert.assertEquals(assetMap.get(asset1.getAssetId()).getAssetType().getDisplayName(), models.get(0).getAssetType()
                .getDisplayName());

        Assert.assertEquals("", models.get(1).getAccountType());
    }

    @Test
    public void testFindAll_ModelPortfolio() {
        UserProfile profile = Mockito.mock(UserProfile.class);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(profile);

        // DG
        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.DEALER);
        Mockito.when(brokerService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(broker));

        List<ModelPortfolioSummary> mockList = Collections.emptyList();
        Mockito.when(modelPortfolioSummaryService.loadModels(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(mockList);
        List<ModelPortfolioSummaryDto> models = modelPortfolioSummaryDtoService.findAll(new ServiceErrorsImpl());
        Assert.assertTrue(models.isEmpty());

        PowerMockito.mockStatic(Properties.class);
        Mockito.when(Properties.getSafeBoolean("feature.model.advisermodel")).thenReturn(false);
        Mockito.when(modelPortfolioSummaryService.loadModels(eq(BrokerKey.valueOf("adviser")), Mockito.any(ServiceErrors.class)))
                .thenReturn(mockList);
        Mockito.when(modelPortfolioSummaryService.loadModels(eq(BrokerKey.valueOf("dealer")), Mockito.any(ServiceErrors.class)))
                .thenReturn(modelPortfolioList);
        // Dealer of an adviser
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        Mockito.when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("dealer"));
        Mockito.when(broker.getKey()).thenReturn(BrokerKey.valueOf("adviser"));
        models = modelPortfolioSummaryDtoService.findAll(new ServiceErrorsImpl());
        Assert.assertTrue(models.size() == 2);

        // Adviser level
        Mockito.when(Properties.getSafeBoolean("feature.model.advisermodel")).thenReturn(true);
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.ADVISER);
        Mockito.when(broker.getDealerKey()).thenReturn(BrokerKey.valueOf("adviser"));
        models = modelPortfolioSummaryDtoService.findAll(new ServiceErrorsImpl());
        Assert.assertTrue(models.isEmpty());

    }

    @Test
    public void testFindAll_NullBrokerKey() {
        UserProfile profile = Mockito.mock(UserProfile.class);
        Mockito.when(userProfileService.getActiveProfile()).thenReturn(profile);
        Mockito.when(brokerService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(null);
        List<ModelPortfolioSummaryDto> models = modelPortfolioSummaryDtoService.findAll(new ServiceErrorsImpl());
        Assert.assertTrue(models.isEmpty());

        Broker broker = Mockito.mock(Broker.class);
        Mockito.when(broker.getBrokerType()).thenReturn(BrokerType.OFFICE);
        Mockito.when(brokerService.getBrokersForJob(Mockito.any(JobProfileIdentifier.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(Collections.singletonList(broker));
        models = modelPortfolioSummaryDtoService.findAll(new ServiceErrorsImpl());
        Assert.assertTrue(models.isEmpty());
    }
}
