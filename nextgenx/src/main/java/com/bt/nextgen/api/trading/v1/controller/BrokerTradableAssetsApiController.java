package com.bt.nextgen.api.trading.v1.controller;

import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.BrokerTradableAssetsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller("BrokerTradableAssetsApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(description = "Get the tradable asset information for a broker")
public class BrokerTradableAssetsApiController {

    @Autowired
    private BrokerTradableAssetsDtoService brokerAssetsDtoService;

    @Autowired
    private JsonObjectMapper mapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.brokertradableassets}")
    @ApiOperation(value = "Gets the tradable assets for a broker based on the search criteria", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse searchTradableAssets(
            @PathVariable("product-id") @ApiParam(value = "Product ID to get assets for", required = true) String productId,
            @RequestParam(value = "query", required = true) @ApiParam(value = "String to search on", required = true) String query,
            @RequestParam(value = "asset_type", required = false) @ApiParam(value = "AssetType to include, comma separated", required = false) String assetType,
            @RequestParam(value = "asset-ids", required = false) @ApiParam(value = "Asset IDs to include, comma separated", required = false) String assetIds) {
        return searchAssets(EncodedString.toPlainText(productId), query, assetType, assetIds);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.benchmark}")
    @ApiOperation(value = "Gets the tradable assets for a broker based on the search criteria", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse searchBenchmarks(
            @RequestParam(value = "query", required = true) @ApiParam(value = "String to search on", required = true) String query,
            @RequestParam(value = "asset-ids", required = false) @ApiParam(value = "Asset IDs to include, comma separated", required = false) String assetIds) {
        return searchAssets(null, query, AssetType.INDEX.name(), assetIds);
    }

    private @ResponseBody ApiResponse searchAssets(String productId, String query, String assetType, String assetIds) {

        final ApiSearchCriteria productIdCriteria = new ApiSearchCriteria("productId", SearchOperation.EQUALS, productId,
                OperationType.STRING);
        final ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, query,
                OperationType.STRING);
        final ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, assetType,
                OperationType.STRING);

        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        if (productId != null) {
            criteriaList.add(productIdCriteria);
        }
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);

        if (!StringUtils.isEmpty(assetIds)) {
            List<String> assetIdList;
            try {
                assetIdList = mapper.readValue(JsonSanitizer.sanitize(assetIds), new TypeReference<ArrayList<String>>() {
                });
            } catch (IOException e) {
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to parse asset-ids " + assetIds, e);
            }

            final ApiSearchCriteria assetIdsCriteria = new ApiSearchCriteria("assetIds", SearchOperation.LIST_CONTAINS,
                    StringUtils.join(assetIdList, ','), OperationType.STRING);
            criteriaList.add(assetIdsCriteria);
        }

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, brokerAssetsDtoService, criteriaList).performOperation();
    }
}
