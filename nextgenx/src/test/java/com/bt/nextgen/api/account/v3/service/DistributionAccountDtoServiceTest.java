package com.bt.nextgen.api.account.v3.service;

import com.bt.nextgen.api.account.v3.model.AccountAssetKey;
import com.bt.nextgen.api.account.v3.model.DistributionAccountDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedFundHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DistributionAccountDtoServiceTest {
    @InjectMocks
    private DistributionAccountDtoServiceImpl managedFundService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private PortfolioIntegrationService portfolioService;

    @Mock
    private AssetIntegrationService assetService;

    private AccountAssetKey key = new AccountAssetKey(EncodedString.fromPlainText("1234").toString(), "4567");

    private DistributionAccountDto mfa = new DistributionAccountDto(key, "Cash");

    private WrapAccountValuation valuation;
    private WrapAccountDetail accountDetail;
    private SubAccountValuation mfValuation;
    private ManagedFundAsset asset;

    @Before
    public void setup() throws Exception {
        valuation = Mockito.mock(WrapAccountValuation.class);
        accountDetail = Mockito.mock(WrapAccountDetail.class);
        asset = Mockito.mock(ManagedFundAsset.class);
        Mockito.when(asset.getAssetId()).thenReturn("4567");

        mfValuation = Mockito.mock(SubAccountValuation.class);
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);
        Mockito.when(portfolioService.loadWrapAccountValuation(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(valuation);
        Mockito.when(assetService.loadAsset(Mockito.anyString(), Mockito.any(ServiceErrors.class))).thenReturn(asset);
        List<SubAccountValuation> valList = new ArrayList<>();
        valList.add(mfValuation);
        Mockito.when(valuation.getSubAccountValuations()).thenReturn(valList);
        Mockito.when(mfValuation.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        ManagedFundHolding holding = Mockito.mock(ManagedFundHolding.class);
        Mockito.when(holding.getDistributionMethod()).thenReturn(DistributionMethod.CASH);
        Mockito.when(holding.getAsset()).thenReturn(asset);

        List<AccountHolding> holdings = new ArrayList<>();
        holdings.add(holding);

        Mockito.when(mfValuation.getHoldings()).thenReturn(holdings);
    }

    @Test
    public void testUpdate_WhenGivenValiDAccount_ThenMFAReturned() {
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        DistributionAccountDto result = managedFundService.update(mfa, serviceErrors);
        Assert.assertEquals(mfa.getKey(), result.getKey());
        Assert.assertEquals(mfa.getAvailableDistributionOptions(), result.getAvailableDistributionOptions());
        Assert.assertEquals(mfa.getDistributionOption(), result.getDistributionOption());
    }


}
