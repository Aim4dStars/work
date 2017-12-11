package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.IncomePreferenceDto;
import com.bt.nextgen.api.account.v3.service.IncomePreferenceDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.Update;
import com.bt.nextgen.service.integration.account.SubAccountKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * IncomePreferenceApiController is the api used to update the income-preference for a SubAccount.
 * 
 */
@Controller("IncomePreferenceApiControllerV3")
@RequestMapping(produces = "application/json")
public class IncomePreferenceApiController {

    @Autowired
    private IncomePreferenceDtoService incomePreferenceService;

    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v3.uri.subaccountIncomePreference}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.trade.submit')")
    public @ResponseBody
    ApiResponse updateIncomePreference(@PathVariable("account-id") String accountId,
            @PathVariable("subaccount-id") String subAccountId, @PathVariable("preference") String preference) {

        IncomePreferenceDto prefDto = new IncomePreferenceDto(SubAccountKey.valueOf(subAccountId), preference);
        return new Update<>("v3_0", incomePreferenceService, null, prefDto).performOperation();
    }
}
