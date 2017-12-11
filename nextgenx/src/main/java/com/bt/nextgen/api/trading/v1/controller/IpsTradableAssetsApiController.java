package com.bt.nextgen.api.trading.v1.controller;

import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.IpsTradableAssetsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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

@Controller("IpsTradableAssetsApiControllerV1")
@RequestMapping(produces = "application/json")
@Api(description = "Search for tradable assets in the AAL of a particular IPS")
public class IpsTradableAssetsApiController {

    @Autowired
    private IpsTradableAssetsDtoService ipsAssetsDtoService;

    @Autowired
    private JsonObjectMapper mapper;

    private static final String IPS_ID = "ips-id";
    private static final String QUERY = "query";
    private static final String ASSET_TYPE = "asset-type";
    private static final String ASSET_IDS = "asset-ids";
    private static final String ASSET_CODES = "asset-codes";
    private static final String DEFAULT_QUERY_STRING = "  ";

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.ipstradableassets}")
    @ApiOperation(value = "Search for any assets within IPS AAL matching the provided query", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse searchTradableAssets(
            @PathVariable(IPS_ID) @ApiParam(value = "ID of IPS to return relevant assets for", required = true) String ipsId,
            @RequestParam(value = QUERY, required = true) @ApiParam(value = "String to search on", required = true) String query,
            @RequestParam(value = ASSET_TYPE, required = true) @ApiParam(value = "AssetTypes to include, | separated", required = true) String assetTypes) {
        return searchAssets(ipsId, query, assetTypes);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.ipstradableassetids}")
    @ApiOperation(value = "Load specified assets of the provided type from IPS AAL", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse searchTradableAssetsById(
            @PathVariable(IPS_ID) @ApiParam(value = "ID of IPS to return relevant assets for", required = true) String ipsId,
            @RequestParam(value = ASSET_IDS, required = true) @ApiParam(value = "Asset ids to include, comma separated", required = true) String assetIds,
            @RequestParam(value = ASSET_TYPE, required = true) @ApiParam(value = "AssetTypes to include, | separated", required = true) String assetTypes) {
        return searchAssetsById(ipsId, assetIds, assetTypes);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.ipstradableassetcodes}")
    @ApiOperation(value = "Load specified assets of the provided type from IPS AAL", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody
    ApiResponse searchTradableAssetsByCode(
            @PathVariable(IPS_ID) @ApiParam(value = "ID of IPS to return relevant assets for", required = true) String ipsId,
            @RequestParam(value = ASSET_CODES, required = true) @ApiParam(value = "Asset codes to include, comma separated", required = true) String assetCodes,
            @RequestParam(value = ASSET_TYPE, required = true) @ApiParam(value = "AssetTypes to include, | separated", required = true) String assetTypes) {
        return searchAssetsByCode(ipsId, assetCodes, assetTypes);
    }

    private ApiResponse searchAssets(String ipsId, String query, String assetTypes) {
        List<ApiSearchCriteria> criteriaList = getStandardCriteriaList(ipsId, query, assetTypes);
        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, ipsAssetsDtoService, criteriaList).performOperation();
    }

    private ApiResponse searchAssetsById(String ipsId, String assetIds, String assetTypes) {
        List<ApiSearchCriteria> criteriaList = getStandardCriteriaList(ipsId, DEFAULT_QUERY_STRING, assetTypes);

        if (StringUtils.isBlank(assetIds)) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Blank asset-ids in request.");
        }
        List<String> assetIdList = new ArrayList<>();
        try {
            JsonNode nodes = mapper.readTree(JsonSanitizer.sanitize(assetIds));
            for (JsonNode node : nodes) {
                String assetId = node.asText();
                // AssetId has to be a number
                if (NumberUtils.isDigits(assetId)) {
                    assetIdList.add(assetId.trim());
                }
            }
        } catch (IOException e) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to parse asset-ids " + assetIds, e);
        }

        final ApiSearchCriteria assetIdsCriteria = new ApiSearchCriteria("assetIds", SearchOperation.LIST_CONTAINS,
                StringUtils.join(assetIdList, ','), OperationType.STRING);
        criteriaList.add(assetIdsCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, ipsAssetsDtoService, criteriaList).performOperation();
    }

    private ApiResponse searchAssetsByCode(String ipsId, String assetCodes, String assetTypes) {
        List<ApiSearchCriteria> criteriaList = getStandardCriteriaList(ipsId, DEFAULT_QUERY_STRING, assetTypes);

        if (StringUtils.isBlank(assetCodes)) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Blank asset codes in request.");
        }

        List<String> assetCodeList = new ArrayList<>();
        try {
            JsonNode assetCodeNodes = mapper.readTree(JsonSanitizer.sanitize(assetCodes));
            for (JsonNode node : assetCodeNodes) {
                String assetNode = node.asText();
                // Only allow assetCodes that are alphanumeric
                if (StringUtils.isNotBlank(assetNode) && StringUtils.isAlphanumeric(assetNode)) {
                    assetCodeList.add(assetNode.trim());
                }
            }
        } catch (IOException e) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to parse asset-codes " + assetCodes, e);
        }

        final ApiSearchCriteria assetCodesCriteria = new ApiSearchCriteria("assetCodes", SearchOperation.LIST_CONTAINS,
                StringUtils.join(assetCodeList, ','), OperationType.STRING);
        criteriaList.add(assetCodesCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, ipsAssetsDtoService, criteriaList).performOperation();
    }

    private List<ApiSearchCriteria> getStandardCriteriaList(String ipsId, String query, String assetTypes) {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        final ApiSearchCriteria ipsIdCriteria = new ApiSearchCriteria("ipsId", SearchOperation.EQUALS, ipsId,
                OperationType.STRING);
        criteriaList.add(ipsIdCriteria);

        final ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, query,
                OperationType.STRING);
        criteriaList.add(queryCriteria);

        final ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, assetTypes,
                OperationType.STRING);
        criteriaList.add(assetTypeCriteria);

        return criteriaList;
    }
}
