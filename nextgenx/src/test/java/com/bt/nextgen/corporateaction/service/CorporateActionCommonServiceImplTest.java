package com.bt.nextgen.corporateaction.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.integration.account.WrapAccount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionContext;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionClientAccountDetails;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionCommonServiceImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountBalance;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetPrice;
import com.bt.nextgen.service.integration.asset.AssetPriceSource;
import com.bt.nextgen.service.integration.asset.AssetPriceStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.financialmarketinstrument.FinancialMarketInstrumentIntegrationService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionCommonServiceImplTest {
    @InjectMocks
    private CorporateActionCommonServiceImpl commonService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private ClientIntegrationService clientService;

    @Mock
    private FinancialMarketInstrumentIntegrationService fmiService;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CorporateActionContext context;

    private Asset shareAsset;
    private Asset managedFundAsset;

    @Before
    public void setup() {
        shareAsset = mock(Asset.class);
        when(shareAsset.getAssetCode()).thenReturn("XXX");
        when(shareAsset.getAssetName()).thenReturn("YYY");
        when(shareAsset.getAssetId()).thenReturn("0");
        when(shareAsset.getAssetType()).thenReturn(AssetType.SHARE);

        managedFundAsset = mock(ManagedFundAsset.class);
        when(managedFundAsset.getAssetCode()).thenReturn("XXX");
        when(managedFundAsset.getAssetName()).thenReturn("YYY");
        when(managedFundAsset.getAssetId()).thenReturn("0");
        when(managedFundAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        AssetPrice assetPrice = mock(AssetPrice.class);
        when(assetPrice.getAsset()).thenReturn(shareAsset);
        when(assetPrice.getAssetPriceStatus()).thenReturn(AssetPriceStatus.SUCCESS);
        when(assetPrice.getAssetPriceSource()).thenReturn(AssetPriceSource.AVALOQ);
        when(assetPrice.getLastPrice()).thenReturn(1000.0);

        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(null);
        when(clientService.loadClientMap(any(ServiceErrors.class))).thenReturn(null);
        when(accountService.loadAccountBalancesMap(any(ServiceErrors.class))).thenReturn(null);
        when(fmiService.loadAssetPrice(Mockito.anyString(), any(Asset.class), Mockito.anyBoolean(), Mockito.anyBoolean()))
                .thenReturn(assetPrice);
        when(userProfileService.getUserId()).thenReturn("201603884");
    }

    @Test
    public void testLoadClientAccountDetails() {
        when(context.getBrokerPositionId()).thenReturn("0");
        CorporateActionClientAccountDetails details = commonService.loadClientAccountDetails(context, null, null);
        Assert.assertNotNull(details);
    }

    @Test
    public void testLoadClientAccountDetails_withAccounts() {
        when(context.getBrokerPositionId()).thenReturn(null);
        CorporateActionAccount corporateActionAccount = mock(CorporateActionAccount.class);
        List<CorporateActionAccount> accounts = new ArrayList<>();
        accounts.add(corporateActionAccount);

        CorporateActionClientAccountDetails details = commonService.loadClientAccountDetails(context, accounts, null);
        assertNotNull(details);
    }

    @Test
    public void testLoadClientAccountDetails_whenIsDealerGroupOrInvestmentManagerthenReturnResults() {
        when(context.isDealerGroupOrInvestmentManager()).thenReturn(Boolean.TRUE);

        Map<AccountKey, WrapAccount> accounts = new HashMap<>();
        WrapAccount wrapAccount = mock(WrapAccount.class);

        AccountKey accountKey = AccountKey.valueOf("0");
        when(wrapAccount.getAccountKey()).thenReturn(accountKey);

        accounts.put(accountKey, wrapAccount);
        when(accountService.loadWrapAccountWithoutContainers(any(ServiceErrors.class))).thenReturn(accounts);

        Map<AccountKey, AccountBalance> accountBalanceMap = new HashMap<>();

        AccountBalance accountBalance = mock(AccountBalance.class);
        when(accountBalance.getKey()).thenReturn(accountKey);
        accountBalanceMap.put(accountKey, accountBalance);

        when(accountService.loadAccountBalancesMap(any(ServiceErrors.class))).thenReturn(accountBalanceMap);

        CorporateActionClientAccountDetails details = commonService.loadClientAccountDetails(context, null, null);
        Assert.assertNotNull(details);
    }

    @Test
    public void testGetShareAssetPrice() {
        BigDecimal lastPrice = commonService.getAssetPrice(shareAsset, null);
        assertEquals(lastPrice, (BigDecimal.valueOf(1000)));
    }

    @Test
    public void testGetManagedFundAssetPrice() {
        BigDecimal lastPrice = commonService.getAssetPrice(managedFundAsset, null);
        assertEquals(lastPrice, (BigDecimal.valueOf(1000)));
    }

    @Test
    public void testUserProfileService() {
        UserProfileService userProfileService = commonService.getUserProfileService();
        Assert.assertNotNull(userProfileService);
    }
}
