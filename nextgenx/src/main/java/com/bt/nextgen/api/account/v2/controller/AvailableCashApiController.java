package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.ParameterisedAccountKey;
import com.bt.nextgen.api.account.v2.service.AvailableCashDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AvailableCashController is used to display the available cash for the order
 * 
 * @author L061041
 * 
 */
@Deprecated
@Controller("AvailableCashApiControllerV2")
@RequestMapping(produces = "application/json")
public class AvailableCashApiController {
    @Autowired
    private AvailableCashDtoService availableCashDtoService;

    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.cash}")
    public @ResponseBody
    ApiResponse getAvailableCash(@PathVariable("account-id") String accountId,
                                 @RequestParam(value = "cache", required = false) String useCache) {

        Map<String, String> params = new HashMap<>();
        ParameterisedAccountKey key = new ParameterisedAccountKey(accountId, params);

        if (StringUtils.isNotEmpty(useCache) && "true".equalsIgnoreCase(useCache))
        {
            params.put("serviceType", "cache");
        }

        return new FindByKey<>(ApiVersion.CURRENT_VERSION, availableCashDtoService, key).performOperation();
    }
}