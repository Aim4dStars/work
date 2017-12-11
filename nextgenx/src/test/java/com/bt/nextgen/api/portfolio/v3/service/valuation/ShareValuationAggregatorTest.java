package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.account.v3.service.DistributionAccountDtoService;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HinType;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
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
        asset1.setAssetId("232323");
        asset1.setStatus(AssetStatus.OPEN);

        AssetImpl asset2 = new AssetImpl();
        asset2.setAssetType(AssetType.SHARE);
        asset2.setAssetName("assetName2");
        asset2.setAssetCode("assetCode2");
        asset2.setAssetId("343434");

        AssetImpl asset3 = new AssetImpl();
        asset3.setAssetType(AssetType.SHARE);
        asset3.setAssetName("assetName3");
        asset3.setAssetCode("assetCode3");
        asset3.setAssetId("243432");

        AssetImpl asset4 = new AssetImpl();
        asset4.setAssetType(AssetType.SHARE);
        asset4.setAssetName("assetName4");
        asset4.setAssetCode("assetCode4");
        asset4.setAssetId("334232");

        AssetImpl asset5 = new AssetImpl();
        asset5.setAssetType(AssetType.SHARE);
        asset5.setAssetName("assetName5");
        asset5.setAssetCode("assetCode5");
        asset5.setAssetId("244533");

        ShareHoldingImpl shareHolding1 = new ShareHoldingImpl();
        shareHolding1.setAsset(asset1);
        shareHolding1.setHinType(HinType.CUSTODIAL);
        shareHolding1.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding1.setAvailableUnits(BigDecimal.valueOf(110));
        shareHolding1.setCost(BigDecimal.valueOf(111));
        shareHolding1.setUnitPrice(BigDecimal.valueOf(1111));
        shareHolding1.setUnitPriceDate(effectiveDate);
        shareHolding1.setUnits(BigDecimal.valueOf(11111));
        shareHolding1.setYield(BigDecimal.valueOf(111111));
        shareHolding1.setHasPending(false);
        shareHolding1.setMarketValue(BigDecimal.valueOf(0));
        shareHolding1.setEstdGainDollar(BigDecimal.valueOf(0.00));
        shareHolding1.setEstdGainPercent(BigDecimal.valueOf(5));
        shareHolding1.setHoldingKey(HoldingKey.valueOf("holding1", asset1.getAssetName()));
        shareHolding1.setDistributionMethod(DistributionMethod.REINVEST);

        ShareHoldingImpl shareHolding2 = new ShareHoldingImpl();
        shareHolding2.setAsset(asset2);
        shareHolding2.setHinType(HinType.CUSTODIAL);
        shareHolding2.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding2.setAvailableUnits(BigDecimal.TEN);
        shareHolding2.setCost(BigDecimal.valueOf(222));
        shareHolding2.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding2.setUnitPriceDate(effectiveDate);
        shareHolding2.setUnits(BigDecimal.valueOf(22222));
        shareHolding2.setYield(BigDecimal.valueOf(22222));
        shareHolding2.setHasPending(false);
        shareHolding2.setMarketValue(BigDecimal.valueOf(0));
        shareHolding2.setEstdGainDollar(BigDecimal.valueOf(20));
        shareHolding2.setEstdGainPercent(BigDecimal.valueOf(5));
        shareHolding2.setHoldingKey(HoldingKey.valueOf("holding2", asset2.getAssetName()));
        shareHolding2.setDistributionMethod(DistributionMethod.CASH);

        ShareHoldingImpl shareHolding3 = new ShareHoldingImpl();
        shareHolding3.setAsset(asset3);
        shareHolding3.setHinType(HinType.CUSTODIAL);
        shareHolding3.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding3.setAvailableUnits(BigDecimal.TEN);
        shareHolding3.setCost(BigDecimal.valueOf(222));
        shareHolding3.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding3.setUnitPriceDate(effectiveDate);
        shareHolding3.setUnits(BigDecimal.valueOf(22222));
        shareHolding3.setYield(BigDecimal.valueOf(22222));
        shareHolding3.setHasPending(false);
        shareHolding3.setMarketValue(BigDecimal.valueOf(222));
        shareHolding3.setEstdGainDollar(BigDecimal.valueOf(0));
        shareHolding3.setEstdGainPercent(BigDecimal.valueOf(5));
        shareHolding3.setHoldingKey(HoldingKey.valueOf("holding3", asset2.getAssetName()));
        shareHolding3.setDistributionMethod(DistributionMethod.CASH);

        ShareHoldingImpl shareHolding4 = new ShareHoldingImpl();
        shareHolding4.setAsset(asset4);
        shareHolding4.setHinType(HinType.INDIVIDUAL);
        shareHolding4.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding4.setAvailableUnits(BigDecimal.ZERO);
        shareHolding4.setCost(BigDecimal.valueOf(222));
        shareHolding4.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding4.setUnitPriceDate(effectiveDate);
        shareHolding4.setUnits(BigDecimal.valueOf(22222));
        shareHolding4.setYield(BigDecimal.valueOf(22222));
        shareHolding4.setHasPending(true);
        shareHolding4.setMarketValue(BigDecimal.valueOf(34000));
        shareHolding4.setEstdGainDollar(BigDecimal.valueOf(20));
        shareHolding4.setEstdGainPercent(BigDecimal.valueOf(5));
        shareHolding4.setHoldingKey(HoldingKey.valueOf("holding4", asset2.getAssetName()));

        ShareHoldingImpl shareHolding5 = new ShareHoldingImpl();
        shareHolding5.setAsset(asset4);
        shareHolding5.setHinType(HinType.INDIVIDUAL);
        shareHolding5.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding5.setAvailableUnits(BigDecimal.valueOf(2));
        shareHolding5.setCost(BigDecimal.valueOf(222));
        shareHolding5.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding5.setUnitPriceDate(effectiveDate);
        shareHolding5.setUnits(BigDecimal.valueOf(22222));
        shareHolding5.setYield(BigDecimal.valueOf(22222));
        shareHolding5.setHasPending(true);
        shareHolding5.setMarketValue(BigDecimal.valueOf(34000));
        shareHolding5.setEstdGainDollar(BigDecimal.valueOf(20));
        shareHolding5.setEstdGainPercent(BigDecimal.valueOf(5));
        shareHolding5.setHoldingKey(HoldingKey.valueOf("holding4", asset2.getAssetName()));

        ShareHoldingImpl shareHolding6 = new ShareHoldingImpl();
        shareHolding6.setAsset(asset5);
        shareHolding6.setHinType(HinType.INDIVIDUAL);
        shareHolding6.setAccruedIncome(BigDecimal.valueOf(99.90d));
        shareHolding6.setAvailableUnits(BigDecimal.valueOf(2));
        shareHolding6.setCost(BigDecimal.valueOf(0));
        shareHolding6.setUnitPrice(BigDecimal.valueOf(2222));
        shareHolding6.setUnitPriceDate(effectiveDate);
        shareHolding6.setUnits(BigDecimal.valueOf(22222));
        shareHolding6.setYield(BigDecimal.valueOf(22222));
        shareHolding6.setHasPending(true);
        shareHolding6.setMarketValue(BigDecimal.valueOf(951));
        shareHolding6.setEstdGainDollar(BigDecimal.valueOf(951));
        shareHolding6.setEstdGainPercent(BigDecimal.valueOf(0));
        shareHolding6.setHoldingKey(HoldingKey.valueOf("holding4", asset2.getAssetName()));

        shareList.add(shareHolding1);
        shareList.add(shareHolding2);
        shareList.add(shareHolding3);
        shareList.add(shareHolding4);
        shareList.add(shareHolding5);
        shareList.add(shareHolding6);

        subAccount = new ShareAccountValuationImpl(AssetType.SHARE);
        subAccount.addHoldings(shareList);

        Mockito.when(
                accountIntegrationService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(accountDetail);


    }

    @Test
    public void testBuildValuationDto_whenLSSubAccountPassed_thenShareValuationDtosCreated() {

        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        List<InvestmentValuationDto> dtoList = shareValuationAggregator.getShareValuationDtos(subAccount.getHoldings(),
                accountBalance);

        Assert.assertNotNull(dtoList);
        Assert.assertEquals(5, dtoList.size());

        ShareValuationDto dto = (ShareValuationDto) dtoList.get(0);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName1", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(false, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(1111), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(110), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(111), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(DistributionMethod.REINVEST.getDisplayName(), dto.getDividendMethod());
        Assert.assertEquals(0, dto.getChildValuations().size());

        dto = (ShareValuationDto) dtoList.get(1);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName5", dto.getInvestmentAsset().getAssetName());
        Assert.assertFalse(dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(951), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(2), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getPercentGain());
        Assert.assertEquals(null, dto.getDividendMethod());

        dto = (ShareValuationDto) dtoList.get(2);

        Assert.assertNotNull(dto);

        Assert.assertEquals("assetName4", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(true, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(68000), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(2), dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(444), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(null, dto.getDividendMethod());
        Assert.assertNotEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getPercentGain());
        Assert.assertEquals(2, dto.getChildValuations().size());

        ShareValuationDto childValuationDto = dto.getChildValuations().get(1);
        Assert.assertEquals(BigDecimal.valueOf(2222), childValuationDto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, childValuationDto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(34000), childValuationDto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.valueOf(2), childValuationDto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(222), childValuationDto.getInvestmentAsset().getAverageCost());
        Assert.assertNotEquals(BigDecimal.valueOf(5), childValuationDto.getInvestmentAsset().getPercentGain());
        Assert.assertEquals(null, childValuationDto.getDividendMethod());
        Assert.assertTrue(HinType.INDIVIDUAL == childValuationDto.getHinType());

        dto = (ShareValuationDto) dtoList.get(3);

        Assert.assertEquals("assetName3", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(false, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(222), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.TEN, dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(222), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(new BigDecimal("0.00"), dto.getInvestmentAsset().getPercentGain());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDividendMethod());

        dto = (ShareValuationDto) dtoList.get(4);

        Assert.assertEquals("assetName2", dto.getInvestmentAsset().getAssetName());
        Assert.assertEquals(false, dto.getPendingSellDown());
        Assert.assertEquals(BigDecimal.valueOf(2222), dto.getInvestmentAsset().getUnitPrice());
        Assert.assertEquals(effectiveDate, dto.getInvestmentAsset().getEffectiveDate());
        Assert.assertEquals(BigDecimal.valueOf(0), dto.getInvestmentAsset().getMarketValue());
        Assert.assertEquals(BigDecimal.TEN, dto.getInvestmentAsset().getAvailableQuantity());
        Assert.assertEquals(BigDecimal.valueOf(222), dto.getInvestmentAsset().getAverageCost());
        Assert.assertEquals(new BigDecimal("-1.00"), dto.getInvestmentAsset().getPercentGain());
        Assert.assertEquals(DistributionMethod.CASH.getDisplayName(), dto.getDividendMethod());
        Assert.assertEquals("Listed securities", dto.getCategoryName());
    }
}
