package com.bt.nextgen.api.order.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.service.OrderGroupDtoService;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.Delete;
import com.bt.nextgen.core.api.operation.FindAll;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Sort;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.core.security.api.model.PermissionAccountKey;
import com.bt.nextgen.core.security.api.model.PermissionsDto;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
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

import java.io.IOException;
import java.util.Collections;

/**
 * OrderGroupApiController is the api used to buy and sell investments
 * 
 */
@Controller("OrderGroupApiControllerV0.1")
@RequestMapping(value = UriMappingConstants.CURRENT_VERSION_API, produces = "application/json")
public class OrderGroupApiController {
    private static final Logger logger = LoggerFactory.getLogger(OrderGroupApiController.class);

    @Autowired
    private OrderGroupDtoService orderGroupDtoService;

    @Autowired
    private OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;

    @Autowired
    private PermissionAccountDtoService permissionAccountDtoService;

    @Autowired
    private UserProfileService profileService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private ObjectMapper mapper;

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.trade.entry')")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ACCOUNT_ORDER_GROUPS)
    public @ResponseBody ApiResponse search(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId) {
        ApiSearchCriteria criteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId,
                OperationType.STRING);

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, Collections.singletonList(criteria))
                .performOperation();
    }

    @PreAuthorize("isAuthenticated() and (hasPermission(null, 'Trade_entry') OR @acctPermissionService.canTransact(#accountId, 'account.trade.entry'))")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.USER_ORDER_GROUPS)
    public @ResponseBody ApiResponse searchOrders(@RequestParam(value = Sort.SORT_PARAMETER, required = false) String orderBy)
            throws IOException {
        return new Sort<>(new FindAll<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService), orderBy).performOperation();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ORDER_GROUPS)
    public @ResponseBody KeyedApiResponse<OrderGroupKey> create(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @RequestParam(value = "orderGroup", required = true) String orderGroup,
            @RequestParam(value = "x-ro-validate-only", required = false) String validateOnly) throws IOException {

        if (!profileService.isEmulating()) {
            String sanitizedOrderGroup = JsonSanitizer.sanitize(orderGroup);
            OrderGroupDto orderGroupDto = mapper.readerWithView(JsonViews.Write.class).forType(OrderGroupDto.class)
                    .readValue(sanitizedOrderGroup);
            orderGroupDto.setAccountKey(new AccountKey(accountId));

            if ("true".equals(validateOnly) && checkPermission(accountId, validateOnly, orderGroupDto)) {
                logger.info("Validate order request for portfolio {} ", accountId);
                return new Validate<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, orderGroupDtoErrorMapper, orderGroupDto)
                        .performOperation();
            } else if ("submit".equals(orderGroupDto.getStatus()) && checkPermission(accountId, validateOnly, orderGroupDto)) {
                logger.info("Submit order request for portfolio {} ", accountId);
                return new Submit<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, orderGroupDtoErrorMapper, orderGroupDto)
                        .performOperation();
            } else if (checkPermission(accountId)) {
                logger.info("Create order request for portfolio {} ", accountId);
                return new Create<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, orderGroupDto).performOperation();
            } else {
                throw new AccessDeniedException("Access Denied");
            }
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.trade.entry')")
    @RequestMapping(method = RequestMethod.GET, value = UriMappingConstants.ORDER_GROUP)
    public @ResponseBody KeyedApiResponse<OrderGroupKey> load(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @PathVariable(UriMappingConstants.ORDER_ID_URI_MAPPING) String orderId) {
        if (!profileService.isEmulating()) {
            OrderGroupKey key = new OrderGroupKey(accountId, orderId);
            return new FindByKey<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, key).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.trade.submit') and "
            + "@acctPermissionService.canTransact(#accountId, 'account.trade.entry')")
    // Should be a DELETE request, but webseal is blocking this http method.
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ORDER_GROUP + "/delete")
    public @ResponseBody ApiResponse delete(@PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @PathVariable(UriMappingConstants.ORDER_ID_URI_MAPPING) String orderId) {
        if (!profileService.isEmulating()) {
            OrderGroupKey key = new OrderGroupKey(accountId, orderId);
            return new Delete<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, key).performOperation();
        } else {
            throw new AccessDeniedException("Access Denied");
        }

    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST, value = UriMappingConstants.ORDER_GROUP)
    public @ResponseBody KeyedApiResponse<OrderGroupKey> save(
            @PathVariable(UriMappingConstants.ACCOUNT_ID_URI_MAPPING) String accountId,
            @PathVariable(UriMappingConstants.ORDER_ID_URI_MAPPING) String orderId,
            @RequestParam(value = "orderGroup", required = true) String orderGroup,
            @RequestParam(value = "x-ro-validate-only", required = false) String validateOnly) throws IOException {

        if (!profileService.isEmulating()) {
            String sanitizedOrderGroup = JsonSanitizer.sanitize(orderGroup);
            OrderGroupDto orderGroupDto = mapper.readerWithView(JsonViews.Write.class).forType(OrderGroupDto.class)
                    .readValue(sanitizedOrderGroup);
            orderGroupDto.setAccountKey(new AccountKey(accountId));

            if ("true".equals(validateOnly) && checkPermission(accountId, validateOnly, orderGroupDto)) {
                logger.info("Validate order request for order {} ", orderId);
                return new Validate<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, orderGroupDtoErrorMapper, orderGroupDto)
                        .performOperation();
            } else if ("submit".equals(orderGroupDto.getStatus()) && checkPermission(accountId, validateOnly, orderGroupDto)) {
                logger.info("Submit order request for order {} ", orderId);
                return new Submit<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, orderGroupDtoErrorMapper, orderGroupDto)
                        .performOperation();
            } else if (checkPermission(accountId)) {
                logger.info("Save order request for order {} ", orderId);
                return new Update<>(ApiVersion.CURRENT_VERSION, orderGroupDtoService, orderGroupDtoErrorMapper, orderGroupDto)
                        .performOperation();
            } else {
                throw new AccessDeniedException("Access Denied");
            }
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    private boolean checkPermission(String accountId, String validateOnly, OrderGroupDto orderGroupDto) {
        PermissionsDto permission = permissionAccountDtoService.find(new PermissionAccountKey(accountId), null);
        final boolean hasSubmitPermission = permission.hasPermission("account.trade.submit");
        final boolean hasCreatePermission = permission.hasPermission("account.trade.entry")
                && permission.hasPermission("account.trade.create");

        if (!"true".equals(validateOnly) && "submit".equals(orderGroupDto.getStatus()) && hasSubmitPermission) {
            return true;
        } else if ("true".equals(validateOnly) && hasCreatePermission) {
            return true;
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    /**
     * @param accountId
     * @return
     * @throws AccessDeniedException
     * 
     *             This method checks access for proper permissions while saving or creating a order . The permission being
     *             verified is account.trade.create
     * 
     */
    private boolean checkPermission(String accountId) throws AccessDeniedException {
        PermissionsDto permission = permissionAccountDtoService.find(new PermissionAccountKey(accountId), null);
        if (permission.hasPermission("account.trade.create")) {
            return true;
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
