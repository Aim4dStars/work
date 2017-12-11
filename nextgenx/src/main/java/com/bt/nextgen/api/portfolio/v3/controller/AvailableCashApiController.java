package com.bt.nextgen.api.portfolio.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.portfolio.v3.service.AvailableCashDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * AvailableCashController is used to display the available cash for the order
 * 
 * @author L061041
 * 
 */
@Controller("AvailableCashApiControllerV3")
@RequestMapping(produces = "application/json")
public class AvailableCashApiController {
    @Autowired
    private AvailableCashDtoService availableCashDtoService;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.portfolio.v3.uri.cash}")
    public @ResponseBody
    ApiResponse getAvailableCash(@PathVariable("account-id") String accountId) {
        AccountKey key = new AccountKey(accountId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, availableCashDtoService, key).performOperation();
    }
}