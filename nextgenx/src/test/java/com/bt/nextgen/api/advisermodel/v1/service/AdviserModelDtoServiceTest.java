package com.bt.nextgen.api.advisermodel.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.exception.NotFoundException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdviserModelDtoServiceTest {

    @InjectMocks
    private AdviserModelDtoServiceImpl adviserModelDtoService;

    @Mock
    private AssetIntegrationService assetService;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();
    private Asset tmpCash;
    private Asset tmpSuperCash;
    private Asset defaultCash;
    private Asset adviserModelCash;

    @Before
    public void setup() {
        Asset tmpCash = mock(Asset.class);
        when(tmpCash.getAssetId()).thenReturn("assetId");
        when(tmpCash.getMoneyAccountType()).thenReturn("Tailor Made Portfolio");
        this.tmpCash = tmpCash;

        Asset tmpSuperCash = mock(Asset.class);
        when(tmpSuperCash.getAssetId()).thenReturn("superAssetId");
        when(tmpSuperCash.getMoneyAccountType()).thenReturn("Super Tailor Made Portfolio");
        this.tmpSuperCash = tmpSuperCash;

        Asset defaultCash = mock(Asset.class);
        when(defaultCash.getAssetId()).thenReturn("cashAssetId");
        when(defaultCash.getMoneyAccountType()).thenReturn("Cash Asset");
        this.defaultCash = defaultCash;

        Asset adviserModelCash = mock(Asset.class);
        when(adviserModelCash.getAssetId()).thenReturn("adviserModelAssetId");
        when(adviserModelCash.getAssetType()).thenReturn(AssetType.CASH);
        when(adviserModelCash.getMoneyAccountType()).thenReturn("Generic Model Cash");
        this.adviserModelCash = adviserModelCash;
    }

    @Test
    public void testLoadCashAsset_whenAdviserModelCashRequested_thenAdviserModelCashReturned() {
        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetId", tmpCash);
        assetMap.put("superAssetId", tmpSuperCash);
        assetMap.put("cashAssetId", defaultCash);
        assetMap.put("adviserModelAssetId", adviserModelCash);

        when(
                assetService.loadAssetsForCriteria(Mockito.anyCollectionOf(String.class), Mockito.any(String.class),
                        Mockito.anyCollectionOf(AssetType.class), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        AssetDto assetDto = adviserModelDtoService.findOne(serviceErrors);

        Assert.assertEquals(UploadAssetCodeEnum.ADVISER_MODEL_CASH.value(), assetDto.getAssetCode());
    }

    @Test(expected = NotFoundException.class)
    public void testLoadCashAsset_whenNoCashAssetsReturned_thenNotFoundExceptionThrown() {
        when(
                assetService.loadAssetsForCriteria(Mockito.anyCollectionOf(String.class), Mockito.any(String.class),
                        Mockito.anyCollectionOf(AssetType.class), Mockito.any(ServiceErrors.class))).thenReturn(
                Collections.<String, Asset> emptyMap());

        adviserModelDtoService.findOne(serviceErrors);
    }

    @Test(expected = NotFoundException.class)
    public void testLoadCashAsset_whenAdviserModelCashNotFound_thenNotFoundExceptionThrown() {
        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("assetId", tmpCash);
        assetMap.put("superAssetId", tmpSuperCash);
        assetMap.put("cashAssetId", defaultCash);

        when(
                assetService.loadAssetsForCriteria(Mockito.anyCollectionOf(String.class), Mockito.any(String.class),
                        Mockito.anyCollectionOf(AssetType.class), Mockito.any(ServiceErrors.class))).thenReturn(assetMap);

        adviserModelDtoService.findOne(serviceErrors);
    }
}
