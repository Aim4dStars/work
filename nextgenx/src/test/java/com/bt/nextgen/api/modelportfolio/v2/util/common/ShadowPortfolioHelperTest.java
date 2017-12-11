package com.bt.nextgen.api.modelportfolio.v2.util.common;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.modelportfolio.v2.model.ModelPortfolioSummaryDto;
import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioSummaryDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ModelPortfolioSummaryImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioAssetSummaryImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.ShadowPortfolioDetailImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAsset;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioAssetSummary;
import com.bt.nextgen.service.integration.modelportfolio.ShadowPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ShadowPortfolioHelperTest {

    @InjectMocks
    private ShadowPortfolioHelper helperService;

    @Mock
    private ModelPortfolioSummaryDtoService modelPortfolioSummaryService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private ModelPortfolioIntegrationService mpService;

    private FailFastErrorsImpl errorsImpl = new FailFastErrorsImpl();

    @Before
    public void setup() throws Exception {
        // Setup mock for modelPortfolioDtoSummary list.
        ModelPortfolioSummaryImpl summaryModel_1 = Mockito.mock(ModelPortfolioSummaryImpl.class);
        Mockito.when(summaryModel_1.getModelKey()).thenReturn(IpsKey.valueOf("modelKey_1"));
        Mockito.when(summaryModel_1.getIpsOrderId()).thenReturn("ipsOrderId_1");
        Mockito.when(summaryModel_1.getModelOrderId()).thenReturn("modelOrderId_1");
        Mockito.when(summaryModel_1.getAssetClass()).thenReturn("assetClass");
        Mockito.when(summaryModel_1.getStatus()).thenReturn(IpsStatus.OPEN);
        Mockito.when(summaryModel_1.getAccountType()).thenReturn(ModelType.INVESTMENT);

        ModelPortfolioSummaryImpl summaryModel_2 = Mockito.mock(ModelPortfolioSummaryImpl.class);
        Mockito.when(summaryModel_2.getModelKey()).thenReturn(IpsKey.valueOf("modelKey_2"));
        Mockito.when(summaryModel_2.getIpsOrderId()).thenReturn("ipsOrderId_2");
        Mockito.when(summaryModel_2.getModelOrderId()).thenReturn("modelOrderId_2");
        Mockito.when(summaryModel_2.getAssetClass()).thenReturn("assetClass");
        Mockito.when(summaryModel_2.getStatus()).thenReturn(IpsStatus.OPEN);
        Mockito.when(summaryModel_2.getAccountType()).thenReturn(ModelType.INVESTMENT);

        List<ModelPortfolioSummaryDto> dtoList = new ArrayList<>();
        dtoList.add(new ModelPortfolioSummaryDto(summaryModel_1, AssetType.OTHER));
        dtoList.add(new ModelPortfolioSummaryDto(summaryModel_2, AssetType.OTHER));

        Mockito.when(modelPortfolioSummaryService.findAll(Mockito.any(ServiceErrors.class))).thenReturn(dtoList);

        // Mock for assetService
        Asset a1 = Mockito.mock(AssetImpl.class);
        Mockito.when(a1.getAssetId()).thenReturn("id1");

        Asset a2 = Mockito.mock(AssetImpl.class);
        Mockito.when(a2.getAssetId()).thenReturn("id2");

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("id1", a1);
        assetMap.put("id2", a2);
        Mockito.when(assetService.loadAssets(Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        // Mock for assetDtoConverter.
        AssetDto dto1 = Mockito.mock(AssetDto.class);
        Mockito.when(dto1.getAssetId()).thenReturn("id1");

        AssetDto dto2 = Mockito.mock(AssetDto.class);
        Mockito.when(dto2.getAssetId()).thenReturn("id2");

        Map<String, AssetDto> assetDtoMap = new HashMap<>();
        assetDtoMap.put("id1", dto1);
        assetDtoMap.put("id2", dto2);

        Mockito.when(assetDtoConverter.toAssetDto(Mockito.anyMap(), Mockito.anyMap())).thenReturn(assetDtoMap);
    }

    @Test
    public void testWhenGetFloatingTargetAllocationMap_noNullMapReturn() {
        // No shadow portfolio retrieved.
        Mockito.when(mpService.loadShadowPortfolioModel(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                null);
        Map<String, BigDecimal> results = helperService.getFloatingTargetAllocationMap(IpsKey.valueOf("999"),
                new ServiceErrorsImpl());
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);

        // Shadow portfolio with no asset-summary.
        ShadowPortfolio shadowPortfolio = Mockito.mock(ShadowPortfolio.class);
        Mockito.when(shadowPortfolio.getAssetSummaries()).thenReturn(null);
        Mockito.when(mpService.loadShadowPortfolioModel(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                shadowPortfolio);
        results = helperService.getFloatingTargetAllocationMap(IpsKey.valueOf("999"), new ServiceErrorsImpl());
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);

        // Asset-summary with no underlying asset.
        ShadowPortfolioAssetSummaryImpl assetSummary = Mockito.mock(ShadowPortfolioAssetSummaryImpl.class);
        Mockito.when(assetSummary.getAssets()).thenReturn(null);
        results = helperService.getFloatingTargetAllocationMap(IpsKey.valueOf("999"), new ServiceErrorsImpl());
        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 0);
    }
    
    @Test
    public void testWhenGetFloatingTargetAllocationMap_thenFloatingTargetPercentRetrieved() {
        ShadowPortfolioDetailImpl sDetail = Mockito.mock(ShadowPortfolioDetailImpl.class);
        Mockito.when(sDetail.getFloatingTargetPercent()).thenReturn(BigDecimal.ONE);

        ShadowPortfolioAssetImpl shadowAsset = Mockito.mock(ShadowPortfolioAssetImpl.class);
        Mockito.when(shadowAsset.getAssetId()).thenReturn("assetId");
        Mockito.when(shadowAsset.getShadowDetail()).thenReturn((ShadowPortfolioDetail) sDetail);
        
        ShadowPortfolioAssetSummaryImpl assetSummary = Mockito.mock(ShadowPortfolioAssetSummaryImpl.class);
        Mockito.when(assetSummary.getAssets()).thenReturn(Collections.singletonList((ShadowPortfolioAsset) shadowAsset));
        
        ShadowPortfolio shadowPortfolio = Mockito.mock(ShadowPortfolio.class);
        Mockito.when(shadowPortfolio.getAssetSummaries()).thenReturn(
                Collections.singletonList((ShadowPortfolioAssetSummary) assetSummary));
        
        Mockito.when(mpService.loadShadowPortfolioModel(Mockito.any(IpsKey.class), Mockito.any(ServiceErrors.class))).thenReturn(
                shadowPortfolio);
        Map<String, BigDecimal> results = helperService.getFloatingTargetAllocationMap(IpsKey.valueOf("999"),
                new ServiceErrorsImpl());

        Assert.assertNotNull(results);
        Assert.assertTrue(results.size() == 1);
        
        BigDecimal valueReturn = results.get("assetId");
        Assert.assertNotNull(valueReturn);
        Assert.assertEquals(BigDecimal.ONE, valueReturn);
    }
}
