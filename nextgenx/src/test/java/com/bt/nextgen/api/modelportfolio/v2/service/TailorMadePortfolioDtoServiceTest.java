package com.bt.nextgen.api.modelportfolio.v2.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.asset.ModelAssetClass;
import com.bt.nextgen.service.integration.modelportfolio.UploadAssetCodeEnum;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
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
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TailorMadePortfolioDtoServiceTest {
    @InjectMocks
    private final TailorMadePortfolioDtoServiceImpl tmpDtoService = new TailorMadePortfolioDtoServiceImpl();

    @Mock
    private AssetIntegrationService assetService;

    private ServiceErrors serviceErrors = new ServiceErrorsImpl();

    @Before
    public void setup() throws Exception {
        Asset tmpCashAsset = mock(Asset.class);
        when(tmpCashAsset.getAssetId()).thenReturn("assetId");
        when(tmpCashAsset.getAssetName()).thenReturn("assetName");
        when(tmpCashAsset.getAssetType()).thenReturn(AssetType.CASH);
        when(tmpCashAsset.getIsin()).thenReturn("Isin");
        when(tmpCashAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        when(tmpCashAsset.getModelAssetClass()).thenReturn(ModelAssetClass.CASH);
        when(tmpCashAsset.isPrePensionRestricted()).thenReturn(false);
        when(tmpCashAsset.getMoneyAccountType()).thenReturn("Tailor Made Portfolio");

        Asset tmpSuperCashAsset = mock(Asset.class);
        when(tmpSuperCashAsset.getAssetId()).thenReturn("superAssetId");
        when(tmpSuperCashAsset.getAssetName()).thenReturn("assetName");
        when(tmpSuperCashAsset.getAssetType()).thenReturn(AssetType.CASH);
        when(tmpSuperCashAsset.getIsin()).thenReturn("Isin");
        when(tmpSuperCashAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        when(tmpSuperCashAsset.getModelAssetClass()).thenReturn(ModelAssetClass.CASH);
        when(tmpSuperCashAsset.isPrePensionRestricted()).thenReturn(false);
        when(tmpSuperCashAsset.getMoneyAccountType()).thenReturn("Super Tailor Made Portfolio");

        Asset cashAsset = mock(Asset.class);
        when(cashAsset.getAssetId()).thenReturn("cashAssetId");
        when(cashAsset.getAssetName()).thenReturn("assetName");
        when(cashAsset.getAssetType()).thenReturn(AssetType.CASH);
        when(cashAsset.getIsin()).thenReturn("Isin");
        when(cashAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        when(cashAsset.getModelAssetClass()).thenReturn(ModelAssetClass.CASH);
        when(cashAsset.isPrePensionRestricted()).thenReturn(false);
        when(cashAsset.getMoneyAccountType()).thenReturn("Cash Asset");

        Asset cashAsset2 = mock(Asset.class);
        when(cashAsset2.getAssetId()).thenReturn("cashAssetId2");
        when(cashAsset2.getAssetName()).thenReturn("assetName");
        when(cashAsset2.getAssetType()).thenReturn(AssetType.CASH);
        when(cashAsset2.getIsin()).thenReturn("Isin");
        when(cashAsset2.getStatus()).thenReturn(AssetStatus.OPEN);
        when(cashAsset2.getModelAssetClass()).thenReturn(ModelAssetClass.CASH);
        when(cashAsset2.isPrePensionRestricted()).thenReturn(false);
        when(cashAsset2.getMoneyAccountType()).thenReturn("Cash Asset");

        Map<String, Asset> assetMap = new HashMap<>();
        assetMap.put("cashAssetId", cashAsset);
        assetMap.put("assetId", tmpCashAsset);
        assetMap.put("cashAssetId2", cashAsset2);
        assetMap.put("superAssetId", tmpSuperCashAsset);

        when(
                assetService.loadAssetsForCriteria(Mockito.anyCollection(), any(String.class), Mockito.anyCollection(),
                        any(ServiceErrors.class))).thenReturn(assetMap);
    }

    @Test
    public void testRetrieveTmpCash_validCashAssetReturn() {
        AssetDto assetDto = tmpDtoService.findTmpCashAssetForModel(ModelType.INVESTMENT, serviceErrors);
        Assert.assertEquals(UploadAssetCodeEnum.TMP_CASH.value(), assetDto.getAssetCode());
    }

    @Test
    public void testRetrieveSuperTmpCash_validCashAssetReturn() {
        AssetDto assetDto = tmpDtoService.findTmpCashAssetForModel(ModelType.SUPERANNUATION, serviceErrors);
        Assert.assertEquals(UploadAssetCodeEnum.SUPER_TMP_CASH.value(), assetDto.getAssetCode());

        assetDto = tmpDtoService.findTmpCashAssetForModel(null, serviceErrors);
        Assert.assertTrue(assetDto == null);
    }

    @Test
    public void testSearchForTMPAndSuper_thenValidCashAssetIsReturn() {

        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.INVESTMENT.getCode(), ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(searchCriteria);

        List<AssetDto> assetDtoList = tmpDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, assetDtoList.size());
        Assert.assertEquals(UploadAssetCodeEnum.TMP_CASH.value(), assetDtoList.get(0).getAssetCode());

        searchCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.SUPERANNUATION.getCode(), ApiSearchCriteria.OperationType.STRING);
        criteriaList = Collections.singletonList(searchCriteria);

        assetDtoList = tmpDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertEquals(1, assetDtoList.size());
        Assert.assertEquals(UploadAssetCodeEnum.SUPER_TMP_CASH.value(), assetDtoList.get(0).getAssetCode());
    }

    @Test
    public void testFindOne_ForTMP_thenTmpCashAssetIsReturn() {
        AssetDto dto = tmpDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertTrue(dto != null);
        Assert.assertEquals(UploadAssetCodeEnum.TMP_CASH.value(), dto.getAssetCode());
    }

    @Test
    public void testWhenNoValidAssetCanBeRetrieved_noCashAssetReturn() {
        Map<String, Asset> assetMap = new HashMap<>();
        when(
                assetService.loadAssetsForCriteria(Mockito.anyCollection(), any(String.class), Mockito.anyCollection(),
                        any(ServiceErrors.class))).thenReturn(assetMap);

        AssetDto assetDto = tmpDtoService.findTmpCashAssetForModel(ModelType.SUPERANNUATION, serviceErrors);
        Assert.assertTrue(assetDto == null);
        assetDto = tmpDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertTrue(assetDto == null);

        assetDto = tmpDtoService.findOne(new ServiceErrorsImpl());
        Assert.assertTrue(assetDto == null);

        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("modelType", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.INVESTMENT.getCode(), ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(searchCriteria);
        List<AssetDto> assetDtoList = tmpDtoService.search(criteriaList, new ServiceErrorsImpl());
        Assert.assertTrue(assetDtoList == null);

    }

    @Test
    public void testSearchWithInvalidModel_defaultCashAssetReturn() {

        ApiSearchCriteria searchCriteria = new ApiSearchCriteria("invalidField", ApiSearchCriteria.SearchOperation.EQUALS,
                ModelType.SUPERANNUATION.getCode(), ApiSearchCriteria.OperationType.STRING);
        List<ApiSearchCriteria> criteriaList = Collections.singletonList(searchCriteria);

        List<AssetDto> assetDtoList = tmpDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(1, assetDtoList.size());
        Assert.assertEquals(UploadAssetCodeEnum.TMP_CASH.value(), assetDtoList.get(0).getAssetCode());

    }

    @Test(expected = IllegalArgumentException.class)
    public void test_uploadAssetCodeEmptyValue() {
        UploadAssetCodeEnum val = UploadAssetCodeEnum.fromValue("value");
    }

    @Test
    public void test_uploadAssetCode() {
        UploadAssetCodeEnum val = UploadAssetCodeEnum.fromValue("MACC.STMP.AUD");
        Assert.assertEquals("MACC.STMP.AUD", val.value());
    }

}
