package com.bt.nextgen.api.trading.v1.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.trading.v1.model.AvailableAssetInfoDto;
import com.bt.nextgen.api.trading.v1.model.TradableInvestmentOptionDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetCountDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.api.trading.v1.service.AvailableAssetInfoDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableAssetsCountDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableAssetsDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableAssetsTypeDtoService;
import com.bt.nextgen.api.trading.v1.service.TradableInvestmentOptionsDtoService;
import com.bt.nextgen.config.JsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.util.Environment;
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

@Controller("TradableAssetsApiControllerV2")
@RequestMapping(produces = "application/json")
@Api(description = "Get the tradable asset information for an account")
public class TradableAssetsApiController {

    @Autowired
    private TradableAssetsDtoService tradeableAssetsDtoService;

    @Autowired
    private TradableAssetsTypeDtoService tradeableAssetsTypeDtoService;

    @Autowired
    private TradableAssetsCountDtoService tradableAssetsCountDtoService;

    @Autowired
    private TradableInvestmentOptionsDtoService tradableInvestmentOptionsDtoService;

    @Autowired
    private AvailableAssetInfoDtoService availableAssetInfoDtoService;

    @Autowired
    private JsonObjectMapper mapper;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.tradableassets}")
    @ApiOperation(value = "Gets the tradable assets for an account based on the search criteria", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse searchTradableAssets(
            @PathVariable("account-id") @ApiParam(value = "Account ID to get assets for", required = true) String accountId,
            @RequestParam(value = "query", required = true) @ApiParam(value = "String to search on", required = true) String query,
            @RequestParam(value = "asset_type", required = false) @ApiParam(value = "AssetType to include, comma separated", required = false) String assetType,
            @RequestParam(value = "asset-ids", required = false) @ApiParam(value = "Asset IDs to include, comma separated", required = false) String assetIds) {
        return searchAssets(accountId, query, assetType, assetIds, true);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.assets}")
    @ApiOperation(value = "Gets the tradable assets for an account based on the search criteria", response = TradeAssetDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse searchAllAssets(
            @PathVariable("account-id") @ApiParam(value = "Account ID to get assets for", required = true) String accountId,
            @RequestParam(value = "query", required = true) @ApiParam(value = "String to search on", required = true) String query,
            @RequestParam(value = "asset_type", required = false) @ApiParam(value = "AssetType to include, comma separated", required = false) String assetType,
            @RequestParam(value = "asset-ids", required = false) @ApiParam(value = "Asset IDs to include, comma separated", required = false) String assetIds) {
        return searchAssets(accountId, query, assetType, assetIds, false);
    }

    private @ResponseBody ApiResponse searchAssets(String accountId, String query, String assetType, String assetIds,
            boolean filterAal) {

        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId,
                OperationType.STRING);
        final ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, query,
                OperationType.STRING);
        final ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, assetType,
                OperationType.STRING);

        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(accountIdCriteria);
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);
        criteriaList.add(new ApiSearchCriteria("filterAal", SearchOperation.EQUALS, Boolean.valueOf(filterAal).toString(),
                OperationType.BOOLEAN));

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

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, tradeableAssetsDtoService, criteriaList).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.tradableassettypes}")
    @ApiOperation(value = "Gets the tradable asset types for an account", response = TradeAssetTypeDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse searchAssetTypes(
            @PathVariable("account-id") @ApiParam(value = "Account ID to get assets for", required = true) String accountId) {

        final ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId,
                OperationType.STRING);

        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(accountIdCriteria);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, tradeableAssetsTypeDtoService, criteriaList).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.tradableassetcounts}")
    @ApiOperation(value = "Gets the number of tradable assets for an account based on the search criteria", response = TradeAssetCountDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse getTradableAssetCount(
            @PathVariable("account-id") @ApiParam(value = "Account ID to get assets for", required = true) String accountId,
            @RequestParam(value = "filters", required = false) @ApiParam(value = "Filter criteria", required = false) String filterCriteria) {

        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId, OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "", OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("filterAal", SearchOperation.EQUALS, "true", OperationType.BOOLEAN));

        if (filterCriteria != null) {
            criteriaList.addAll(ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, filterCriteria));
        }

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, tradableAssetsCountDtoService, criteriaList).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.tradableinvestmentoptions}")
    @ApiOperation(value = "Gets the tradable investment policy statements for an account based on the search criteria", response = TradableInvestmentOptionDto.class, responseContainer = "List")
    public @ResponseBody ApiResponse getTradableInvestmentOptions(
            @PathVariable("account-id") @ApiParam(value = "Account ID to get assets for", required = true) String accountId,
            @RequestParam(value = "asset_type", required = false) @ApiParam(value = "AssetType to include, comma separated", required = false) String assetType) {

        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("accountId", SearchOperation.EQUALS, accountId, OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("query", SearchOperation.EQUALS, "", OperationType.STRING));
        criteriaList.add(new ApiSearchCriteria("filterAal", SearchOperation.EQUALS, "true", OperationType.BOOLEAN));

        String atSearchString = AssetType.MANAGED_PORTFOLIO.getDisplayName();
        if (StringUtils.isNotEmpty(assetType)) {
            atSearchString = assetType;
        }
        criteriaList.add(new ApiSearchCriteria("assetType", SearchOperation.EQUALS, atSearchString, OperationType.STRING));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, tradableInvestmentOptionsDtoService, criteriaList)
                .performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = "${api.trading.v1.uri.availableassetinfo}")
    @ApiOperation(value = "Gets the aal info for an account including dealer group id, product id etc", response = AvailableAssetInfoDto.class)
    public @ResponseBody ApiResponse getAvailableAssetInfo(
            @PathVariable("account-id") @ApiParam(value = "Account ID to get assets for", required = true) String accountId) {
        if (Environment.notProduction() && StringUtils.isNotEmpty(accountId)) {
            return new FindByKey<>(ApiVersion.CURRENT_VERSION, availableAssetInfoDtoService, new AccountKey(accountId))
                    .performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
