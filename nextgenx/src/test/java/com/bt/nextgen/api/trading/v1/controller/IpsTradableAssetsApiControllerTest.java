package com.bt.nextgen.api.trading.v1.controller;

import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.IpsTradableAssetsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class IpsTradableAssetsApiControllerTest {

    @InjectMocks
    private IpsTradableAssetsApiController ipsTradableAssetsApiController;

    @Mock
    private IpsTradableAssetsDtoService ipsTradableAssetsDtoService;

    @Mock
    private JsonObjectMapper mapper;

    @Test
    public void testSearchTradableAssets_whenAllParameters_thenAllSearchCriteria() {
        Mockito.when(
                ipsTradableAssetsDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<TradeAssetDto>>() {

                    @Override
                    public List<TradeAssetDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(3, criteria.size());

                        for (ApiSearchCriteria parameter : criteria) {
                            switch (parameter.getProperty()) {
                            case "ipsId":
                                Assert.assertEquals("ipsId", parameter.getValue());
                                break;
                            case "query":
                                Assert.assertEquals("query", parameter.getValue());
                                break;
                            case "assetType":
                                Assert.assertEquals("SHARE", parameter.getValue());
                                break;
                            default:
                                break;
                            }
                        }

                        return new ArrayList<>();
                    }

                });

        ipsTradableAssetsApiController.searchTradableAssets("ipsId", "query", "SHARE");
    }

    @Test
    public void testSearchTradableAssetsById_whenAllParameters_thenAllSearchCriteria() throws JsonParseException,
            JsonMappingException, IOException {

        String assetIds = "[\"123\",\"234\",\"345\"]";
        JsonObjectMapper mapper1 = new JsonObjectMapper();
        JsonNode nodes = mapper1.readTree(assetIds);
        Mockito.when(mapper.readTree(Mockito.anyString())).thenReturn(nodes);
        Mockito.when(
                ipsTradableAssetsDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<TradeAssetDto>>() {

                    @Override
                    public List<TradeAssetDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(4, criteria.size());

                        for (ApiSearchCriteria parameter : criteria) {
                            switch (parameter.getProperty()) {
                            case "ipsId":
                                Assert.assertEquals("ipsId", parameter.getValue());
                                break;
                            case "query":
                                Assert.assertEquals("  ", parameter.getValue());
                                break;
                            case "assetIds":
                                Assert.assertEquals("123,234,345", parameter.getValue());
                                break;
                            case "assetType":
                                Assert.assertEquals("SHARE|MANAGED_FUND", parameter.getValue());
                                break;
                            default:
                                break;
                            }
                        }

                        return new ArrayList<>();
                    }

                });
        ipsTradableAssetsApiController.searchTradableAssetsById("ipsId", assetIds, "SHARE|MANAGED_FUND");
    }

    @Test
    public void testSearchTradableAssetsByCode_whenAllParameters_thenAllSearchCriteria() throws JsonParseException,
            JsonMappingException, IOException {

        String assetCodes = "[\"WOW\",\"BHP\"]";
        JsonObjectMapper mapper1 = new JsonObjectMapper();
        JsonNode nodes = mapper1.readTree(assetCodes);
        Mockito.when(mapper.readTree(Mockito.anyString())).thenReturn(nodes);

        Mockito.when(
                ipsTradableAssetsDtoService.search(Mockito.anyListOf(ApiSearchCriteria.class), Mockito.any(ServiceErrors.class)))
                .thenAnswer(new Answer<List<TradeAssetDto>>() {

                    @Override
                    public List<TradeAssetDto> answer(InvocationOnMock invocation) throws Throwable {
                        List<ApiSearchCriteria> criteria = (List<ApiSearchCriteria>) invocation.getArguments()[0];
                        Assert.assertEquals(4, criteria.size());

                        for (ApiSearchCriteria parameter : criteria) {
                            switch (parameter.getProperty()) {
                            case "ipsId":
                                Assert.assertEquals("ipsId", parameter.getValue());
                                break;
                            case "query":
                                Assert.assertEquals("  ", parameter.getValue());
                                break;
                            case "assetCodes":
                                Assert.assertEquals("WOW,BHP", parameter.getValue());
                                break;
                            case "assetType":
                                Assert.assertEquals("SHARE", parameter.getValue());
                                break;
                            default:
                                break;
                            }
                        }

                        return new ArrayList<>();
                    }

                });

        ipsTradableAssetsApiController.searchTradableAssetsByCode("ipsId", assetCodes, "SHARE");
    }

    @Test(expected = BadRequestException.class)
    public void testSearchTradableassetsById_whenIdsNotParsed_thenException() throws JsonParseException, JsonMappingException,
            IOException {
        Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenThrow(IOException.class);

        ipsTradableAssetsApiController.searchTradableAssetsById("ipsId", "", "SHARE");
    }

    @Test(expected = BadRequestException.class)
    public void testSearchTradableassetsByCode_whenCodesNotParsed_thenException() throws JsonParseException,
            JsonMappingException, IOException {
        Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(TypeReference.class))).thenThrow(IOException.class);

        ipsTradableAssetsApiController.searchTradableAssetsByCode("ipsId", "", "SHARE");
    }
}
