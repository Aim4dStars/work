package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountCashSweepDto;
import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.account.v3.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v3.model.CashSweepInvestmentDto;
import com.bt.nextgen.api.account.v3.model.InitialInvestmentDto;
import com.bt.nextgen.api.account.v3.service.AccountCashSweepDtoService;
import com.bt.nextgen.api.account.v3.service.AccountSubscriptionDtoService;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.service.DepositDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.json.JsonSanitizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller("AccountSubscriptionApiControllerV3")
@Api(description = "Manage product subscriptions and cash sweep details for an account")
public class AccountSubscriptionApiController {

    @Autowired
    @Qualifier("AccountSubscriptionDtoServiceV3")
    private AccountSubscriptionDtoService subscriptionDtoService;

    @Autowired
    @Qualifier("DepositDtoServiceV2")
    private DepositDtoService depositDtoService;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private SecureJsonObjectMapper mapper;

    @Autowired
    @Qualifier("AccountCashSweepDtoServiceV3")
    private AccountCashSweepDtoService accountCashSweepDtoService;

    private static final String API_VERSION = "v3_0";

    /**
     * @param accountId whose subscription is to be fetched
     * @return ApiResponse
     * @throws IOException is thrown when object mapping fails
     */
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.account.subscription}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    ApiResponse getSubscriptionDetails(@PathVariable("account-id") String accountId) throws IOException {
        AccountKey accountKey = new AccountKey(accountId);
        return new FindByKey<>(API_VERSION, subscriptionDtoService, accountKey).performOperation();

    }

    /**
     * @param accountId        Id for the account whose subscription is to be updated
     * @param subscriptionType The subscription to switch to (simple/active)
     * @param investmentId     asset id of the Managed Portfolio for the initial investment
     * @param amount           amount for the initial investment
     * @param depositJson      JSON string for the DepositDto {@link DepositDto}
     * @return ApiResponse
     * @throws IOException is thrown when object mapping fails
     */
    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v3.uri.account.subscription}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    ApiResponse updateSubscription(@PathVariable("account-id") String accountId,
                                   @RequestParam("type") String subscriptionType,
                                   @RequestParam(value = "investment-id", required = false) String investmentId,
                                   @RequestParam(value = "amount", required = false) String amount,
                                   @RequestParam(value = "deposit", required = false) String depositJson) throws IOException {

        final AccountKey key = new AccountKey(accountId);
        final AccountSubscriptionDto accountSubscriptionDto;

        if (StringUtils.isNotBlank(investmentId) && StringUtils.isNotBlank(amount)) {
            final AssetImpl asset = new AssetImpl();
            asset.setAssetId(investmentId);
            accountSubscriptionDto = new AccountSubscriptionDto(key, subscriptionType,
                    Collections.singletonList(new InitialInvestmentDto(asset, new BigDecimal(amount))));
        } else {
            accountSubscriptionDto = new AccountSubscriptionDto(key, subscriptionType);
        }

        if (StringUtils.isNotBlank(depositJson)) {
            final String sanitizedDepositJson = JsonSanitizer.sanitize(depositJson);
            final DepositDto deposit = mapper.readerWithView(JsonViews.Write.class)
                    .forType(new TypeReference<DepositDto>() {
                    }).readValue(sanitizedDepositJson);
            deposit.setKey(new AccountKey(accountId));
            return new UpdateSubscriptionWithDeposit<AccountSubscriptionDto>(
                    new Update<>(API_VERSION, subscriptionDtoService, null, accountSubscriptionDto),
                    new Submit<>("v2_0", depositDtoService, null, deposit)).performOperation();
        }

        return new Update<>(API_VERSION, subscriptionDtoService, null, accountSubscriptionDto).performOperation();
    }

    /**
     * Gets the cash sweep details for the account
     *
     * @param accountId - Account identifier (encoded)
     */
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v3.uri.account.cashSweep}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    @ApiOperation(value = "Gets the cash sweep details for the account", response = CashSweepInvestmentDto.class)
    ApiResponse getCashSweepDetails(@PathVariable("account-id") String accountId) {
        return new FindByKey<>(API_VERSION, accountCashSweepDtoService, new AccountKey(accountId)).performOperation();

    }

    /**
     * Updates the cash sweep details for the account
     *
     * @param accountId         - Account identifier (encoded)
     * @param investmentListStr - String-ified list of assets and percentage allocations
     * @param cashSweepApplied  - Flag to enable/disable cash sweep on account
     * @throws IOException - Throws IOException when parsing for investmentListStr fails
     */
    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v3.uri.account.cashSweep}")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.hasTransactionPermission(#accountId, 'account.details.update')")
    public
    @ResponseBody
    @ApiOperation(value = "Updates the cash sweep details for the account", response = CashSweepInvestmentDto.class)
    ApiResponse updateCashSweepInvestments(@PathVariable("account-id") String accountId,
                                           @RequestParam(value = "sweep", required = false, defaultValue = "false") Boolean cashSweepApplied,
                                           @RequestParam(value = "amount", required = false) BigDecimal minCashSweepAmount,
                                           @RequestParam(value = "assets", required = false) String investmentListStr) throws IOException {
        final List<CashSweepInvestmentDto> cashSweepInvestments = new ArrayList<>();
        if (StringUtils.isNotBlank(investmentListStr)) {
            final String sanitizedInvestmentListJson = JsonSanitizer.sanitize(investmentListStr);
            final List<CashSweepInvestmentDto> values = mapper.readerWithView(JsonViews.Write.class)
                    .forType(new TypeReference<List<CashSweepInvestmentDto>>() {
                    }).readValue(sanitizedInvestmentListJson);
            cashSweepInvestments.addAll(values);
        }
        return new Update<>(API_VERSION, accountCashSweepDtoService, null, new AccountCashSweepDto(new AccountKey(accountId),
                null, cashSweepApplied, minCashSweepAmount, cashSweepInvestments)).performOperation();
    }
}
