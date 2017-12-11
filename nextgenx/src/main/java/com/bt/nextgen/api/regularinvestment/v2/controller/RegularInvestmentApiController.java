package com.bt.nextgen.api.regularinvestment.v2.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.validation.OrderGroupDtoErrorMapper;
import com.bt.nextgen.api.regularinvestment.v2.model.InvestmentPeriodDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RIPAction;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentDtoService;
import com.bt.nextgen.api.regularinvestment.v2.service.RegularInvestmentTransactionDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.api.operation.Create;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByCriteria;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.core.api.operation.Validate;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller("RegularInvestmentApiControllerV2")
@RequestMapping(produces = "application/json")
@Api(description = "Create and maintain regular investment plans")
public class RegularInvestmentApiController {

    private static final Logger logger = LoggerFactory.getLogger(RegularInvestmentApiController.class);

    @Autowired
    private UserProfileService profileService;

    @Autowired
    private RegularInvestmentDtoService regularInvestmentService;

    @Autowired
    private RegularInvestmentTransactionDtoService regularInvestmentTransactionService;

    @Autowired
    private OrderGroupDtoErrorMapper orderGroupDtoErrorMapper;

    @Autowired
    private SecureJsonObjectMapper mapper;

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.regularinvestment.v2.uri.rip}")
    @ApiOperation(value = "Load existing regular investment plan", response = RegularInvestmentDto.class)
    public @ResponseBody
    KeyedApiResponse<OrderGroupKey> getRegularInvestmentPlan(
            @PathVariable("account-id") @ApiParam(value = "Account ID of current account", required = true) String accountId,
            @PathVariable("rip-id") @ApiParam(value = "RIP ID to load", required = true) String ripId) {
        OrderGroupKey key = new OrderGroupKey();
        key.setAccountId(accountId);
        key.setOrderGroupId(ripId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, regularInvestmentService, key).performOperation();
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.regular.investment.submit')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.regularinvestment.v2.uri.creation}")
    @ApiOperation(value = "Create a new regular investment plan", response = RegularInvestmentDto.class)
    public @ResponseBody
    KeyedApiResponse<OrderGroupKey> create(
            @PathVariable("account-id") @ApiParam(value = "Account ID of current account", required = true) String accountId,
            @RequestParam(value = "investmentOrder", required = true) @ApiParam(value = "RegularInvestmentDtoV2 in JSON format", required = true) String investmentOrder,
            @RequestParam(value = "x-ro-validate-only", required = false) @ApiParam(value = "Flag indicating the data is for validation only", required = false) String validateOnly)
            throws IOException {

        if (!profileService.isEmulating()) {
            RegularInvestmentDto dto = mapper.readerWithView(JsonViews.Write.class).forType(RegularInvestmentDto.class)
                    .readValue(JsonSanitizer.sanitize(investmentOrder));
            dto.setAccountKey(new AccountKey(accountId));

            if ("true".equals(validateOnly)) {
                logger.info("Validate regular-investment request for account {} ", accountId);
                return new Validate<>(ApiVersion.CURRENT_VERSION, regularInvestmentService, orderGroupDtoErrorMapper, dto)
                        .performOperation();

            } else if ("submit".equals(dto.getStatus())) {
                logger.info("Submit regular-investment request for account {} ", accountId);
                return new Submit<>(ApiVersion.CURRENT_VERSION, regularInvestmentService, orderGroupDtoErrorMapper, dto)
                        .performOperation();

            } else if ("save".equals(dto.getStatus())) {
                logger.info("Save regular-investment request for account {} ", accountId);
                return new Create<>(ApiVersion.CURRENT_VERSION, regularInvestmentService, dto)
                        .performOperation();
            }
        }
        throw new AccessDeniedException("Access Denied");
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.regular.investment.submit')")
    @RequestMapping(method = RequestMethod.POST, value = "${api.regularinvestment.v2.uri.update}")
    @ApiOperation(value = "Update an existing regular investment plan", response = RegularInvestmentDto.class)
    public @ResponseBody
    ApiResponse updateRegularInvestmentPlan(
            @PathVariable("account-id") @ApiParam(value = "Account ID of current account", required = true) String accountId,
            @PathVariable("rip-id") @ApiParam(value = "RIP ID to load", required = true) String ripId,
            @RequestParam("action") @ApiParam(value = "Action to perform from RIPActionV2", required = true) String action)
            throws IOException {

        if (!profileService.isEmulating()) {
            OrderGroupKey key = new OrderGroupKey(accountId, ripId);
            OrderGroupDto orderDto = new OrderGroupDto();
            orderDto.setKey(key);

            RIPAction ripAction = RIPAction.getRIPAction(action);
            orderDto.setStatus(ripAction.getAction());
            RegularInvestmentDto regInvDto = new RegularInvestmentDto(orderDto, null, new InvestmentPeriodDto(null),
                    ripAction.getAction(), null);

            return new Update<>(ApiVersion.CURRENT_VERSION, regularInvestmentService, orderGroupDtoErrorMapper, regInvDto)
                    .performOperation();
        }
        throw new AccessDeniedException("Access Denied");
    }

    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.report.view')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.regularinvestment.v2.uri.rips}")
    @ApiOperation(value = "Search for existing regular investment plan", response = RegularInvestmentDto.class)
    public @ResponseBody
    ApiResponse search(
            @PathVariable("account-id") @ApiParam(value = "Account ID of current account", required = true) String accountId,
            @RequestParam(value = "cache", required = false) @ApiParam(value = "Flag indicating search only in cached data", required = false) String useCache)
            throws IOException {

        List<ApiSearchCriteria> criteria = new ArrayList<>();
        ApiSearchCriteria accountCriteria = new ApiSearchCriteria(Attribute.ACCOUNT_ID, SearchOperation.EQUALS, accountId,
                OperationType.STRING);
        criteria.add(accountCriteria);

        if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache)) {
            ApiSearchCriteria useCacheCriteria = new ApiSearchCriteria("serviceType", ApiSearchCriteria.SearchOperation.EQUALS,
                    "cache", ApiSearchCriteria.OperationType.STRING);
            criteria.add(useCacheCriteria);
        }

        return new SearchByCriteria<>(ApiVersion.CURRENT_VERSION, regularInvestmentTransactionService, criteria)
                .performOperation();
    }

}
