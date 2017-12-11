package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserTierServiceImpl implements UserTierService {

    private static final String MARKIT_ADVISER_TIER = "markit.on.demand.adviser.tier";
    private static final String MARKIT_INVESTOR_TIER = "markit.on.demand.investor.tier";

    @Autowired
    private Configuration configuration;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private PermissionBaseDtoService permissionBaseService;

    @Autowired
    private PermissionAccountDtoService permissionAccountService;

    @Override
    public String getUserTier(AccountKey accountKey) {
        boolean showRealtimePrice = false;
        if (accountKey != null) {
            showRealtimePrice = permissionAccountService.canTransact(accountKey.getAccountId(), "account.trade.create");
        } else {
            showRealtimePrice = permissionBaseService.hasBasicPermission("marketinformation.realtimeprice.view");
        }

        if (showRealtimePrice) {
            return configuration.getString(MARKIT_ADVISER_TIER);
        }
        return configuration.getString(MARKIT_INVESTOR_TIER);
    }

    @Override
    public boolean isShareEnabled() {
        if (userProfileService.isInvestor()) {
            // permissionAccountService
            return false;
        }
        return true;
    }
}
