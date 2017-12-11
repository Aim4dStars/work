package com.bt.nextgen.api.trading.v1.controller;

import com.bt.nextgen.api.trading.v1.service.BrokerTradableAssetsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Assert;
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
public class BrokerTradableAssetsApiControllerTest {

    @InjectMocks
    private BrokerTradableAssetsApiController brokerTradableAssetsApiController;

    @Mock
    private BrokerTradableAssetsDtoService brokerAssetsDtoService;

    @Mock
    private JsonObjectMapper mapper;

    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> searchCriteriaCaptor;

    @Test
    public void testSearchTradableAssets_whenAllParameters_thenAllSearchCriteria() throws IOException {
        String assetIds = "[\"123\",\"234\",\"345\"]";
        List<String> assetIdList = Arrays.asList("123", "234", "345");
        Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenReturn(assetIdList);

        brokerTradableAssetsApiController.searchTradableAssets("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "query",
                "SHARE", assetIds);

        verify(brokerAssetsDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(4, apiSearchCriteria.size());
        Assert.assertEquals("productId", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("12345", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals("query", apiSearchCriteria.get(1).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(2).getProperty());
        Assert.assertEquals("SHARE", apiSearchCriteria.get(2).getValue());
        Assert.assertEquals("assetIds", apiSearchCriteria.get(3).getProperty());
        Assert.assertEquals("123,234,345", apiSearchCriteria.get(3).getValue());
    }

    @Test
    public void testSearchBenchmarks_whenAllParameters_thenAllSearchCriteria() throws IOException {
        brokerTradableAssetsApiController.searchBenchmarks("query", "");

        verify(brokerAssetsDtoService, atLeastOnce()).search(searchCriteriaCaptor.capture(), Mockito.any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteria = searchCriteriaCaptor.getValue();
        Assert.assertEquals(2, apiSearchCriteria.size());
        Assert.assertEquals("query", apiSearchCriteria.get(0).getProperty());
        Assert.assertEquals("query", apiSearchCriteria.get(0).getValue());
        Assert.assertEquals("assetType", apiSearchCriteria.get(1).getProperty());
        Assert.assertEquals(AssetType.INDEX.name(), apiSearchCriteria.get(1).getValue());
    }

    @Test(expected = BadRequestException.class)
    public void testSearchTradableassets_whenIdsNotParsed_thenException()
            throws JsonParseException, JsonMappingException, IOException {
        Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenThrow(IOException.class);

        brokerTradableAssetsApiController.searchTradableAssets("DB373E6005CF13D441A2BEB45C4C6848E4D3F1643FE90B90", "", "SHARE",
                "dsflkjsdf<]");
    }
}
