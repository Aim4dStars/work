package com.bt.nextgen.api.asset.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.AssetPriceDto;
import com.bt.nextgen.api.asset.model.AssetPriceDtoKey;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoService;
import com.bt.nextgen.api.asset.service.AssetHoldersDtoService;
import com.bt.nextgen.api.asset.service.AssetPriceDtoService;
import com.bt.nextgen.api.asset.service.AvailableAssetDtoService;
import com.bt.nextgen.api.asset.service.AvailableShareAssetDtoService;
import com.bt.nextgen.api.asset.service.SimplePortfolioAssetDtoService;
import com.bt.nextgen.api.asset.util.AssetConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.selectFirst;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetApiControllerTest {
    @InjectMocks
    private AssetApiController assetApiController;

    @Mock
    private AssetDtoService assetDtoService;

    @Mock
    private AvailableShareAssetDtoService availableShareAssetDtoService;

    @Mock
    private AvailableAssetDtoService availableAssetDtoService;

    @Mock
    private AssetHoldersDtoService assetHoldersDtoService;

    @Mock
    private AssetPriceDtoService assetPriceDtoService;

    @Mock
    private SimplePortfolioAssetDtoService simplePortfolioAssetDtoService;

    @Mock
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> listArgumentCaptor;

    @Before
    public void setup() {
        AssetDto assetDto = mock(AssetDto.class);
        when(assetDto.getAssetId()).thenReturn("1");
        List<AssetDto> assetDtoList = Arrays.asList(assetDto);

        when(availableAssetDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(assetDtoList);
        when(availableAssetDtoService.getFilteredValue(anyString(), anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(assetDtoList);
        when(assetDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(assetDtoList);
        when(availableShareAssetDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class))).thenReturn(assetDtoList);

        AssetPriceDto assetPriceDto = mock(AssetPriceDto.class);
        when(assetPriceDto.getLastPrice()).thenReturn(Double.valueOf(100.0));

        when(assetPriceDtoService.find(any(AssetPriceDtoKey.class), any(ServiceErrors.class))).thenReturn(assetPriceDto);
        when(assetPriceDtoService.search(anyListOf(ApiSearchCriteria.class), any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(assetPriceDto));
    }

    @Test
    public void testGetAvailableAssetsForAccount() throws Exception {
        assetApiController.getAvailableAssetsForAccount("1");

        verify(availableAssetDtoService).search(listArgumentCaptor.capture(), any(ServiceErrors.class));

        ApiSearchCriteria criteria = getApiSearchCriteria(Attribute.ACCOUNT_ID, listArgumentCaptor.getValue());

        assertNotNull(criteria);
        assertEquals("1", criteria.getValue());
    }

    @Test
    public void testGetAvailableAssets() throws Exception {
        assetApiController.getAvailableAssets(null, null, null, null, null);

        verify(availableAssetDtoService).search(listArgumentCaptor.capture(), any(ServiceErrors.class));
        List<ApiSearchCriteria> apiSearchCriteriaList = listArgumentCaptor.getValue();
        assertTrue(apiSearchCriteriaList.isEmpty());

        assetApiController.getAvailableAssets("query", "assetType", "assetStatus", "true", "productId");

        verify(availableAssetDtoService).getFilteredValue(anyString(), listArgumentCaptor.capture(), any(ServiceErrors.class));

        ApiSearchCriteria criteria = getApiSearchCriteria("product-id", listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("productId", criteria.getValue());

        criteria = getApiSearchCriteria(Attribute.ASSET_TYPE, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("assetType", criteria.getValue());

        criteria = getApiSearchCriteria(Attribute.ASSET_STATUS, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("assetStatus", criteria.getValue());

        criteria = getApiSearchCriteria(AssetConstants.FILTER_AAL, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("true", criteria.getValue());

        assetApiController.getAvailableAssets("query", null, null, "true", null);

        verify(availableAssetDtoService, atLeast(1)).getFilteredValue(anyString(), listArgumentCaptor.capture(), any(ServiceErrors.class));

        criteria = getApiSearchCriteria(AssetConstants.FILTER_AAL, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("true", criteria.getValue());

        assertNull(getApiSearchCriteria("product-id", listArgumentCaptor.getValue()));
        assertNull(getApiSearchCriteria(Attribute.ASSET_TYPE, listArgumentCaptor.getValue()));
        assertNull(getApiSearchCriteria(Attribute.ASSET_STATUS, listArgumentCaptor.getValue()));

        assetApiController.getAvailableAssets(null, null, "assetStatus", null, "productId");

        verify(availableAssetDtoService, atLeast(1)).getFilteredValue(anyString(), listArgumentCaptor.capture(), any(ServiceErrors.class));

        assertNotNull(getApiSearchCriteria("product-id", listArgumentCaptor.getValue()));
        assertNull(getApiSearchCriteria(Attribute.ASSET_TYPE, listArgumentCaptor.getValue()).getValue());
        assertNotNull(getApiSearchCriteria(Attribute.ASSET_STATUS, listArgumentCaptor.getValue()).getValue());
        assertNull(getApiSearchCriteria(AssetConstants.FILTER_AAL, listArgumentCaptor.getValue()).getValue());
    }

    @Test
    public void testAssetPrice() {
        ApiResponse response = assetApiController.getAssetPrice("1", "true", "true", "true");

        AssetPriceDto assetPriceDto = (AssetPriceDto) response.getData();
        assertEquals(Double.valueOf(100.0), assetPriceDto.getLastPrice());
    }

    @Test
    public void testAssetsPrice() throws IOException {
        List<String> assetCodeList = Arrays.asList("1");

        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(assetCodeList);

        assetApiController.getAssetsPrice("[\"1\", \"2\"]", "true", "true", "true");

        verify(assetPriceDtoService).search(listArgumentCaptor.capture(), any(ServiceErrors.class));

        ApiSearchCriteria criteria = getApiSearchCriteria(Attribute.LIVE_ASSET_PRICE, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("true", criteria.getValue());

        criteria = getApiSearchCriteria(Attribute.COMPREHENSIVE_ASSET_PRICE, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("true", criteria.getValue());

        criteria = getApiSearchCriteria(Attribute.FALLBACK, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("true", criteria.getValue());

        criteria = getApiSearchCriteria(Attribute.ASSETTYPEINTLID, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("1", criteria.getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssetsPrice_whenInvalidJson_thenThrowIllegalArgumentException() throws IOException {
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new IOException());

        assetApiController.getAssetsPrice("{\"dummy\": false}", "true", "true", "true");
    }

    @Test
    public void testGetAssets() throws IOException {
        List<String> assetCodeList = Arrays.asList("1");

        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenReturn(assetCodeList);

        assetApiController.getAssets("{\"dummy\": false}");

        verify(assetDtoService).search(listArgumentCaptor.capture(), any(ServiceErrors.class));

        ApiSearchCriteria criteria = getApiSearchCriteria("assetCodes", listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.LIST_CONTAINS, criteria.getOperation());
        assertEquals("1", criteria.getValue());

        assetApiController.getAssets("");

        verify(assetDtoService, atLeast(1)).search(listArgumentCaptor.capture(), any(ServiceErrors.class));

        criteria = getApiSearchCriteria("assetCodes", listArgumentCaptor.getValue());
        assertNull(criteria);
    }

    @Test(expected = BadRequestException.class)
    public void testGetAssets_whenThereAreAssetCodesButInvalid_thenThrowBadRequestException() throws IOException {
        when(objectMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(new IOException());

        assetApiController.getAssets("{\"dummy\": false}");
    }

    @Test
    public void testSearchAssets() {
        assetApiController.searchAssets("query", "assetType", "assetName");

        verify(availableShareAssetDtoService).search(listArgumentCaptor.capture(), any(ServiceErrors.class));

        ApiSearchCriteria criteria = getApiSearchCriteria("query", listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.CONTAINS, criteria.getOperation());
        assertEquals("query", criteria.getValue());

        criteria = getApiSearchCriteria(AssetConstants.ASSET_TYPE, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.CONTAINS, criteria.getOperation());
        assertEquals("assetType", criteria.getValue());
    }

    @Test
    public void testGetSimplePortfolioAssets() {
        when(simplePortfolioAssetDtoService.findAll(any(ServiceErrors.class)))
                .thenReturn(Arrays.asList(mock(ManagedPortfolioAssetDto.class)));

        assetApiController.getSimplePortfolioAssets();

        verify(simplePortfolioAssetDtoService, times(1)).findAll(any(ServiceErrors.class));
    }

    @Test
    public void testGetAssetHolders() {
        DateTime priceDate = new DateTime();

        String filter = "[{\"prop\": \"1\", \"op\": \"=\", \"val\": \"0\", \"type\": \"number\"}]";
        String paging = "{\"startIndex\": \"0\", \"maxResults\": \"100\"}";
        ApiResponse response = assetApiController.getAssetHolders("1", priceDate.toString(), filter, "sortBy", paging);

        verify(assetHoldersDtoService, times(1)).search(listArgumentCaptor.capture(), any(ServiceErrors.class));

        assertNotNull(response.getPaging());

        ApiSearchCriteria criteria = getApiSearchCriteria(AssetConstants.ASSET_IDS, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals("1", criteria.getValue());

        criteria = getApiSearchCriteria(AssetConstants.PRICE_DATE, listArgumentCaptor.getValue());
        assertNotNull(criteria);
        assertEquals(ApiSearchCriteria.SearchOperation.EQUALS, criteria.getOperation());
        assertEquals(priceDate.toString(), criteria.getValue());

        response = assetApiController.getAssetHolders("1", priceDate.toString(), filter, "sortBy", null);

        assertNull(response.getPaging());

        response = assetApiController.getAssetHolders("1", priceDate.toString(), filter, null, paging);

        assertNotNull(response.getPaging());

        response = assetApiController.getAssetHolders("1", priceDate.toString(), filter, null, null);

        assertNull(response.getPaging());

        response = assetApiController.getAssetHolders("1", null, filter, null, null);

        assertNull(response.getPaging());
    }

    private ApiSearchCriteria getApiSearchCriteria(String key, List<ApiSearchCriteria> apiSearchCriteriaList) {
        return selectFirst(apiSearchCriteriaList, having(on(ApiSearchCriteria.class).getProperty(), equalTo(key)));
    }
}
