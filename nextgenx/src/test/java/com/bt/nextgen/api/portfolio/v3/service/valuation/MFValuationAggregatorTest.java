package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedFundValuationDto;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class MFValuationAggregatorTest {

    @InjectMocks
    public MFValuationAggregator mfValuationAggregator;

    @Mock
    public UserProfileService userProfileService;

    @Mock
    public AccountIntegrationService accountIntegrationService;

    @Mock
    public DistributionAccountDtoService mfaDtoService;

    private AccountKey accountKey;
    private WrapAccountDetail accountDetail;
    private ManagedFundAccountValuationImpl subAccount;
    private BigDecimal accountBalance;

    @Before
    public void setup() {

        accountKey = AccountKey.valueOf("plaintext");
        accountDetail = Mockito.mock(WrapAccountDetail.class);

        List<AccountHolding> mfList = new ArrayList<>();

        AssetImpl mfAsset1 = new AssetImpl();
        mfAsset1.setAssetType(AssetType.MANAGED_FUND);
        mfAsset1.setAssetName("assetName1");
        mfAsset1.setAssetCode("assetCode1");

        AssetImpl mfAsset2 = new AssetImpl();
        mfAsset2.setAssetType(AssetType.MANAGED_FUND);
        mfAsset2.setAssetName("assetName2");
        mfAsset2.setAssetCode("assetCode2");

        ManagedFundHoldingImpl mfHolding1 = new ManagedFundHoldingImpl();
        mfHolding1.setAsset(mfAsset1);
        mfHolding1.setAccruedIncome(BigDecimal.valueOf(99.90d));
        mfHolding1.setAvailableUnits(BigDecimal.valueOf(11));
        mfHolding1.setCost(BigDecimal.valueOf(111));
        mfHolding1.setUnitPrice(BigDecimal.valueOf(1111));
        mfHolding1.setUnitPriceDate(new DateTime());
        mfHolding1.setUnits(BigDecimal.valueOf(11111));
        mfHolding1.setYield(BigDecimal.valueOf(111111));
        mfHolding1.setMarketValue(BigDecimal.valueOf(94000));
        mfHolding1.setHasPending(false);
        mfHolding1.setDistributionMethod(DistributionMethod.REINVEST);
        mfHolding1.setHoldingKey(HoldingKey.valueOf("holding1", mfAsset1.getAssetName()));

        ManagedFundHoldingImpl mfHolding2 = new ManagedFundHoldingImpl();
        mfHolding2.setAsset(mfAsset2);
        mfHolding2.setAccruedIncome(BigDecimal.valueOf(99.90d));
        mfHolding2.setAvailableUnits(BigDecimal.ZERO);
        mfHolding2.setCost(BigDecimal.valueOf(222));
        mfHolding2.setUnitPrice(BigDecimal.valueOf(2222));
        mfHolding2.setUnitPriceDate(new DateTime());
        mfHolding2.setUnits(BigDecimal.valueOf(22222));
        mfHolding2.setYield(BigDecimal.valueOf(22222));
        mfHolding2.setHasPending(true);
        mfHolding2.setMarketValue(BigDecimal.valueOf(34000));
        mfHolding2.setDistributionMethod(DistributionMethod.CASH);
        mfHolding2.setHoldingKey(HoldingKey.valueOf("holding2", mfAsset2.getAssetName()));

        mfList.add(mfHolding1);
        mfList.add(mfHolding2);

        subAccount = new ManagedFundAccountValuationImpl();
        subAccount.addHoldings(mfList);

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);

        DistributionMethod method = DistributionMethod.forIntlId("div_revst_yes");
        DistributionMethod method2 = DistributionMethod.forIntlId("div_revst_no");
        List<DistributionMethod> methods = new ArrayList<>();
        methods.add(method);
        methods.add(method2);

        Mockito.when(mfaDtoService.getAvailableDistributionMethod(Mockito.any(Asset.class))).thenReturn(methods);
    }

    @Test
    public void testBuildValuationDto_whenMFSubAccountPassed_thenMFValuationDtosCreated() {

        List<InvestmentValuationDto> dtoList = mfValuationAggregator.getManagedFundValuationDtos(subAccount.getHoldings(),
                accountBalance);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(2, dtoList.size());

        ManagedFundValuationDto dto = (ManagedFundValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName1", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(DistributionMethod.REINVEST.getDisplayName(), dto.getDistributionMethod());
        Assert.assertEquals(false, dto.getPendingSellDown());

        dto = (ManagedFundValuationDto) dtoList.get(1);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName2", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDistributionMethod());
        Assert.assertEquals(true, dto.getPendingSellDown());
        Assert.assertEquals("Managed funds", dto.getCategoryName());
    }

}
