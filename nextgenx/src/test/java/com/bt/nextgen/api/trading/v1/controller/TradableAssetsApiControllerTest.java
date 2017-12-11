package com.bt.nextgen.api.trading.v1.controller;

import com.bt.nextgen.api.trading.v1.model.AvailableAssetInfoDto;
import com.bt.nextgen.api.trading.v1.service.AvailableAssetInfoDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableAssetsCountDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableAssetsDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableAssetsTypeDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableInvestmentOptionsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TradableAssetsApiControllerTest {
    @InjectMocks
    private TradableAssetsApiController tradableAssetsController;

    @Mock
    private TradableAssetsDtoService tradeableAssetsDtoService;

    @Mock
    private TradableAssetsTypeDtoService tradableAssetsTypeDtoService;

    @Mock
    private TradableAssetsCountDtoService tradableAssetsCountDtoService;

    @Mock
    private TradableInvestmentOptionsDtoService tradableInvestmentOptionsDtoService;

    @Mock
    private AvailableAssetInfoDtoService availableAssetInfoDtoService;

    @Mock
    private JsonObjectMapper mapper;

    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> searchCriteriaCaptor;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSearchTradableAssets_whenAllParameters_thenAllSearchCriteria() throws IOException {
        String assetIds = "[\"123\",\"234\",\"345\"]";
        List<String> assetIdList = Arrays.asList("123", "234", "345");
        Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenReturn(assetIdList);

        tradableAssetsController.searchTradableAssets("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "query", "share",
                assetIds);

        verify(tradeableAssetsDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(5, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("share", apiSearchCriteria.get(2).getValue());
        Assert.assertEquals("filterAal", apiSearchCriteria.get(3).getProperty());
        Assert.assertEquals("true", apiSearchCriteria.get(3).getValue());
        Assert.assertEquals("assetIds", apiSearchCriteria.get(4).getProperty());
        Assert.assertEquals("123,234,345", apiSearchCriteria.get(4).getValue());
    }

    @Test
    public void testSearchAllAssets_whenParametersPassed_thenSearchCriteriaMatches() throws IOException {
        tradableAssetsController.searchAllAssets("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "query", "share", "");

        verify(tradeableAssetsDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(4, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("share", apiSearchCriteria.get(2).getValue());
        Assert.assertEquals("filterAal", apiSearchCriteria.get(3).getProperty());
        Assert.assertEquals("false", apiSearchCriteria.get(3).getValue());
    }

    @Test(expected = BadRequestException.class)
    public void testSearchTradableassets_whenIdsNotParsed_thenException()
            throws JsonParseException, JsonMappingException, IOException {
        Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenThrow(IOException.class);

        tradableAssetsController.searchTradableAssets("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "", "share",
                "dsflkjsdf<]");
    }

    @Test
    public void testSearchAssetTypes_whenParametersPassed_thenSearchCriteriaMatches() throws IOException {
        tradableAssetsController.searchAssetTypes("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90");

        verify(tradableAssetsTypeDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(),
                Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(1, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
    }

    @Test
    public void testGetTradableAssetCount_whenParametersPassed_thenSearchCriteriaMatches() throws IOException {
        tradableAssetsController.getTradableAssetCount("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", null);

        verify(tradableAssetsCountDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(),
                Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(3, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("filterAal", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("true", apiSearchCriteria.get(2).getValue());
    }

    @Test
    public void testGetTradableAssetCount_whenFilterPassed_thenSearchCriteriaMatches() throws IOException {
        String assetTypeQueryString = "[{\"prop\":\"assetType\",\"op\":\"=\",\"val\":\"share\",\"type\":\"string\"}]";
        tradableAssetsController.getTradableAssetCount("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", assetTypeQueryString);

        verify(tradableAssetsCountDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(),
                Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(4, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("filterAal", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("true", apiSearchCriteria.get(2).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(3).getProperty());
        Assert.assertEquals("share", apiSearchCriteria.get(3).getValue());
    }

    @Test
    public void testGetTradableInvestmentOptions_whenNoAssetTypePassed_thenAssetTypeManagedPortfolio() throws IOException {
        tradableAssetsController.getTradableInvestmentOptions("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "");

        verify(tradableInvestmentOptionsDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(),
                Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(4, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("filterAal", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("true", apiSearchCriteria.get(2).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(3).getProperty());
        Assert.assertEquals(AssetType.MANAGED_PORTFOLIO.getDisplayName(), apiSearchCriteria.get(3).getValue());
    }

    @Test
    public void testGetTradableInvestmentOptions_whenParametersPassed_thenSearchCriteriaMatches() throws IOException {
        tradableAssetsController.getTradableInvestmentOptions("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "share");

        verify(tradableInvestmentOptionsDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(),
                Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(4, apiSearchCriteria.size());
        Assert.assertEquals("accountId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("filterAal", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("true", apiSearchCriteria.get(2).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(3).getProperty());
        Assert.assertEquals("share", apiSearchCriteria.get(3).getValue());
    }

    @Test
    public void testGetAvailableAssetInfo_whenAccountPassed_thenFindCalled() throws IOException {
        Mockito.when(availableAssetInfoDtoService.find(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new AvailableAssetInfoDto());
        tradableAssetsController.getAvailableAssetInfo("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90");

        verify(availableAssetInfoDtoService, atLeastOnce())
                .find(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class), Mockito.any(ServiceErrors.class));
    }

    @Test(expected = AccessDeniedException.class)
    public void testGetAvailableAssetInfo_whenNoAccountPassed_thenBadRequestThrown() throws IOException {
        Mockito.when(availableAssetInfoDtoService.find(Mockito.any(com.bt.nextgen.api.account.v3.model.AccountKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(new AvailableAssetInfoDto());
        tradableAssetsController.getAvailableAssetInfo(null);
    }
}
