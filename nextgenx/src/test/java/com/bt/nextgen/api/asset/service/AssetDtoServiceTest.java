package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetDtoServiceTest {
    @InjectMocks
    private AssetDtoServiceImpl assetDtoService;

    @Mock
    private AssetIntegrationService assetService;

    @Mock
    private AssetDtoConverter assetDtoConverter;

    @Captor
    private ArgumentCaptor<Collection<String>> listArgumentCaptor;

    @Captor
    private ArgumentCaptor<Collection<AssetKey>> assetAllocArgCaptor;

    private List<Asset> assetList = new ArrayList<>();

    @Test
    public void testSearchAssetBasedOnCodes_assetCodesAreParsedCorrectly() {

        List<String> codeList = new ArrayList<>();
        codeList.add("bhp");
        codeList.add("1234");

        List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        ApiSearchCriteria c = new ApiSearchCriteria("assetCodes", SearchOperation.LIST_CONTAINS, StringUtils.join(codeList, ','),
                OperationType.STRING);
        criteriaList.add(c);

        List<AssetDto> dtoList = assetDtoService.search(criteriaList, new FailFastErrorsImpl());

        verify(assetService).loadAssetsForAssetCodes(Mockito.anyCollection(), Mockito.any(ServiceErrors.class));
        verify(assetService).loadAssetsForAssetCodes(listArgumentCaptor.capture(), Mockito.any(ServiceErrorsImpl.class));

        Collection<String> assetCodes = listArgumentCaptor.getValue();
        Assert.assertEquals(2, assetCodes.size());

        for (String code : assetCodes) {
            Assert.assertTrue(codeList.contains(code));
        }
    }

    @Test
    public void testSearchAssetBasedOnCodes_withManagedFunds() {
        ManagedFundAsset mfAsset = mock(ManagedFundAsset.class);
        when(mfAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        when(mfAsset.getAssetId()).thenReturn("12345");
        assetList.add(mfAsset);

        when(assetService.loadAssetsForAssetCodes(Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(assetList);

        assetDtoService.search(new ArrayList<ApiSearchCriteria>(), new FailFastErrorsImpl());
        verify(assetService).loadAssetAllocations(Mockito.anyCollection(), Mockito.any(DateTime.class), Mockito.any(ServiceErrors.class));
        verify(assetService).loadAssetAllocations(assetAllocArgCaptor.capture(), Mockito.any(DateTime.class), Mockito.any(ServiceErrorsImpl.class));
    }
}
