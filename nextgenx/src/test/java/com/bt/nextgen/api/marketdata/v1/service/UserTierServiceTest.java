package com.bt.nextgen.api.marketdata.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.security.api.service.PermissionAccountDtoService;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class UserTierServiceTest {
    private final Logger logger = LoggerFactory.getLogger(UserTierServiceTest.class);

    @InjectMocks
    public UserTierServiceImpl userTierService;

    @Mock
    private Configuration configuration;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private PermissionBaseDtoService permissionBaseService;

    @Mock
    private PermissionAccountDtoService permissionAccountService;

    private static final String tradeAccess = "trade access";
    private static final String noTradeAccess = "no trade access";
    private static final String realtime = "realtime";
    private static final String delayed = "delayed";

    @Before
    public void setupConfiguration() {
        Mockito.when(configuration.getString(eq("markit.on.demand.adviser.tier"))).thenReturn(realtime);
        Mockito.when(configuration.getString(eq("markit.on.demand.investor.tier"))).thenReturn(delayed);

        Mockito.when(permissionBaseService.hasBasicPermission(Mockito.anyString())).thenReturn(true);
        Mockito.when(permissionAccountService.canTransact(eq(tradeAccess), Mockito.anyString())).thenReturn(true);
        Mockito.when(permissionAccountService.canTransact(eq(noTradeAccess), Mockito.anyString())).thenReturn(false);
    }

    @Test
    public void testGetUserTier_whenInvestorHasTradePermission_thenRealtime() throws Exception {
        String userTier = userTierService.getUserTier(new AccountKey(tradeAccess));
        assertEquals(realtime, userTier);
    }

    @Test
    public void testGetUserTier_whenInvestorDoesNotHaveTradePermission_thenDelayed() throws Exception {
        String userTier = userTierService.getUserTier(new AccountKey(noTradeAccess));
        assertEquals(delayed, userTier);
    }

    @Test
    public void testGetUserTier_whenAccountNull_thenRealtime() throws Exception {
        String userTier = userTierService.getUserTier(null);
        assertEquals(realtime, userTier);
    }
}
