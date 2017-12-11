package com.bt.nextgen.api.account.v2.controller;

import com.bt.nextgen.api.account.v2.model.AccountAssetKey;
import com.bt.nextgen.api.account.v2.model.DistributionAccountDto;
import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.api.operation.SearchByKey;
import com.bt.nextgen.core.api.operation.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ValuationMovementApiController is the api used to load the valuation
 * movements for the account
 * 
 */
@Deprecated
@Controller("ReinvestApiControllerV2")
@RequestMapping(produces = "application/json")
public class ReinvestApiController {
    private static final String DISTRIBUTION_OPTION = "distributionOption";
    @Autowired
    private DistributionAccountDtoService distributionAccountService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.reinvestment}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    ApiResponse getReinvestOptions(@PathVariable("account-id") String accountId, @PathVariable("asset-id") String assetId) {
        AccountAssetKey key = new AccountAssetKey(accountId, assetId);
        return new FindByKey<>(ApiVersion.CURRENT_VERSION, distributionAccountService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.account.v2.uri.reinvestments}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_account_reports')")
    public @ResponseBody
    ApiResponse getReinvestOptions(@PathVariable("account-id") String accountId) {
        AccountAssetKey key = new AccountAssetKey(accountId, null);
        return new SearchByKey<>(ApiVersion.CURRENT_VERSION, distributionAccountService, key).performOperation();
    }

    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v2.uri.reinvestment}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.trade.submit')")
    public @ResponseBody
    ApiResponse updateReinvestOptions(@PathVariable("account-id") String accountId, @PathVariable("asset-id") String assetId,
            @RequestParam(DISTRIBUTION_OPTION) String option) {
        AccountAssetKey key = new AccountAssetKey(accountId, assetId);
        DistributionAccountDto mfa = new DistributionAccountDto(key, option);
        return new Update<>(ApiVersion.CURRENT_VERSION, distributionAccountService, null, mfa).performOperation();
    }

}
