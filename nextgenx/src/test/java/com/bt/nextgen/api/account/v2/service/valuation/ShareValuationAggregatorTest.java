package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ShareValuationDto;
import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
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
public class ShareValuationAggregatorTest {

    @InjectMocks
    public ShareValuationAggregator shareValuationAggregator;

    @Mock
    public AccountIntegrationService accountIntegrationService;

    @Mock
    public DistributionAccountDtoService mfaDtoService;

    private AccountKey accountKey;
    private WrapAccountDetail accountDetail;
    private ShareAccountValuationImpl subAccount;
    private BigDecimal accountBalance;
    private DateTime effectiveDate;

    @Before
    public void setup() {
        accountKey = AccountKey.valueOf("plaintext");
        accountDetail = Mockito.mock(WrapAccountDetail.class);

        effectiveDate = new DateTime();

        List<AccountHolding> shareList = new ArrayList<>();

        AssetImpl asset1 = new AssetImpl();
        asset1.setAssetType(AssetType.SHARE);
        asset1.setAssetName("assetName1");
        asset1.setAssetCode("assetCode1");

        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetType(AssetType.SHARE);
        asset2.setAssetName("assetName2");
        asset2.setAssetCode("assetCode2");

        ShareHoldingImpl shareHolding1 = new ShareHoldingImpl();
        shareHolding1.setAsset(asset1);
        shareHolding1.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding1.setAvailableUnits(BigDecimal.valueOf(110));
        shareHolding1.setCost(BigDecimal.valueOf(111));
        shareHolding1.setUnitPrice(BigDecimal.valueOf(1111));
        shareHolding1.setUnitPriceDate(effectiveDate);
        shareHolding1.setUnits(BigDecimal.valueOf(11111));
        shareHolding1.setYield(BigDecimal.valueOf(111111));
        shareHolding1.setHasPending(false);
        shareHolding1.setMarketValue(BigDecimal.valueOf(94000));
        shareHolding1.setHoldingKey(HoldingKey.valueOf("holding1", asset1.getAssetName()));
        shareHolding1.setDistributionMethod(DistributionMethod.REINVEST);

        ShareHoldingImpl shareHolding2 = new ShareHoldingImpl();
        shareHolding2.setAsset(asset2);
        shareHolding2.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding2.setAvailableUnits(BigDecimal.TEN);
        shareHolding2.setCost(BigDecimal.valueOf(222));
        shareHolding2.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding2.setUnitPriceDate(effectiveDate);
        shareHolding2.setUnits(BigDecimal.valueOf(22222));
        shareHolding2.setYield(BigDecimal.valueOf(22222));
        shareHolding2.setHasPending(false);
        shareHolding2.setMarketValue(BigDecimal.valueOf(34000));
        shareHolding2.setHoldingKey(HoldingKey.valueOf("holding2", asset2.getAssetName()));
        shareHolding2.setDistributionMethod(DistributionMethod.CASH);

        ShareHoldingImpl shareHolding3 = new ShareHoldingImpl();
        shareHolding3.setAsset(asset2);
        shareHolding3.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding3.setAvailableUnits(BigDecimal.TEN);
        shareHolding3.setCost(BigDecimal.valueOf(222));
        shareHolding3.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding3.setUnitPriceDate(effectiveDate);
        shareHolding3.setUnits(BigDecimal.valueOf(22222));
        shareHolding3.setYield(BigDecimal.valueOf(22222));
        shareHolding3.setHasPending(false);
        shareHolding3.setMarketValue(BigDecimal.valueOf(0));
        shareHolding3.setHoldingKey(HoldingKey.valueOf("holding3", asset2.getAssetName()));
        shareHolding3.setDistributionMethod(DistributionMethod.CASH);

        ShareHoldingImpl shareHolding4 = new ShareHoldingImpl();
        shareHolding4.setAsset(asset2);
        shareHolding4.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding4.setAvailableUnits(BigDecimal.ZERO);
        shareHolding4.setCost(BigDecimal.valueOf(222));
        shareHolding4.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding4.setUnitPriceDate(effectiveDate);
        shareHolding4.setUnits(BigDecimal.valueOf(22222));
        shareHolding4.setYield(BigDecimal.valueOf(22222));
        shareHolding4.setHasPending(true);
        shareHolding4.setMarketValue(BigDecimal.valueOf(34000));
        shareHolding4.setHoldingKey(HoldingKey.valueOf("holding4", asset2.getAssetName()));
        shareHolding4.setDistributionMethod(DistributionMethod.CASH);

        shareList.add(shareHolding1);
        shareList.add(shareHolding2);
        shareList.add(shareHolding3);
        shareList.add(shareHolding4);

        subAccount = new ShareAccountValuationImpl(AssetType.SHARE);
        subAccount.addHoldings(shareList);

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
    public void testBuildValuationDto_whenLSSubAccountPassed_thenShareValuationDtosCreated() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentValuationDto> dtoList = shareValuationAggregator.getShareValuationDtos(subAccount, accountBalance);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(4, dtoList.size());

        ShareValuationDto dto = (ShareValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName1", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(false, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(1111), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(94000), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(110), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(111), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(DistributionMethod.REINVEST.getDisplayName(), dto.getDividendMethod());

        dto = (ShareValuationDto) dtoList.get(1);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName2", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(false, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(34000), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(10), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(222), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDividendMethod());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDividendMethod());

        dto = (ShareValuationDto) dtoList.get(2);

        Assert.assertEquals("assetName2", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(false, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(10), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(222), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDividendMethod());

        dto = (ShareValuationDto) dtoList.get(3);

        Assert.assertEquals("assetName2", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(true, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(34000), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(222), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDividendMethod());
        Assert.assertEquals("Listed securities", dto.getCategoryName());
    }
}
