package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDtoV2;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositAssetBuilderV2Test {

    @InjectMocks
    private TermDepositAssetBuilderV2 termDepositAssetBuilder;

    @Mock
    private CmsService cmsService;

    private TermDepositAssetImpl tdAsset;
    private AssetImpl mpAsset;
    private ManagedFundAssetImpl mfAsset;
    private TermDepositAssetDetailImpl tdAssetDetail;
    private Map<String, TermDepositAssetDetail> termDepositAssetDetails;
    private List<TermDepositInterestRate> termDepositInterestRates;
    private final Map<String, Asset> termDepositassetMap = new HashMap<String, Asset>();

    @Before
    public void setUp() throws Exception {
        List<DistributionMethod> availableMethods = new ArrayList<>();
        availableMethods.add(DistributionMethod.CASH);
        availableMethods.add(DistributionMethod.REINVEST);

        mfAsset = new ManagedFundAssetImpl();
        mfAsset.setAssetId("92655");
        mfAsset.setAssetCode("AMP0254AU");
        mfAsset.setAssetType(AssetType.MANAGED_FUND);
        mfAsset.setAssetName("AMP Capital Investors International Bond");
        mfAsset.setPrice(new BigDecimal(.89));
        mfAsset.setStatus(AssetStatus.OPEN);
        mfAsset.setDistributionMethod("Cash Only");

        mpAsset = new AssetImpl();
        mpAsset.setAssetId("28737");
        mpAsset.setAssetCode("BR0001");
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset.setAssetName("BlackRock International");
        mpAsset.setStatus(AssetStatus.OPEN);

        tdAsset = new TermDepositAssetImpl();
        tdAsset.setAssetId("65484");
        tdAsset.setAssetType(AssetType.TERM_DEPOSIT);
        tdAsset.setMaturityDate(DateTime.now());
        tdAsset.setTerm(new Term("3Y"));
        tdAsset.setAssetName("BT Term Deposit: 3 months interest payment at maturity");
        tdAsset.setIssuerName("St. George");
        tdAsset.setIntrRate(new BigDecimal(0.15));

        termDepositInterestRates = new ArrayList<>();
        TermDepositInterestRate termDepositInterestRate = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65484")).withDealerGroupKey(BrokerKey.valueOf("99971"))
                .withRate(new BigDecimal(0.15)).withAccountStructureType(AccountStructureType.Individual).withUpperLimit(new BigDecimal(20000)).withLowerLimit(new BigDecimal(0))
                .withTerm(new Term("3Y")).withPaymentFrequency(PaymentFrequency.ANNUALLY).buildTermDepositRate();
        termDepositInterestRates.add(termDepositInterestRate);

        termDepositassetMap.put(tdAsset.getAssetId(), tdAsset);
        termDepositassetMap.put(mfAsset.getAssetId(), mfAsset);
        termDepositassetMap.put(mpAsset.getAssetId(), mpAsset);
    }

    @Test
    public final void testBuildTradeAssets() {

        List<TradeAssetDto> tradeTermDepositAssetDtoList = termDepositAssetBuilder.buildTradeAssets(termDepositInterestRates,
                termDepositassetMap);

        for (TradeAssetDto tradeAssetDto : tradeTermDepositAssetDtoList) {
            Assert.assertNotNull(tradeAssetDto);
            // Assert.assertEquals(tradeAssetDto.getKey(), "");
            Assert.assertEquals(tradeAssetDto.getAsset().getAssetId(), "65484");
            Assert.assertEquals(tradeAssetDto.getAssetTypeDescription(), "Term deposit");
            Assert.assertEquals(tradeAssetDto.getAvailableQuantity(), null);
            Assert.assertEquals(tradeAssetDto.getBalance(), null);
            Assert.assertEquals(tradeAssetDto.getBuyable(), true);
            Assert.assertEquals(tradeAssetDto.getSellable(), false);
            if (tradeAssetDto instanceof TermDepositTradeAssetDtoV2) {
                Assert.assertEquals(((TermDepositTradeAssetDtoV2) tradeAssetDto).getDescription(), "yearly");
                Assert.assertEquals(((TermDepositTradeAssetDtoV2) tradeAssetDto).getTermDisplay(), "36 months");
                Assert.assertNotNull(((TermDepositTradeAssetDtoV2) tradeAssetDto).getIndicativeRate());
                AssetDto assetDto = ((TermDepositTradeAssetDtoV2) tradeAssetDto).getAsset();
                Assert.assertTrue(assetDto instanceof TermDepositAssetDtoV2);
                Assert.assertNotNull(((TermDepositAssetDtoV2) assetDto).getIntrRate());
            }
            Assert.assertEquals(tradeAssetDto.getAsset().getAssetName(), "St. George");

        }

    }

    @Test
    public final void testBuildTradeAssetsAboveMaxAmount() {
        termDepositInterestRates = new ArrayList<>();
        TermDepositInterestRate termDepositInterestRate = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder()
                .withAssetKey(AssetKey.valueOf("65484"))
                .withDealerGroupKey(BrokerKey.valueOf("99971"))
                .withRate(new BigDecimal(0.15))
                .withAccountStructureType(AccountStructureType.Individual)
                .withUpperLimit(new BigDecimal(5000))
                .withLowerLimit(new BigDecimal(0))
                .withTerm(new Term("3Y"))
                .withPaymentFrequency(PaymentFrequency.ANNUALLY)
                .withMinInvestmentAmount(BigDecimal.valueOf(5000))
                .withMaxInvestmentAmount(BigDecimal.valueOf(25000))
                .buildTermDepositRate();

        termDepositInterestRates.add(termDepositInterestRate);

        List<TradeAssetDto> tradeTermDepositAssetDtoList = termDepositAssetBuilder.buildTradeAssets(termDepositInterestRates,
                termDepositassetMap);

        for (TradeAssetDto tradeAssetDto : tradeTermDepositAssetDtoList) {
            Assert.assertNotNull(tradeAssetDto);
            // Assert.assertEquals(tradeAssetDto.getKey(), "");
            Assert.assertEquals(tradeAssetDto.getAsset().getAssetId(), "65484");
            Assert.assertEquals(tradeAssetDto.getAssetTypeDescription(), "Term deposit");
            Assert.assertEquals(tradeAssetDto.getAvailableQuantity(), null);
            Assert.assertEquals(tradeAssetDto.getBalance(), null);
            Assert.assertEquals(tradeAssetDto.getBuyable(), true);
            Assert.assertEquals(tradeAssetDto.getSellable(), false);
            if (tradeAssetDto instanceof TermDepositTradeAssetDtoV2) {
                Assert.assertEquals(((TermDepositTradeAssetDtoV2) tradeAssetDto).getDescription(), "yearly");
                // Assert.assertEquals(0.0, ((TermDepositTradeAssetDto)
                // tradeAssetDto).getIndicativeRate().doubleValue(), 0.01);
                Assert.assertEquals(((TermDepositTradeAssetDtoV2) tradeAssetDto).getTermDisplay(), "36 months");
                Assert.assertNull(((TermDepositTradeAssetDtoV2) tradeAssetDto).getIndicativeRate());
            }
            Assert.assertEquals(((TermDepositAssetDtoV2) tradeAssetDto.getAsset()).getMinInvest(), BigDecimal.valueOf(5000));
            Assert.assertEquals(((TermDepositAssetDtoV2) tradeAssetDto.getAsset()).getMaxInvest(), BigDecimal.valueOf(25000));


            TermDepositInterestRate tdRateWithNoFreq = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder()
                    .withAssetKey(AssetKey.valueOf("65484"))
                    .withDealerGroupKey(BrokerKey.valueOf("99971"))
                    .withRate(new BigDecimal(0.15))
                    .withAccountStructureType(AccountStructureType.Individual)
                    .withUpperLimit(new BigDecimal(5000))
                    .withLowerLimit(new BigDecimal(0))
                    .withTerm(new Term("3Y"))
                    .withMinInvestmentAmount(BigDecimal.valueOf(5000))
                    .withMaxInvestmentAmount(BigDecimal.valueOf(25000))
                    .buildTermDepositRate();

            tradeTermDepositAssetDtoList = termDepositAssetBuilder.buildTradeAssets(Collections.singletonList(tdRateWithNoFreq), termDepositassetMap);
                Assert.assertNull(((TermDepositAssetDtoV2) tradeTermDepositAssetDtoList.get(0).getAsset()).getInterestPaymentFrequency());

        }

    }
}
