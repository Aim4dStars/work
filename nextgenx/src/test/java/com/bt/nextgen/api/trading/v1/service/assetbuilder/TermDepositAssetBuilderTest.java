package com.bt.nextgen.api.trading.v1.service.assetbuilder;

import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl.InterestRateImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@RunWith(MockitoJUnitRunner.class)
public class TermDepositAssetBuilderTest {

    @InjectMocks
    private TermDepositAssetBuilder termDepositAssetBuilder;

    @Mock
    private CmsService cmsService;

    private TermDepositAssetImpl tdAsset;
    private AssetImpl mpAsset;
    private ManagedFundAssetImpl mfAsset;
    private TermDepositAssetDetailImpl tdAssetDetail;
    private Map<String, TermDepositAssetDetail> termDepositAssetDetails;
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

        tdAssetDetail = new TermDepositAssetDetailImpl();
        tdAssetDetail.setAssetId("65484");
        tdAssetDetail.setIssuer("BT");
        tdAssetDetail.setPaymentFrequency(PaymentFrequency.ANNUALLY);
        tdAssetDetail.setTerm(new Term("3Y"));

        TreeSet<InterestRate> interestRates = new TreeSet<>();
        InterestRateImpl interestRate = tdAssetDetail.new InterestRateImpl();
        interestRate.setIrcId("12345");
        interestRate.setLowerLimit(new BigDecimal("5000"));
        interestRate.setUpperLimit(new BigDecimal("500000"));
        interestRate.setRate(new BigDecimal("0.045"));
        interestRates.add(interestRate);
        interestRate = tdAssetDetail.new InterestRateImpl();
        interestRate.setIrcId("45678");
        interestRate.setLowerLimit(new BigDecimal("500000"));
        interestRate.setUpperLimit(new BigDecimal("50000000"));
        interestRate.setRate(new BigDecimal("0.047"));
        tdAssetDetail.setInterestRates(interestRates);

        termDepositAssetDetails = new HashMap<>();
        termDepositAssetDetails.put(tdAssetDetail.getAssetId(), tdAssetDetail);

        termDepositassetMap.put(tdAsset.getAssetId(), tdAsset);
        termDepositassetMap.put(mfAsset.getAssetId(), mfAsset);
        termDepositassetMap.put(mpAsset.getAssetId(), mpAsset);
    }

    @Test
    public final void testBuildTradeAssets() {

        List<TradeAssetDto> tradeTermDepositAssetDtoList = termDepositAssetBuilder.buildTradeAssets(termDepositAssetDetails,
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
            if (tradeAssetDto instanceof TermDepositTradeAssetDto) {
                Assert.assertEquals(((TermDepositTradeAssetDto) tradeAssetDto).getDescription(), "yearly");
                // Assert.assertEquals(0.0, ((TermDepositTradeAssetDto)
                // tradeAssetDto).getIndicativeRate().doubleValue(), 0.01);
                Assert.assertEquals(((TermDepositTradeAssetDto) tradeAssetDto).getTermDisplay(), "36 months");
            }
            Assert.assertEquals(tradeAssetDto.getAsset().getAssetName(), "St. George");
        }

    }
}
