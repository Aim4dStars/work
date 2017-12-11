package com.bt.nextgen.api.asset.controller;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AvailableShareAssetDtoServiceImpl;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
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
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class AvailableShareAssetDtoServiceTest {

    @InjectMocks
    AvailableShareAssetDtoServiceImpl availableShareAssetDtoService;
    private final List<ApiSearchCriteria> apiSearchCriteriaList = new ArrayList<>();
    @Mock
    private List<ApiSearchCriteria> apiSearchCriteriaListMock;

    @Mock
    ApiSearchCriteria apiSearchCriteriaquer;

    @Mock
    ApiSearchCriteria apiSearchCriteriatype;
    @Mock
    private ServiceErrors serviceErrors;

    @Mock
    private AssetIntegrationService assetService;

    private Asset suspendedAsset;
    private Asset terminatedAsset;
    private Asset delistedAsset;
    private Asset closedToNewAsset;
    private Asset closedAsset;
    private Asset openAsset;
    private Asset nullStatusAsset;
    private Asset nullIsinAsset;

    private Map<String, Asset> assetMap;

    @Before
    public void setUp() throws Exception {
        suspendedAsset = Mockito.mock(Asset.class);
        Mockito.when(suspendedAsset.getAssetId()).thenReturn("suspendedAsset");
        Mockito.when(suspendedAsset.getStatus()).thenReturn(AssetStatus.SUSPENDED);
        Mockito.when(suspendedAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(suspendedAsset.getIsin()).thenReturn("1234");

        terminatedAsset = Mockito.mock(Asset.class);
        Mockito.when(terminatedAsset.getAssetId()).thenReturn("terminatedAsset");
        Mockito.when(terminatedAsset.getStatus()).thenReturn(AssetStatus.TERMINATED);
        Mockito.when(terminatedAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(terminatedAsset.getIsin()).thenReturn("1234");

        delistedAsset = Mockito.mock(Asset.class);
        Mockito.when(delistedAsset.getAssetId()).thenReturn("delistedAsset");
        Mockito.when(delistedAsset.getStatus()).thenReturn(AssetStatus.DELISTED);
        Mockito.when(delistedAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(delistedAsset.getIsin()).thenReturn("1234");

        closedToNewAsset = Mockito.mock(Asset.class);
        Mockito.when(closedToNewAsset.getAssetId()).thenReturn("closedToNewAsset");
        Mockito.when(closedToNewAsset.getStatus()).thenReturn(AssetStatus.CLOSED_TO_NEW);
        Mockito.when(closedToNewAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(closedToNewAsset.getIsin()).thenReturn("1234");

        closedAsset = Mockito.mock(Asset.class);
        Mockito.when(closedAsset.getAssetId()).thenReturn("closedAsset");
        Mockito.when(closedAsset.getStatus()).thenReturn(AssetStatus.CLOSED);
        Mockito.when(closedAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(closedAsset.getIsin()).thenReturn("1234");

        openAsset = Mockito.mock(Asset.class);
        Mockito.when(openAsset.getAssetId()).thenReturn("openAsset");
        Mockito.when(openAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(openAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(openAsset.getIsin()).thenReturn("1234");

        nullStatusAsset = Mockito.mock(Asset.class);
        Mockito.when(nullStatusAsset.getAssetId()).thenReturn("nullStatusAsset");
        Mockito.when(nullStatusAsset.getAssetType()).thenReturn(AssetType.SHARE);
        Mockito.when(nullStatusAsset.getIsin()).thenReturn("1234");

        nullIsinAsset = Mockito.mock(Asset.class);
        Mockito.when(nullIsinAsset.getAssetId()).thenReturn("nullIsinAsset");
        Mockito.when(nullIsinAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(nullIsinAsset.getAssetType()).thenReturn(AssetType.SHARE);

        assetMap = new HashMap<>();
        assetMap.put(suspendedAsset.getAssetId(), suspendedAsset);
        assetMap.put(terminatedAsset.getAssetId(), terminatedAsset);
        assetMap.put(delistedAsset.getAssetId(), delistedAsset);
        assetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        assetMap.put(closedAsset.getAssetId(), closedAsset);
        assetMap.put(openAsset.getAssetId(), openAsset);
        assetMap.put(nullStatusAsset.getAssetId(), nullStatusAsset);
        assetMap.put(nullIsinAsset.getAssetId(), nullIsinAsset);
    }

    @Test
    public void testSearchMethod() throws Exception {
        Map map = new HashMap<>();

        apiSearchCriteriaList.add(apiSearchCriteriaquer);
        apiSearchCriteriaList.add(apiSearchCriteriatype);
        Mockito.when(apiSearchCriteriaquer.getProperty()).thenReturn("query");
        Mockito.when(apiSearchCriteriaquer.getValue()).thenReturn("westpac");
        Mockito.when(apiSearchCriteriatype.getProperty()).thenReturn("asset_type");
        Mockito.when(apiSearchCriteriatype.getValue()).thenReturn("SHARE");
        Mockito.when(assetService.loadShareAssetsForCriteria(Mockito.anyString(), Mockito.anyCollection(),
                Mockito.any(ServiceErrors.class))).thenReturn(map);

        availableShareAssetDtoService.search(apiSearchCriteriaList, serviceErrors);
        Mockito.verify(assetService, Mockito.times(1)).loadShareAssetsForCriteria(Mockito.anyString(), Mockito.anyCollection(),
                Mockito.any(ServiceErrors.class));
        Mockito.verify(apiSearchCriteriaquer).getProperty();
        Mockito.verify(apiSearchCriteriatype).getValue();
        Mockito.verify(apiSearchCriteriaquer).getValue();
        Mockito.verify(apiSearchCriteriatype).getProperty();
    }

    @Test
    public void testIsAssetStatusSellable() {
        Set<AssetDto> assetDtos = availableShareAssetDtoService.filterAssets(assetMap);
        Assert.assertEquals(4, assetDtos.size());
        for (AssetDto assetDto : assetDtos) {
            Assert.assertNotEquals(suspendedAsset.getAssetId(), assetDto.getAssetId());
            Assert.assertNotEquals(terminatedAsset.getAssetId(), assetDto.getAssetId());
            Assert.assertNotEquals(delistedAsset.getAssetId(), assetDto.getAssetId());
            Assert.assertNotEquals(nullIsinAsset.getAssetId(), assetDto.getAssetId());
        }
    }
}
