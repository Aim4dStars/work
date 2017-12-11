package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl.InterestRateImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import com.bt.nextgen.service.integration.investment.InvestmentStyle;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AssetDtoConverterTest {
    @InjectMocks
    private AssetDtoConverter assetDtoConverter;

    @Mock
    private CmsService cmsService;

    @Mock
    private InvestmentPolicyStatementIntegrationService cacheIPSIntegrationService;

    private TermDepositAssetImpl tdAsset;
    private AssetImpl mpAsset;
    private ManagedFundAssetImpl mfAsset;
    private ShareAssetImpl shAsset;
    private AssetImpl idxAsset;
    private List<Asset> assets;
    private List<Asset> emptyAssets;
    private TermDepositAssetDetailImpl tdAssetDetail;
    private Map<String, TermDepositAssetDetail> tdAssetDetails;

    @Before
    public void setup() throws Exception {
        shAsset = new ShareAssetImpl();
        shAsset.setAssetId("110707");
        shAsset.setAssetCode("TIX");
        shAsset.setAssetType(AssetType.SHARE);
        shAsset.setAssetName("360 Capital Industrial Fund AUD");
        shAsset.setPrice(new BigDecimal(2.67));
        shAsset.setIndustryType("Retailing");
        shAsset.setIndustrySector("Customer Discretionary");
        shAsset.setInvestmentHoldingLimit(new BigDecimal("22"));
        shAsset.setInvestmentHoldingLimitBuffer(new BigDecimal("27"));
        shAsset.setSuperInvestIhl(new BigDecimal("22"));
        shAsset.setSuperInvestIhlBuffer(new BigDecimal("27"));

        mfAsset = new ManagedFundAssetImpl();
        mfAsset.setAssetId("92655");
        mfAsset.setAssetCode("AMP0254AU");
        mfAsset.setAssetType(AssetType.MANAGED_FUND);
        mfAsset.setAssetClass(AssetClass.DIVERSIFIED);
        mfAsset.setAssetName("AMP Capital Investors International Bond");
        mfAsset.setPrice(new BigDecimal(.89));
        mfAsset.setStatus(AssetStatus.OPEN);
        mfAsset.setDistributionMethod("Cash Only");
        mfAsset.setRiskMeasure("High");

        mpAsset = new AssetImpl();
        mpAsset.setAssetId("28737");
        mpAsset.setAssetCode("BR0001");
        mpAsset.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAsset.setAssetName("BlackRock International");
        mpAsset.setStatus(AssetStatus.OPEN);
        mpAsset.setRiskMeasure("High to medium");
        mpAsset.setIpsInvestmentStyle(InvestmentStyle.CONSERVATIVE.getDescription());
        mpAsset.setIpsId("ipsid1");

        tdAsset = new TermDepositAssetImpl();
        tdAsset.setAssetId("65484");
        tdAsset.setAssetType(AssetType.TERM_DEPOSIT);
        tdAsset.setMaturityDate(DateTime.now());
        tdAsset.setTerm(new Term("3Y"));
        tdAsset.setAssetName("BT Term Deposit: 3 months interest payment at maturity");
        tdAsset.setPrePensionRestricted("+");

        idxAsset = new AssetImpl();
        idxAsset.setAssetId("99773");
        idxAsset.setAssetType(AssetType.INDEX);
        idxAsset.setAssetName("Consumer Price Index Australia");

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

        tdAssetDetails = new HashMap<>();
        tdAssetDetails.put(tdAssetDetail.getAssetId(), tdAssetDetail);

        assets = new ArrayList<>();
        assets.add(shAsset);
        assets.add(mfAsset);
        assets.add(mpAsset);
        assets.add(tdAsset);
        assets.add(idxAsset);

        emptyAssets = new ArrayList<>();
    }

    @Test
    public void testToAssetDto_assetsEmpty() {
        List<AssetDto> assetDtos = assetDtoConverter.toAssetDto(emptyAssets, tdAssetDetails);
        Assert.assertEquals(0, assetDtos.size());
    }

    @Test
    public void testToAssetDto_sizeMatches() {
        List<AssetDto> assetDtos = assetDtoConverter.toAssetDto(assets, tdAssetDetails);
        assertNotNull(assetDtos);
        Assert.assertEquals(5, assetDtos.size());
    }

    @Test
    public void testToAssetDto_valuesMatch_forShareAsset() {
        AssetDto assetDto = assetDtoConverter.toAssetDto(shAsset, null);
        assertNotNull(assetDto);
        Assert.assertSame(ShareAssetDto.class, assetDto.getClass());
        ShareAssetDto shAssetDto = (ShareAssetDto) assetDto;
        Assert.assertEquals(shAsset.getAssetId(), shAssetDto.getKey());
        Assert.assertEquals(shAsset.getAssetCode(), shAssetDto.getAssetCode());
        Assert.assertEquals(shAsset.getAssetName(), shAssetDto.getAssetName());
        Assert.assertEquals(shAsset.getPrice(), shAssetDto.getPrice());
        Assert.assertEquals(shAsset.getIndustryType(), shAssetDto.getIndustryType());
        Assert.assertEquals(shAsset.getIndustrySector(), shAssetDto.getIndustrySector());
        Assert.assertEquals(shAsset.getInvestmentHoldingLimit(), shAssetDto.getInvestmentHoldingLimit());
        Assert.assertEquals(shAsset.getInvestmentHoldingLimitBuffer(), shAssetDto.getInvestmentHoldingLimitBuffer());
        Assert.assertEquals(shAsset.getSuperInvestIhl(), shAssetDto.getSuperInvestIhl());
        Assert.assertEquals(shAsset.getSuperInvestIhlBuffer(), shAssetDto.getSuperInvestIhlBuffer());
    }

    @Test
    public void testToAssetDto_valuesMatch_forManagedFundAsset() {
        Map<String, BigDecimal> allocations = new HashMap<>();
        allocations.put(AssetClass.ALTERNATIVES.getDescription(), BigDecimal.TEN);
        allocations.put(AssetClass.CASH.getDescription(), BigDecimal.TEN);

        AssetDto assetDto = assetDtoConverter.toAssetDto(mfAsset, null, allocations);
        assertNotNull(assetDto);
        Assert.assertSame(ManagedFundAssetDto.class, assetDto.getClass());
        ManagedFundAssetDto mfAssetDto = (ManagedFundAssetDto) assetDto;
        Assert.assertEquals(mfAsset.getAssetId(), mfAssetDto.getKey());
        Assert.assertEquals(mfAsset.getAssetCode(), mfAssetDto.getAssetCode());
        Assert.assertEquals(mfAsset.getAssetName(), mfAssetDto.getAssetName());
        Assert.assertEquals(mfAsset.getPrice(), mfAssetDto.getPrice());
        Assert.assertEquals(mfAsset.getStatus().getDisplayName(), mfAssetDto.getStatus());
        Assert.assertEquals(mfAsset.getDistributionMethod(), mfAssetDto.getDistributionMethod());
        Assert.assertEquals(mfAsset.getPriceFrequency(), mfAssetDto.getPriceFrequency());
        Assert.assertEquals(mfAssetDto.getAllocations().size(), 2);
    }

    @Test
    public void testToAssetDto_valuesMatch_forManagedPortfolioAsset() {
        InvestmentPolicyStatementInterface ipsDetail = getIpsDetail("ipsid1", "apir1", "9500");
        Mockito.when(cacheIPSIntegrationService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class)))
                .thenReturn(ipsDetail);
        AssetDto assetDto = assetDtoConverter.toAssetDto(mpAsset, null);
        assertNotNull(assetDto);
        Assert.assertSame(ManagedPortfolioAssetDto.class, assetDto.getClass());
        ManagedPortfolioAssetDto mpAssetDto = (ManagedPortfolioAssetDto) assetDto;
        Assert.assertEquals(mpAsset.getAssetId(), mpAssetDto.getKey());
        Assert.assertEquals(mpAsset.getAssetCode(), mpAssetDto.getAssetCode());
        Assert.assertEquals(mpAsset.getAssetName(), mpAssetDto.getAssetName());
        Assert.assertEquals(mpAsset.getStatus().getDisplayName(), mpAssetDto.getStatus());
        Assert.assertEquals(mpAsset.getRiskMeasure(), mpAssetDto.getRiskMeasure());
        Assert.assertEquals(mpAsset.getIpsInvestmentStyle(), mpAssetDto.getInvestmentStyle());
        Assert.assertEquals(mpAssetDto.getInvestmentStyle(), InvestmentStyle.CONSERVATIVE.getDescription());
        assertThat(mpAssetDto.getTaxAssetDomicile(), is(true));
    }

    @Test
    public void testToAssetDto_valuesMatch_forTermDepositAsset() {
        AssetDto assetDto = assetDtoConverter.toAssetDto(tdAsset, tdAssetDetail);
        assertNotNull(assetDto);
        Assert.assertSame(TermDepositAssetDto.class, assetDto.getClass());
        TermDepositAssetDto tdAssetDto = (TermDepositAssetDto) assetDto;
        Assert.assertEquals(tdAsset.getAssetId(), tdAssetDto.getKey());
        Assert.assertEquals(tdAsset.getMaturityDate(), tdAssetDto.getMaturityDate());
        Assert.assertEquals(tdAsset.isPrePensionRestricted(), tdAssetDto.isPrePensionRestricted());

        Assert.assertEquals(tdAssetDetail.getIssuer(), tdAssetDto.getAssetName());
        Assert.assertEquals(tdAssetDetail.getPaymentFrequency().getDisplayName(), tdAssetDto.getInterestPaymentFrequency());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getLowerLimit(), tdAssetDto.getMinInvest());
        Assert.assertEquals(tdAssetDetail.getInterestRates().last().getUpperLimit(), tdAssetDto.getMaxInvest());
        Assert.assertEquals(tdAssetDetail.getTerm().getMonths(), tdAssetDto.getTerm());

        Assert.assertEquals(tdAssetDetail.getInterestRates().size(), tdAssetDto.getInterestBands().size());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getLowerLimit(),
                tdAssetDto.getInterestBands().get(0).getLowerLimit());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getUpperLimit(),
                tdAssetDto.getInterestBands().get(0).getUpperLimit());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getRateAsPercentage(),
                tdAssetDto.getInterestBands().get(0).getRate());
    }

    @Test
    public void testToAssetDto_valuesMatch_forIndexAsset() {
        AssetDto assetDto = assetDtoConverter.toAssetDto(idxAsset, null);
        assertNotNull(assetDto);
        Assert.assertSame(AssetDto.class, assetDto.getClass());
        Assert.assertEquals(idxAsset.getAssetId(), assetDto.getKey());
        Assert.assertEquals(idxAsset.getAssetName(), assetDto.getAssetName());
    }

    @Test
    public void testToAssetDto_valuesMatchAllowRateless_forTermDepositAsset() {
        Map<String, Asset> assets = new HashMap<>();
        assets.put(tdAsset.getAssetId(), tdAsset);
        Map<String, TermDepositAssetDetail> tdAssetDetails = new HashMap<>();
        tdAssetDetails.put(tdAssetDetail.getAssetId(), tdAssetDetail);

        AssetDto assetDto = assetDtoConverter.toAssetDto(assets, tdAssetDetails, true).get(tdAsset.getAssetId());
        assertNotNull(assetDto);
        Assert.assertSame(TermDepositAssetDto.class, assetDto.getClass());
        TermDepositAssetDto tdAssetDto = (TermDepositAssetDto) assetDto;
        Assert.assertEquals(tdAsset.getAssetId(), tdAssetDto.getKey());
        Assert.assertEquals(tdAsset.getMaturityDate(), tdAssetDto.getMaturityDate());
        Assert.assertEquals(tdAssetDetail.getIssuer(), tdAssetDto.getAssetName());
        Assert.assertEquals(tdAssetDetail.getPaymentFrequency().getDisplayName(), tdAssetDto.getInterestPaymentFrequency());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getLowerLimit(), tdAssetDto.getMinInvest());
        Assert.assertEquals(tdAssetDetail.getInterestRates().last().getUpperLimit(), tdAssetDto.getMaxInvest());
        Assert.assertEquals(tdAssetDetail.getTerm().getMonths(), tdAssetDto.getTerm());

        Assert.assertEquals(tdAssetDetail.getInterestRates().size(), tdAssetDto.getInterestBands().size());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getLowerLimit(),
                tdAssetDto.getInterestBands().get(0).getLowerLimit());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getUpperLimit(),
                tdAssetDto.getInterestBands().get(0).getUpperLimit());
        Assert.assertEquals(tdAssetDetail.getInterestRates().first().getRateAsPercentage(),
                tdAssetDto.getInterestBands().get(0).getRate());
    }

    @Test
    public void testToAssetDto_valuesMatchAllowRateless_forNullTermDepositAsset() {
        Map<String, Asset> assets = new HashMap<>();
        assets.put(tdAsset.getAssetId(), tdAsset);
        Map<String, TermDepositAssetDetail> tdAssetDetails = new HashMap<>();

        AssetDto assetDto = assetDtoConverter.toAssetDto(assets, tdAssetDetails, true).get(tdAsset.getAssetId());

        assertNotNull(assetDto);
        Assert.assertSame(TermDepositAssetDto.class, assetDto.getClass());
        TermDepositAssetDto tdAssetDto = (TermDepositAssetDto) assetDto;
        Assert.assertEquals(tdAsset.getAssetId(), tdAssetDto.getKey());
        Assert.assertEquals(tdAsset.getMaturityDate(), tdAssetDto.getMaturityDate());
        Assert.assertEquals(tdAsset.getBrand(), tdAssetDto.getAssetName());
        Assert.assertNull(null);
        Assert.assertNull(tdAssetDto.getMinInvest());
        Assert.assertNull(tdAssetDto.getMaxInvest());
        Assert.assertEquals(tdAsset.getTerm().getMonths(), tdAssetDto.getTerm());
        Assert.assertEquals(0, tdAssetDto.getInterestBands().size());
    }

    InvestmentPolicyStatementInterface getIpsDetail(final String ipsId, final String apirCode, final String investmentStyleId) {
        InvestmentPolicyStatementInterface ips = Mockito.mock(InvestmentPolicyStatementInterface.class);
        Mockito.when(ips.getInvestmentStyleId()).thenReturn(investmentStyleId);
        Mockito.when(ips.getApirCode()).thenReturn(apirCode);
        Mockito.when(ips.getIpsKey()).thenReturn(IpsKey.valueOf(ipsId));
        Mockito.when(ips.getMinInitInvstAmt()).thenReturn(new BigDecimal(500));
        Mockito.when(ips.getTaxAssetDomicile()).thenReturn(true);

        return ips;
    }
}
