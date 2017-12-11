package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedFundAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ManagedPortfolioAssetImpl;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetDetailImpl.InterestRateImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.bt.nextgen.service.avaloq.asset.TermDepositInterestRateUtil;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AssetDtoConverterV2Test {
    @InjectMocks
    private AssetDtoConverterV2 assetDtoConverter;

    @Mock
    private CmsService cmsService;

    @Mock
    private InvestmentPolicyStatementIntegrationService cacheIPSIntegrationService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public static final java.lang.String TD_BRAND_PREFIX = "td.brand.";


    private TermDepositAssetImpl tdAsset;
    private AssetImpl mpAsset;
    private ManagedFundAssetImpl mfAsset;
    private ShareAssetImpl shAsset;
    private AssetImpl idxAsset;
    private List<Asset> assets;
    private List<Asset> emptyAssets;
    private TermDepositAssetDetailImpl tdAssetDetail;
    private List<TermDepositInterestRate> termDepositInterestRates;
    private Map<String,Asset> assetMap;

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
        tdAsset.setAssetName("St George Term Deposit: 3 years interest payment at maturity");
        tdAsset.setPrePensionRestricted("+");

        idxAsset = new AssetImpl();
        idxAsset.setAssetId("99773");
        idxAsset.setAssetType(AssetType.INDEX);
        idxAsset.setAssetName("Consumer Price Index Australia");

        termDepositInterestRates= new ArrayList<>();
        TermDepositInterestRate termDepositInterestRate01 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65484")).withDealerGroupKey(BrokerKey.valueOf("99971"))
                .withRate(new BigDecimal(0.15)).withAccountStructureType(AccountStructureType.Individual).withPaymentFrequency(PaymentFrequency.ANNUALLY).withLowerLimit(new BigDecimal(0)).withUpperLimit(new BigDecimal(5000))
                .withTerm(new Term("3Y")).withIssuerId("80000051").buildTermDepositRate();
        TermDepositInterestRate termDepositInterestRate02 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65484")).withDealerGroupKey(BrokerKey.valueOf("99971"))
                .withRate(new BigDecimal(0.15)).withAccountStructureType(AccountStructureType.Individual).withPaymentFrequency(PaymentFrequency.ANNUALLY).withLowerLimit(new BigDecimal(5000)).withUpperLimit(new BigDecimal(10000))
                .withTerm(new Term("3Y")).withIssuerId("80000051").buildTermDepositRate();
        termDepositInterestRates.add(termDepositInterestRate01);
        termDepositInterestRates.add(termDepositInterestRate02);

        assets = new ArrayList<>();
        assets.add(shAsset);
        assets.add(mfAsset);
        assets.add(mpAsset);
        assets.add(tdAsset);
        assets.add(idxAsset);

        emptyAssets = new ArrayList<>();
        assetMap = new HashMap<>();
        assetMap.put(shAsset.getAssetId(),shAsset);
        assetMap.put(mfAsset.getAssetId(),mfAsset);
        assetMap.put(mpAsset.getAssetId(),mpAsset);
        assetMap.put(tdAsset.getAssetId(),tdAsset);
        assetMap.put(idxAsset.getAssetId(),idxAsset);

        when(cmsService.getContent(TD_BRAND_PREFIX.concat("80000051"))).thenReturn("St George");
        when(cmsService.getContent(TD_BRAND_PREFIX.concat("80000052"))).thenReturn("BT");

    }

    @Test
    public void testToAssetDto_assetsEmpty() {
        List<AssetDto> assetDtos = assetDtoConverter.toAssetDto(emptyAssets, termDepositInterestRates);
        Assert.assertEquals(0, assetDtos.size());
    }

    @Test
    public void testToAssetDto_sizeMatches() {
        List<AssetDto> assetDtos = assetDtoConverter.toAssetDto(assets, termDepositInterestRates);
        assertNotNull(assetDtos);
        Assert.assertEquals(5, assetDtos.size());
    }

    @Test
    public void testToAssetDto_InvalidAssets() {
        ManagedPortfolioAssetImpl mpAssetInvalid01 = new ManagedPortfolioAssetImpl();
        mpAssetInvalid01.setAssetId("28737");
        mpAssetInvalid01.setAssetCode("BR0001");
        mpAssetInvalid01.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAssetInvalid01.setAssetName(null);
        mpAssetInvalid01.setStatus(AssetStatus.OPEN);
        mpAssetInvalid01.setRiskMeasure("High to medium");
        mpAssetInvalid01.setIpsInvestmentStyle(InvestmentStyle.CONSERVATIVE.getDescription());
        mpAssetInvalid01.setIpsId("ipsid1");

        ManagedPortfolioAssetImpl mpAssetInvalid02 = new ManagedPortfolioAssetImpl();
        mpAssetInvalid02.setAssetId("28737");
        mpAssetInvalid02.setAssetCode("BR0001");
        mpAssetInvalid02.setAssetType(AssetType.MANAGED_PORTFOLIO);
        mpAssetInvalid02.setAssetName(null);
        mpAssetInvalid02.setStatus(AssetStatus.OPEN);
        mpAssetInvalid02.setRiskMeasure("High to medium");
        mpAssetInvalid02.setIpsInvestmentStyle(InvestmentStyle.CONSERVATIVE.getDescription());
        mpAssetInvalid02.setIpsId("ipsid1");


        List<Asset> inValidAssets = new ArrayList<>();
        inValidAssets.add(mpAssetInvalid02);
        inValidAssets.add(mpAssetInvalid02);


        List<AssetDto> assetDtoList = assetDtoConverter.toAssetDto(inValidAssets, null);
        assertNotNull(assetDtoList);

    }

    @Test
    public void testToAssetDtoMap_sizeMatches() {
        Map<String,Asset> newAssetMap = new HashMap<>();
        newAssetMap.putAll(assetMap);
        TermDepositAssetImpl tdAssetNew = new TermDepositAssetImpl();
        tdAssetNew.setAssetId("65485");
        tdAssetNew.setAssetType(AssetType.TERM_DEPOSIT);
        tdAssetNew.setMaturityDate(DateTime.now());
        tdAssetNew.setTerm(new Term("5Y"));
        tdAssetNew.setAssetName("St George Term Deposit: 5 years interest payment at maturity");
        tdAssetNew.setPrePensionRestricted("+");
        newAssetMap.put(tdAssetNew.getAssetId(),tdAssetNew);


        List<TermDepositInterestRate> newTermDepositInterestRates = new ArrayList<>();
        newTermDepositInterestRates.addAll(termDepositInterestRates);
        TermDepositInterestRate termDepositInterestRate03 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65485")).withDealerGroupKey(BrokerKey.valueOf("99971"))
                .withRate(new BigDecimal(0.10)).withAccountStructureType(AccountStructureType.Individual).withPaymentFrequency(PaymentFrequency.ANNUALLY).withLowerLimit(new BigDecimal(0)).withUpperLimit(new BigDecimal(5000))
                .withTerm(new Term("5Y")).withIssuerId("80000051").buildTermDepositRate();
        TermDepositInterestRate termDepositInterestRate04 = new TermDepositInterestRateImpl.TermDepositInterestRateBuilder().withAssetKey(AssetKey.valueOf("65485")).withDealerGroupKey(BrokerKey.valueOf("99971"))
                .withRate(new BigDecimal(0.10)).withAccountStructureType(AccountStructureType.Individual).withPaymentFrequency(PaymentFrequency.ANNUALLY).withLowerLimit(new BigDecimal(5000)).withUpperLimit(new BigDecimal(10000))
                .withTerm(new Term("5Y")).withIssuerId("80000051").buildTermDepositRate();
        newTermDepositInterestRates.add(termDepositInterestRate03);
        newTermDepositInterestRates.add(termDepositInterestRate04);


        Map<String,AssetDto> assetDtos = assetDtoConverter.toAssetDto(newAssetMap, newTermDepositInterestRates);
        assertNotNull(assetDtos);
        Assert.assertEquals(6, assetDtos.size());
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
        when(cacheIPSIntegrationService.getIPSDetail(any(IpsIdentifier.class), any(ServiceErrors.class)))
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
        SortedSet<TermDepositInterestRate> termDepositInterestRateSet = new TreeSet<>();
        termDepositInterestRateSet.addAll(termDepositInterestRates);

        AssetDto assetDto = assetDtoConverter.toAssetDto(tdAsset, termDepositInterestRateSet);
        assertNotNull(assetDto);
        Assert.assertSame(TermDepositAssetDtoV2.class, assetDto.getClass());
        TermDepositAssetDtoV2 tdAssetDto = (TermDepositAssetDtoV2) assetDto;
        Assert.assertEquals(tdAsset.getAssetId(), tdAssetDto.getKey());
        Assert.assertEquals(tdAsset.getMaturityDate(), tdAssetDto.getMaturityDate());
        Assert.assertEquals(tdAsset.isPrePensionRestricted(), tdAssetDto.isPrePensionRestricted());

        Assert.assertEquals(termDepositInterestRates.get(0).getPaymentFrequency().getDisplayName(), tdAssetDto.getInterestPaymentFrequency());
        Assert.assertEquals(termDepositInterestRates.get(0).getLowerLimit(), tdAssetDto.getMinInvest());
        Assert.assertEquals(termDepositInterestRates.get(1).getUpperLimit(), tdAssetDto.getMaxInvest());
        Assert.assertEquals(termDepositInterestRates.get(0).getTerm().getMonths(), tdAssetDto.getTerm());

        Assert.assertEquals(2, tdAssetDto.getInterestBands().size());
        Assert.assertEquals(termDepositInterestRates.get(0).getLowerLimit(),
                tdAssetDto.getInterestBands().get(0).getLowerLimit());
        Assert.assertEquals(termDepositInterestRates.get(0).getUpperLimit(),
                tdAssetDto.getInterestBands().get(0).getUpperLimit());
        Assert.assertEquals(termDepositInterestRates.get(0).getRateAsPercentage(),
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


        AssetDto assetDto = assetDtoConverter.toAssetDto(assets, termDepositInterestRates, true).get(tdAsset.getAssetId());
        assertNotNull(assetDto);
        Assert.assertSame(TermDepositAssetDtoV2.class, assetDto.getClass());
        TermDepositAssetDtoV2 tdAssetDto = (TermDepositAssetDtoV2) assetDto;
        Assert.assertEquals(tdAsset.getAssetId(), tdAssetDto.getKey());
        Assert.assertEquals(tdAsset.getMaturityDate(), tdAssetDto.getMaturityDate());
        Assert.assertEquals(tdAsset.isPrePensionRestricted(), tdAssetDto.isPrePensionRestricted());

        Assert.assertEquals(termDepositInterestRates.get(0).getPaymentFrequency().getDisplayName(), tdAssetDto.getInterestPaymentFrequency());
        Assert.assertEquals(termDepositInterestRates.get(0).getLowerLimit(), tdAssetDto.getMinInvest());
        Assert.assertEquals(termDepositInterestRates.get(1).getUpperLimit(), tdAssetDto.getMaxInvest());
        Assert.assertEquals(termDepositInterestRates.get(0).getTerm().getMonths(), tdAssetDto.getTerm());

        Assert.assertEquals(2, tdAssetDto.getInterestBands().size());
        Assert.assertEquals(termDepositInterestRates.get(0).getLowerLimit(),
                tdAssetDto.getInterestBands().get(0).getLowerLimit());
        Assert.assertEquals(termDepositInterestRates.get(0).getUpperLimit(),
                tdAssetDto.getInterestBands().get(0).getUpperLimit());
        Assert.assertEquals(termDepositInterestRates.get(0).getRateAsPercentage(),
                tdAssetDto.getInterestBands().get(0).getRate());
    }


    @Test
    public void testToAssetDto_forEmptyTDs() {
        Map<String, Asset> assets = new HashMap<>();
        assets.put(tdAsset.getAssetId(), tdAsset);
        Assert.assertNull(assetDtoConverter.toAssetDto(assets, null, false).get(tdAsset.getAssetId()));
    }


    @Test
    public void testToAssetDto_valuesMatchAllowRateless_forNullTermDepositAsset() {
        Map<String, Asset> assets = new HashMap<>();
        assets.put(tdAsset.getAssetId(), tdAsset);
        List<TermDepositInterestRate> termDepositInterestRatesBlank = new ArrayList<>();

        AssetDto assetDto = assetDtoConverter.toAssetDto(assets, termDepositInterestRatesBlank, true).get(tdAsset.getAssetId());

        assertNotNull(assetDto);
        Assert.assertSame(TermDepositAssetDtoV2.class, assetDto.getClass());
        TermDepositAssetDtoV2 tdAssetDto = (TermDepositAssetDtoV2) assetDto;
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
        when(ips.getInvestmentStyleId()).thenReturn(investmentStyleId);
        when(ips.getApirCode()).thenReturn(apirCode);
        when(ips.getIpsKey()).thenReturn(IpsKey.valueOf(ipsId));
        when(ips.getMinInitInvstAmt()).thenReturn(new BigDecimal(500));
        when(ips.getTaxAssetDomicile()).thenReturn(true);

        return ips;
    }
}
