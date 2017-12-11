package com.bt.nextgen.api.account.v3.controller;

import com.bt.nextgen.api.account.v3.model.AccountAssetKey;
import com.bt.nextgen.api.account.v3.model.DistributionAccountDto;
import com.bt.nextgen.api.account.v3.service.DistributionAccountDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
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
 * ValuationMovementApiController is the api used to load the valuation movements for the account
 * 
 */
@Controller("ReinvestApiControllerV3")
@RequestMapping(produces = "application/json")
public class ReinvestApiController {
    private static final String DISTRIBUTION_OPTION = "distributionOption";
    @Autowired
    private DistributionAccountDtoService distributionAccountService;

    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v3.uri.subaccountReinvestment}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'Submit_trade_to_executed')")
    public @ResponseBody ApiResponse updateReinvestOptions(@PathVariable("subaccount-id") String subaccountId,
            @PathVariable("asset-id") String assetId, @RequestParam(DISTRIBUTION_OPTION) String option) {
        AccountAssetKey key = new AccountAssetKey(subaccountId, assetId);
        DistributionAccountDto mfa = new DistributionAccountDto(key, option);
        return new Update<>("v3_0", distributionAccountService, null, mfa).performOperation();
    }

    /**
     * This method has been created to support direct users to update 'Distribution option'. Since direct level permissions are
     * based on account, need account-id to evaluate permissions.
     *
     * @param accountId
     * @param subaccountId
     * @param assetId
     * @param option
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "${api.account.v3.uri.accountReinvestment}", produces = "application/json")
    @PreAuthorize("isAuthenticated() and @acctPermissionService.canTransact(#accountId, 'account.trade.submit')")
    public @ResponseBody ApiResponse updateAccountReinvestOptions(@PathVariable("account-id") String accountId,
            @PathVariable("subaccount-id") String subaccountId, @PathVariable("asset-id") String assetId,
            @RequestParam(DISTRIBUTION_OPTION) String option) {
        AccountAssetKey key = new AccountAssetKey(subaccountId, assetId);
        DistributionAccountDto mfa = new DistributionAccountDto(key, option);
        return new Update<>("v3_0", distributionAccountService, null, mfa).performOperation();
    }
}
