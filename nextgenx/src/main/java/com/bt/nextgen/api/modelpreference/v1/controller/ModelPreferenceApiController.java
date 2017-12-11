package com.bt.nextgen.api.modelpreference.v1.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.modelportfolio.v2.validation.ModelPortfolioDtoErrorMapper;
import com.bt.nextgen.api.modelpreference.v1.model.SubaccountPreferencesActionDto;
import com.bt.nextgen.api.modelpreference.v1.service.AccountPreferencesDtoService;
import com.bt.nextgen.api.modelpreference.v1.service.SubaccountPreferencesDtoService;
import com.bt.nextgen.api.modelpreference.v1.service.SubaccountPreferencesSubmitDtoService;
import com.bt.nextgen.api.order.model.PreferenceActionDto;
import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.KeyedApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.Submit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;

@Controller
@RequestMapping(produces = "application/json")
public class ModelPreferenceApiController {

    @Autowired
    private AccountPreferencesDtoService accountModelService;

    @Autowired
    private SubaccountPreferencesDtoService subAccountService;

    @Autowired
    private SubaccountPreferencesSubmitDtoService subAccountSubmitService;

    @Autowired
    private ModelPortfolioDtoErrorMapper modelPortfolioErrorMapper;

    @Autowired
    @Qualifier("SecureJsonObjectMapper")
    private static ObjectMapper mapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelpreference.v1.uri.accountpreference}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<AccountKey> getAccountPreference(@PathVariable("account-id") String accountId) {
        AccountKey key = new AccountKey(accountId);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, accountModelService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelpreference.v1.uri.subaccountpreference}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<AccountKey> getSubaccountPreference(
            @PathVariable("subaccount-id") String subaccountId) {
        AccountKey key = new AccountKey(subaccountId);

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, subAccountService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.modelpreference.v1.uri.subaccountpreference}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody KeyedApiResponse<AccountKey> getSubaccountPreference(@PathVariable("subaccount-id") String subaccountId,
            @RequestBody String exclusionJson) throws IOException {
        AccountKey key = new AccountKey(subaccountId);
        String sanitizedExclusionJson = JsonSanitizer.sanitize(exclusionJson);
        List<PreferenceActionDto> preferences = mapper.readerWithView(JsonViews.Write.class)
                .forType(new TypeReference<List<PreferenceActionDto>>() {
                }).readValue(sanitizedExclusionJson);

        SubaccountPreferencesActionDto dto = new SubaccountPreferencesActionDto(key, preferences);

        return new Submit<>(ApiVersion.CURRENT_VERSION, subAccountSubmitService, modelPortfolioErrorMapper, dto)
                .performOperation();
    }
}
