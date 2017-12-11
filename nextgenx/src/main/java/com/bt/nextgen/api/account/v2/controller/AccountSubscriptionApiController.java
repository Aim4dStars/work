package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v1.model.DepositDto;
import com.bt.nextgen.api.account.v1.service.DepositDtoService;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDepositDto;
import com.bt.nextgen.api.account.v2.model.AccountSubscriptionDto;
import com.bt.nextgen.api.account.v2.model.InitialInvestmentAssetDto;
import com.bt.nextgen.api.account.v2.service.AccountSubscriptionDtoHelperService;
import com.bt.nextgen.api.account.v2.service.AccountSubscriptionDtoService;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.config.SecureJsonObjectMapper;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Submit;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.json.JsonSanitizer;
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
import java.util.Collections;

@Deprecated
@Controller("AccountSubscriptionApiControllerV2")
public class AccountSubscriptionApiController {

    @Autowired
    @Qualifier("AccountSubscriptionDtoServiceV2")
    private AccountSubscriptionDtoService subscriptionDtoService;

    @Autowired
    @Qualifier("DepositDtoServiceV1")
    private DepositDtoService depositDtoService;

    /**
     * @param accountId        Id for the account whose subscription is to be updated
     * @param subscriptionType The subscription to switch to (simple/active)
     * @param investmentId     asset id of the Managed Portfolio for the initial investment
     * @param amount           amount for the initial investment
     * @param depositJson      JSON string for the DepositDto {@link com.bt.nextgen.api.account.v1.model.DepositDto}
     * @return ApiResponse
     * @throws IOException is thrown when object mapping fails
     */
    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v2.uri.updateSubscription}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public
    @ResponseBody
    ApiResponse updateSubscription(@PathVariable("account-id") String accountId,
                                   @RequestParam("type") String subscriptionType,
                                   @RequestParam(value = "investment-id", required = false) String investmentId,
                                   @RequestParam(value = "amount", required = false) String amount,
                                   @RequestParam(value = "deposit", required = false) String depositJson) throws IOException {

        AccountKey key = new AccountKey(accountId);
        AccountSubscriptionDto accountSubscriptionDto;

        if (StringUtils.isNotBlank(investmentId) && StringUtils.isNotBlank(amount)) {
            AssetImpl asset = new AssetImpl();
            asset.setAssetId(investmentId);
            accountSubscriptionDto = new AccountSubscriptionDto(key, subscriptionType,
                    Collections.singletonList(new InitialInvestmentAssetDto(asset, new BigDecimal(amount))));
        } else {
            accountSubscriptionDto = new AccountSubscriptionDto(key, subscriptionType);
        }

        if (StringUtils.isNotBlank(depositJson)) {
            final String sanitizedDepositJson = JsonSanitizer.sanitize(depositJson);
            final DepositDto depositDto = new SecureJsonObjectMapper().readerWithView(JsonViews.Write.class)
                    .forType(new TypeReference<DepositDto>() {
                    }).readValue(sanitizedDepositJson);
            depositDto.setKey(new com.bt.nextgen.api.account.v1.model.AccountKey(EncodedString.toPlainText(accountId)));
            return new AccountSubscriptionDtoHelperService<AccountSubscriptionDepositDto>(
                    new Update<>("v2_0", subscriptionDtoService, null, accountSubscriptionDto),
                    new Submit<>(ApiVersion.CURRENT_VERSION, depositDtoService, null, depositDto)).performOperation();
        }

        return new Update<>("v2_0", subscriptionDtoService, null, accountSubscriptionDto).performOperation();

    }
}
