package com.bt.nextgen.api.asset.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.AssetPriceDtoKey;
import com.bt.nextgen.api.asset.service.AssetDtoService;
import com.bt.nextgen.api.asset.service.AssetHoldersDtoService;
import com.bt.nextgen.api.asset.service.AssetPriceDtoService;
import com.bt.nextgen.api.asset.service.AvailableAssetDtoService;
import com.bt.nextgen.api.asset.service.AvailableShareAssetDtoService;
import com.bt.nextgen.api.asset.service.SimplePortfolioAssetDtoService;
import com.bt.nextgen.api.asset.util.AssetConstants;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.ServiceFilter;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static com.bt.nextgen.api.asset.util.AssetConstants.ASSET_IDS;
import static com.bt.nextgen.api.asset.util.AssetConstants.ASSET_STATUS_KEY;
import static com.bt.nextgen.api.asset.util.AssetConstants.ASSET_TYPE;
import static com.bt.nextgen.api.asset.util.AssetConstants.ASSET_TYPE_KEY;
import static com.bt.nextgen.api.asset.util.AssetConstants.FILTER_AAL;
import static com.bt.nextgen.api.asset.util.AssetConstants.PRICE_DATE;
import static com.bt.nextgen.core.api.ApiVersion.CURRENT_VERSION;
import static com.bt.nextgen.core.api.operation.BeanFilter.QUERY_PARAMETER;
import static com.bt.nextgen.core.api.operation.PageFilter.PAGING_PARAMETER;
import static com.bt.nextgen.core.api.operation.SearchByCriteria.SEARCH_CRITERIA_PARAMETER;
import static com.bt.nextgen.core.api.operation.Sort.SORT_PARAMETER;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Controller
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class AssetApiController {
    private static final Logger logger = LoggerFactory.getLogger(AssetApiController.class);

    @Autowired
    private AssetDtoService assetDtoService;

    @Autowired
    private AvailableAssetDtoService availableAssetDtoService;

    @Autowired
    private AvailableShareAssetDtoService availableShareAssetDtoService;

    @Autowired
    private AssetPriceDtoService assetPriceDtoService;

    @Autowired
    private SimplePortfolioAssetDtoService simplePortfolioAssetDtoService;

    @Autowired
    private AssetHoldersDtoService assetHoldersDtoService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private ObjectMapper objectMapper;

    public static final String PRODUCT_ID = "product-id";

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_AVAILABLE_ASSETS)
    public @ResponseBody ApiResponse getAvailableAssetsForAccount(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) throws Exception {
        AccountKey.valueOf(accountId);
        List<ApiSearchCriteria> criteria = Collections.singletonList(
                new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        return new SearchByCriteria<AssetDto>(CURRENT_VERSION, availableAssetDtoService, criteria).performOperation();
    }

    /**
     * Get all available assets for the dealer. If asset type key is provided, then only matching asset types are returned.
     *
     * @param assetType
     *            the asset type internal id
     * @return a list of sorted assets by name in ascending order. If a asset type filter is provided, then only matching assets
     *         are returned.
     * @throws Exception
     */
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.AVAILABLE_ASSETS)
    public @ResponseBody ApiResponse getAvailableAssets(
            @RequestParam(value = QUERY_PARAMETER, required = false) final String query,
            @RequestParam(value = ASSET_TYPE_KEY, required = false) final String assetType,
            @RequestParam(value = ASSET_STATUS_KEY, required = false) final String assetStatus,
            @RequestParam(value = FILTER_AAL, required = false, defaultValue = "true") final String filterAal,
            @RequestParam(value = PRODUCT_ID, required = false) final String productId) throws Exception {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        if (productId != null) {
            criteria.add(new ApiSearchCriteria(PRODUCT_ID, SearchOperation.EQUALS, productId, OperationType.STRING));
        }
        if (assetType != null || assetStatus != null) {
            criteria.add(new ApiSearchCriteria(Attribute.ASSET_TYPE, SearchOperation.EQUALS, assetType, OperationType.STRING));
            criteria.add(
                    new ApiSearchCriteria(Attribute.ASSET_STATUS, SearchOperation.EQUALS, assetStatus, OperationType.STRING));
        }

        if (query != null || isNotEmpty(criteria)) {
            // Filtered edition
            criteria.add(new ApiSearchCriteria(FILTER_AAL, SearchOperation.EQUALS, filterAal, OperationType.BOOLEAN));
            return new ServiceFilter<>(CURRENT_VERSION, availableAssetDtoService, query, criteria).performOperation();
        } else {
            return new SearchByCriteria<>(CURRENT_VERSION, availableAssetDtoService, criteria).performOperation();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ASSET_PRICE)
    public @ResponseBody ApiResponse getAssetPrice(@PathVariable(UriMappingConstants.ASSET_ID_URI_MAPPING) String assetId,
            @RequestParam(value = AssetConstants.LIVE_ASSET_PRICE, required = false) String live,
            @RequestParam(value = AssetConstants.COMPREHENSIVE_ASSET_PRICE, required = false) String comprehensive,
            @RequestParam(value = AssetConstants.FALLBACK, required = false) String fallback) {
        return new FindByKey<>(CURRENT_VERSION, assetPriceDtoService, new AssetPriceDtoKey(assetId,
                Boolean.parseBoolean(live), Boolean.parseBoolean(comprehensive), Boolean.parseBoolean(fallback)))
                        .performOperation();
    }


    // Not currently used by any screen
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ASSETS_PRICE)
    public @ResponseBody
    ApiResponse getAssetsPrice(@RequestParam(value = "assetIds", required = true) String assetIds,
                               @RequestParam(value = AssetConstants.LIVE_ASSET_PRICE, required = false) String live,
                               @RequestParam(value = AssetConstants.COMPREHENSIVE_ASSET_PRICE, required = false) String comprehensive,
                               @RequestParam(value = AssetConstants.FALLBACK, required = false) String fallback) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();

        criteria.add(new ApiSearchCriteria(Attribute.LIVE_ASSET_PRICE, SearchOperation.EQUALS, live, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.COMPREHENSIVE_ASSET_PRICE, SearchOperation.EQUALS, comprehensive,
                OperationType.STRING));
        criteria.add(new ApiSearchCriteria(Attribute.FALLBACK, SearchOperation.EQUALS, fallback, OperationType.STRING));

        List<String> assetIdList;

        try {
            String sanitizedAssetIds = JsonSanitizer.sanitize(assetIds);
            assetIdList = objectMapper.readValue(sanitizedAssetIds, new TypeReference<ArrayList<String>>() {
            });

            for (String assetId : assetIdList) {
                criteria.add(new ApiSearchCriteria(Attribute.ASSETTYPEINTLID, SearchOperation.EQUALS, assetId, OperationType.STRING));
            }
        } catch (IOException e) {
            logger.error("IOException", e);
            throw new IllegalArgumentException("Unable to map asset IDs: ", e);
        }

        return new SearchByCriteria<>(CURRENT_VERSION, assetPriceDtoService, criteria).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ASSETS)
    public @ResponseBody ApiResponse getAssets(@RequestParam(value = "asset-codes", required = false) String assetCodes) {
        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();

        if (!StringUtils.isEmpty(assetCodes)) {
            List<String> assetCodeList;

            try {
                String sanitizedAssetCodes = JsonSanitizer.sanitize(assetCodes);
                assetCodeList = objectMapper.readValue(sanitizedAssetCodes, new TypeReference<ArrayList<String>>() {
                });
            } catch (IOException e) {
                throw new BadRequestException(CURRENT_VERSION, "Unable to parse asset-codes " + assetCodes, e);
            }

            final ApiSearchCriteria assetCodesCriteria = new ApiSearchCriteria("assetCodes", SearchOperation.LIST_CONTAINS,
                    StringUtils.join(assetCodeList, ','), OperationType.STRING);
            criteriaList.add(assetCodesCriteria);
        }

        return new SearchByCriteria<>(CURRENT_VERSION, assetDtoService, criteriaList).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.AVAILABLE_SHARES)
    public
    @ResponseBody
    ApiResponse searchAssets(@RequestParam(value = QUERY_PARAMETER, required = false) String query,
                             @RequestParam(value = ASSET_TYPE, required = false) String assetType,
                             @RequestParam(value = SORT_PARAMETER, required = false, defaultValue = "assetName") String sortBy) {

        final ApiSearchCriteria queryCriteria = new ApiSearchCriteria(QUERY_PARAMETER, SearchOperation.CONTAINS, query,
                OperationType.STRING);
        final ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria(ASSET_TYPE, SearchOperation.CONTAINS, assetType,
                OperationType.STRING);

        final List<ApiSearchCriteria> criteriaList = new ArrayList<>();
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);

        return new Sort<>(new SearchByCriteria<>(CURRENT_VERSION, availableShareAssetDtoService, criteriaList), sortBy).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/simple-assets")
    @PreAuthorize("isAuthenticated()")
    public @ResponseBody ApiResponse getSimplePortfolioAssets() {
        return new FindAll<>(CURRENT_VERSION, simplePortfolioAssetDtoService).performOperation();
    }

    /**
     * @param assetIds  - Comma separated list of asset ids to fetch account holders/holdings for
     * @param priceDate - Date for which we need holding information
     * @param filter    - filter parameters
     * @param sortBy    - Sorting parameters
     * @param paging    - paging parameters
     * @return - List of All the asset holders
     */
    @RequestMapping(method = RequestMethod.GET, value = "/asset-holders")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ApiResponse getAssetHolders(@RequestParam(value = ASSET_IDS) String assetIds,
                                       @RequestParam(required = false, value = PRICE_DATE) String priceDate,
                                       @RequestParam(required = false, value = SEARCH_CRITERIA_PARAMETER) String filter,
                                       @RequestParam(required = false, value = SORT_PARAMETER) String sortBy,
                                       @RequestParam(required = false, value = PAGING_PARAMETER) String paging) {

        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(ASSET_IDS, SearchOperation.EQUALS, assetIds, OperationType.STRING));
        criteria.add(new ApiSearchCriteria(PRICE_DATE, SearchOperation.EQUALS, priceDate != null ? priceDate :
                DateTime.now().toString(), OperationType.DATE));
        criteria.addAll(ApiSearchCriteria.parseQueryString(CURRENT_VERSION, filter));

        if (sortBy != null && paging != null) {
            return new PageFilter(CURRENT_VERSION, new Sort<>(new SearchByCriteria<>(CURRENT_VERSION, assetHoldersDtoService, criteria),
                    sortBy), paging).performOperation();
        }
        if (sortBy != null) {
            return new Sort<>(new SearchByCriteria<>(CURRENT_VERSION, assetHoldersDtoService, criteria), sortBy).performOperation();
        }
        if (paging != null) {
            return new PageFilter<>(CURRENT_VERSION, new SearchByCriteria<>(CURRENT_VERSION, assetHoldersDtoService, criteria), paging).performOperation();
        }

        return new SearchByCriteria<>(CURRENT_VERSION, assetHoldersDtoService, criteria).performOperation();
    }
}
