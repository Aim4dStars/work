package com.bt.nextgen.api.order.controller;

import com.bt.nextgen.api.order.model.GeneralOrderDto;
import com.bt.nextgen.api.order.model.OrderDto;
import com.bt.nextgen.api.order.model.OrderKey;
import com.bt.nextgen.api.order.service.OrderDtoService;
import com.bt.nextgen.api.order.service.OrderDtoServiceV2;
import com.bt.nextgen.api.order.service.OrderInProgressDtoService;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.PageFilter;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.SearchByKey;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Controller("OrderApiControllerV0.1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class OrderApiController {
    @Autowired
    private OrderDtoService orderDtoService;

    @Autowired
    private OrderGroupDtoErrorMapper orderDtoErrorMapper;

    @Autowired
    private OrderInProgressDtoService orderInProgressDtoService;

    @Autowired
    private PermissionAccountDtoService permissionAccountDtoService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private OrderDtoServiceV2 orderDtoServiceV2;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private ObjectMapper mapper;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_intermediary_reports') ")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ADVISER_ORDERS)
    public @ResponseBody ApiResponse getOrders(@RequestParam(value = "search-criteria", required = false) String searchCriteria,
            @RequestParam(value = "paging", required = false) String paging,
            @PathVariable(UriMappingConstants.ADVISER_ID_URI_MAPPING) String adviserId){
        final boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ADVISER_ID, SearchOperation.EQUALS, adviserId, OperationType.STRING));

        if (StringUtils.isNotEmpty(searchCriteria)) {
            criteria.addAll(ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, searchCriteria));
        }
        if(termDepositToggle){
            return new PageFilter<>(ApiVersion.CURRENT_VERSION,
                    new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderDtoServiceV2, criteria), paging).performOperation();
        }
        return new PageFilter<>(ApiVersion.CURRENT_VERSION,
                new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderDtoService, criteria), paging).performOperation();
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.order.view')")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_ORDERS)
    public @ResponseBody ApiResponse getAccountOrders(
            @RequestParam(value = "search-criteria", required = false) String searchCriteria,
            @RequestParam(value = "paging", required = false) String paging,
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        final boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");

        List<ApiSearchCriteria> criteria = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, searchCriteria);
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));
        if(termDepositToggle){

            return new PageFilter<>(ApiVersion.CURRENT_VERSION,
                    new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderDtoServiceV2, criteria), paging).performOperation();
        }else{

            return new PageFilter<>(ApiVersion.CURRENT_VERSION,
                new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderDtoService, criteria), paging).performOperation();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ORDER)
    public @ResponseBody ApiResponse getOrder(@PathVariable(UriMappingConstants.ORDER_ID_URI_MAPPING) String orderId,
            @RequestParam(value = "include-external", required = false) Boolean includeExternal) throws IOException {
        final boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");

        OrderKey key = new OrderKey(orderId);
        if (includeExternal) {
            if(termDepositToggle){
                return new SearchByKey<>(ApiVersion.CURRENT_VERSION, orderDtoServiceV2, key).performOperation();
            }
            return new SearchByKey<>(ApiVersion.CURRENT_VERSION, orderDtoService, key).performOperation();
        } else {
            if(termDepositToggle){
                return new FindByKey<>(ApiVersion.CURRENT_VERSION, orderDtoServiceV2, key).performOperation();
            }
            return new FindByKey<>(ApiVersion.CURRENT_VERSION, orderDtoService, key).performOperation();
        }
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Submit_trade_to_executed')")
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ORDERS)
    public @ResponseBody KeyedApiResponse<OrderKey> amendOrder(@RequestParam(value = "order", required = true) String order)
            throws IOException {

        if (!profileService.isEmulating()) {
            String sanitizedOrder = JsonSanitizer.sanitize(order);
            OrderDto orderDto = mapper.readerWithView(JsonViews.Write.class).forType(GeneralOrderDto.class)
                    .readValue(sanitizedOrder);
            return new Update<>(ApiVersion.CURRENT_VERSION, orderDtoService, orderDtoErrorMapper, orderDto).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ACCOUNT_ORDERS + "/update")
    public @ResponseBody KeyedApiResponse<OrderKey> updateAccountOrder(
            @RequestParam(value = "order", required = true) String order,
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) throws IOException {
        final PermissionsDto permission = permissionAccountDtoService.find(new PermissionAccountKey(accountId), null);
        if (permission.hasPermission("account.trade.submit")) {
            return amendOrder(order);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_ORDERS_IN_PROGRESS)
    @Deprecated
    /**
     * @deprecated use getAccountOrders
     */
    public @ResponseBody ApiResponse getOrdersInProgress(/*
                                                          * @RequestParam(value = "search-criteria", required = false) String
                                                          * searchCriteria,
                                                          */
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        List<ApiSearchCriteria> criteria = ApiSearchCriteria.parseQueryString(ApiVersion.CURRENT_VERSION, null);
        criteria.add(new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId, OperationType.STRING));

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderInProgressDtoService, criteria).performOperation();

    }
}
