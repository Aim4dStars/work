package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.ManagedFundAssetBuilder;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.ManagedPortfolioAssetBuilder;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.ShareAssetBuilder;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.TermDepositAssetBuilder;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositAccountValuationImpl;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@RunWith(MockitoJUnitRunner.class)
public class TradeAssetDtoConverterTest {

    @InjectMocks
    private TradeAssetDtoConverter tradeAssetDtoConverter;

    @Mock
    private ManagedFundAssetBuilder managedFundAssetBuilder;
    @Mock
    private ManagedPortfolioAssetBuilder managedPortfolioAssetBuilder;
    @Mock
    private TermDepositAssetBuilder termDepositAssetBuilder;
    @Mock
    private ShareAssetBuilder shareAssetBuilder;
    @Mock
    private InvestmentPolicyStatementIntegrationService cacheIPSIntegrationService;

    private AssetDto managedFundAssetDto;
    private AssetDto managedPortfolioAssetDto;
    private AssetDto shareAssetDto;
    private AssetDto indexAssetDto;
    private SortedMap<String, Asset> availableAssets;
    private Map<String, TermDepositAssetDetail> termDepositAssetDetails;
    private Map<String, Asset> termDepositassetMap;
    private WrapAccountValuation valuation;
    private Map<String, Asset> filteredAssets;
    private Map<String, List<DistributionMethod>> assetDistributionMethods;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Asset managedFundAsset = Mockito.mock(Asset.class);
        Mockito.when(managedFundAsset.getAssetId()).thenReturn("managedFundAssetId");
        Mockito.when(managedFundAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(managedFundAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        managedFundAssetDto = Mockito.mock(AssetDto.class);
        Mockito.when(managedFundAssetDto.getAssetId()).thenReturn("managedFundAssetId");
        Mockito.when(managedFundAssetDto.getAssetName()).thenReturn("managed fund asset");

        Asset managedPortfolioAsset = Mockito.mock(Asset.class);
        Mockito.when(managedPortfolioAsset.getAssetId()).thenReturn("managedPortfolioAssetId");
        Mockito.when(managedPortfolioAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(managedPortfolioAsset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);

        managedPortfolioAssetDto = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioAssetDto.getAssetId()).thenReturn("managedPortfolioAssetId");
        Mockito.when(managedPortfolioAssetDto.getAssetName()).thenReturn("managed portfolio asset");

        Asset shareAsset = Mockito.mock(Asset.class);
        Mockito.when(shareAsset.getAssetId()).thenReturn("shareAssetId");
        Mockito.when(shareAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(shareAsset.getAssetType()).thenReturn(AssetType.SHARE);

        shareAssetDto = Mockito.mock(AssetDto.class);
        Mockito.when(shareAssetDto.getAssetId()).thenReturn("shareAssetId");
        Mockito.when(shareAssetDto.getAssetName()).thenReturn("share asset");

        Asset indexAsset = Mockito.mock(Asset.class);
        Mockito.when(indexAsset.getAssetId()).thenReturn("indexAssetId");
        Mockito.when(indexAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(indexAsset.getAssetType()).thenReturn(AssetType.INDEX);

        indexAssetDto = Mockito.mock(AssetDto.class);
        Mockito.when(indexAssetDto.getAssetId()).thenReturn("indexAssetId");
        Mockito.when(indexAssetDto.getAssetName()).thenReturn("index asset");

        availableAssets = new TreeMap<>();
        availableAssets.put(managedFundAsset.getAssetId(), managedFundAsset);
        availableAssets.put(managedPortfolioAsset.getAssetId(), managedPortfolioAsset);
        availableAssets.put(shareAsset.getAssetId(), shareAsset);
        availableAssets.put(indexAsset.getAssetId(), indexAsset);

        TermDepositAssetDetail termDepositAssetDetail = Mockito.mock(TermDepositAssetDetail.class);
        Mockito.when(termDepositAssetDetail.getAssetId()).thenReturn("termDepositAssetDetail");
        termDepositAssetDetails = new HashMap<String, TermDepositAssetDetail>();
        termDepositAssetDetails.put(termDepositAssetDetail.getAssetId(), termDepositAssetDetail);

        termDepositassetMap = new HashMap<>();
        List<SubAccountValuation> subAccountValuations = new ArrayList<>();

        SubAccountValuation managedFundAccountValuation = Mockito.mock(ManagedFundAccountValuationImpl.class);
        Mockito.when(managedFundAccountValuation.getAssetType()).thenReturn(AssetType.MANAGED_FUND);

        SubAccountValuation managedPortfolioAccountValuation = Mockito.mock(ManagedPortfolioAccountValuationImpl.class);
        Mockito.when(managedPortfolioAccountValuation.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);

        SubAccountValuation shareAccountValuation = Mockito.mock(ShareAccountValuationImpl.class);
        Mockito.when(shareAccountValuation.getAssetType()).thenReturn(AssetType.SHARE);

        SubAccountValuation termDepositAccountValuation = Mockito.mock(TermDepositAccountValuationImpl.class);
        Mockito.when(termDepositAccountValuation.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);

        subAccountValuations.add(managedFundAccountValuation);
        subAccountValuations.add(managedPortfolioAccountValuation);
        subAccountValuations.add(shareAccountValuation);
        subAccountValuations.add(termDepositAccountValuation);

        valuation = Mockito.mock(WrapAccountValuation.class);
        Mockito.when(valuation.getSubAccountValuations()).thenReturn(subAccountValuations);

        Asset filteredAsset = Mockito.mock(Asset.class);
        Mockito.when(filteredAsset.getAssetId()).thenReturn("filteredAssetId");
        Mockito.when(filteredAsset.getStatus()).thenReturn(AssetStatus.OPEN);

        filteredAssets = new HashMap<String, Asset>();
        filteredAssets.put(filteredAsset.getAssetId(), filteredAsset);

        List<DistributionMethod> distributionMethods = new ArrayList<>();
        distributionMethods.add(DistributionMethod.CASH);
        distributionMethods.add(DistributionMethod.REINVEST);

        assetDistributionMethods = new HashMap<>();
        assetDistributionMethods.put("assetId", distributionMethods);
        assetDistributionMethods.put("filteredAssetId", distributionMethods);

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradeAssetDtoConverter#toAssetDTo(java.util.Map, java.util.Map, java.util.Map, com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation, java.util.Map, java.lang.String, java.util.Map)}
     * .
     */
    @Test
    public final void testToAssetDTo() {
        TradeAssetDto managedFundTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(managedFundTradeAssetDto.getAsset()).thenReturn(managedFundAssetDto);
        Map<String, TradeAssetDto> managedFundTradeAssetMap = new HashMap<>();
        managedFundTradeAssetMap.put("managedFundAssetId", managedFundTradeAssetDto);
        Mockito.when(managedFundAssetBuilder.buildTradeAssetFromAvailabeAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedFundTradeAssetDto);
        Mockito.when(managedFundAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedFundTradeAssetMap);

        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAssetDto);
        Map<String, TradeAssetDto> managedPortfolioTradeAssetMap = new HashMap<>();
        managedPortfolioTradeAssetMap.put("managedPortfolioAssetId", managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssetFromAvailableAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetMap);

        List<TradeAssetDto> termDepositTradeAssetMap = new ArrayList<>();
        Mockito.when(termDepositAssetBuilder.buildTradeAssets(termDepositAssetDetails, termDepositassetMap))
                .thenReturn(termDepositTradeAssetMap);

        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAssetDto);
        Map<String, TradeAssetDto> shareTradeAssetMap = new HashMap<>();
        shareTradeAssetMap.put("shareAssetId", shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssetFromAvailabeAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetMap);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(availableAssets, valuation, filteredAssets, true,
                assetDistributionMethods, DateTime.now());

        Assert.assertEquals(3, tradeAssetDtos.size());
        Assert.assertEquals("managedFundAssetId", tradeAssetDtos.get(0).getAsset().getAssetId());
        Assert.assertEquals("managedPortfolioAssetId", tradeAssetDtos.get(1).getAsset().getAssetId());
        Assert.assertEquals("shareAssetId", tradeAssetDtos.get(2).getAsset().getAssetId());
    }

    /**
     * Test method to check that if Managed Portfolio container (SubAccount) is closed, then
     * it should not be included in the Tradable assets list
     */
    @Test
    public final void testToAssetDTo_WhenMPSubAccountIsClosed_ThenAssetNotInTradableList() {
        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAssetDto);
        Map<String, TradeAssetDto> managedPortfolioTradeAssetMap = new HashMap<>();
        managedPortfolioTradeAssetMap.put("managedPortfolioAssetId", managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssetFromAvailableAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetMap);

        List<SubAccountValuation> subAccountValuations = new ArrayList<>();
        SubAccountValuation managedPortfolioAccountValuation = Mockito.mock(ManagedPortfolioAccountValuationImpl.class);
        Mockito.when(managedPortfolioAccountValuation.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        Mockito.when((((ManagedPortfolioAccountValuation) managedPortfolioAccountValuation).getStatus())).thenReturn(ManagedPortfolioStatus.CLOSED);
        subAccountValuations.add(managedPortfolioAccountValuation);

        valuation = Mockito.mock(WrapAccountValuation.class);
        Mockito.when(valuation.getSubAccountValuations()).thenReturn(subAccountValuations);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(availableAssets, valuation, filteredAssets, true,
                assetDistributionMethods, DateTime.now());

        Assert.assertEquals(0, tradeAssetDtos.size());
    }

    @Test
    public final void testBuildTradeAssetsFromAvailableAssets_whenAllAssetTypes_thenAllTradeAssetTypesBuilt() {
        TradeAssetDto managedFundTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(managedFundTradeAssetDto.getAsset()).thenReturn(managedFundAssetDto);
        Map<String, TradeAssetDto> managedFundTradeAssetMap = new HashMap<>();
        managedFundTradeAssetMap.put("managedFundAssetId", managedFundTradeAssetDto);
        Mockito.when(managedFundAssetBuilder.buildTradeAssetFromAvailabeAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedFundTradeAssetDto);
        Mockito.when(managedFundAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedFundTradeAssetMap);

        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAssetDto);
        Map<String, TradeAssetDto> managedPortfolioTradeAssetMap = new HashMap<>();
        managedPortfolioTradeAssetMap.put("managedPortfolioAssetId", managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssetFromAvailableAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetMap);

        List<TradeAssetDto> termDepositTradeAssetMap = new ArrayList<>();
        Mockito.when(termDepositAssetBuilder.buildTradeAssets(termDepositAssetDetails, termDepositassetMap))
                .thenReturn(termDepositTradeAssetMap);

        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAssetDto);
        Map<String, TradeAssetDto> shareTradeAssetMap = new HashMap<>();
        shareTradeAssetMap.put("shareAssetId", shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssetFromAvailabeAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetMap);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.buildTradeAssetsFromAvailableAssets(availableAssets, false,
                assetDistributionMethods, DateTime.now());

        Assert.assertEquals(4, tradeAssetDtos.size());
        Assert.assertEquals("indexAssetId", tradeAssetDtos.get(0).getAsset().getAssetId());
        Assert.assertEquals("managedFundAssetId", tradeAssetDtos.get(1).getAsset().getAssetId());
        Assert.assertEquals("managedPortfolioAssetId", tradeAssetDtos.get(2).getAsset().getAssetId());
        Assert.assertEquals("shareAssetId", tradeAssetDtos.get(3).getAsset().getAssetId());
    }

    @Test
    public final void testBuildTradeAssetsFromAvailableAssets_whenOptionAssetTypes_thenShareAssetBuilt() {
        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAssetDto);
        Map<String, TradeAssetDto> shareTradeAssetMap = new HashMap<>();
        shareTradeAssetMap.put("shareAssetId", shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssetFromAvailabeAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetMap);

        Asset optionAsset = Mockito.mock(Asset.class);
        Mockito.when(optionAsset.getAssetType()).thenReturn(AssetType.OPTION);
        Map<String, Asset> availableAssets = new HashMap<>();
        availableAssets.put("1234", optionAsset);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.buildTradeAssetsFromAvailableAssets(availableAssets, false,
                assetDistributionMethods, DateTime.now());

        Assert.assertEquals(1, tradeAssetDtos.size());
        Assert.assertEquals("shareAssetId", tradeAssetDtos.get(0).getAsset().getAssetId());
    }

    @Test
    public final void testBuildTradeAssetsFromAvailableAssets_whenBondAssetTypes_thenShareAssetBuilt() {
        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAssetDto);
        Map<String, TradeAssetDto> shareTradeAssetMap = new HashMap<>();
        shareTradeAssetMap.put("shareAssetId", shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssetFromAvailabeAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetDto);
        Mockito.when(shareAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(shareTradeAssetMap);

        Asset optionAsset = Mockito.mock(Asset.class);
        Mockito.when(optionAsset.getAssetType()).thenReturn(AssetType.BOND);
        Map<String, Asset> availableAssets = new HashMap<>();
        availableAssets.put("1234", optionAsset);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.buildTradeAssetsFromAvailableAssets(availableAssets, false,
                assetDistributionMethods, DateTime.now());

        Assert.assertEquals(1, tradeAssetDtos.size());
        Assert.assertEquals("shareAssetId", tradeAssetDtos.get(0).getAsset().getAssetId());
    }

    @Test
    public final void testBuildTradeAssetsFromAvailableAssets_whenTailoredAssetTypes_thenManagedPortfolioAssetBuilt() {
        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAssetDto);
        Map<String, TradeAssetDto> managedPortfolioTradeAssetMap = new HashMap<>();
        managedPortfolioTradeAssetMap.put("managedPortfolioAssetId", managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssetFromAvailableAsset(Mockito.any(Asset.class), Mockito.anyMap(),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetDto);
        Mockito.when(managedPortfolioAssetBuilder.buildTradeAssets(Mockito.any(SubAccountValuation.class),
                Mockito.anyMapOf(String.class, Asset.class), Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(DateTime.class))).thenReturn(managedPortfolioTradeAssetMap);

        Asset tailoredAsset = Mockito.mock(Asset.class);
        Mockito.when(tailoredAsset.getAssetType()).thenReturn(AssetType.TAILORED_PORTFOLIO);
        Map<String, Asset> availableAssets = new HashMap<>();
        availableAssets.put("1234", tailoredAsset);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.buildTradeAssetsFromAvailableAssets(availableAssets, false,
                assetDistributionMethods, DateTime.now());

        Assert.assertEquals(1, tradeAssetDtos.size());
        Assert.assertEquals("managedPortfolioAssetId", tradeAssetDtos.get(0).getAsset().getAssetId());
    }

    @Test
    public final void testtoTradeAssetTypeDtos_whenAllAssetTypes_thenReturnedInOrder() {
        TradeAssetDto managedFundTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedFundAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedFundTradeAssetDto.getAsset()).thenReturn(managedFundAsset);
        Mockito.when(managedFundTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.MANAGED_FUND.getDisplayName());
        Mockito.when(managedFundAsset.getAssetName()).thenReturn("AMP Capital Wholesale Plus Corporate Bond Fund");

        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAsset);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset().getAssetType())
                .thenReturn(AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Mockito.when(managedPortfolioAsset.getAssetName()).thenReturn("managedPortfolio");

        TradeAssetDto tailorMadePortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto tailorMadePortfolioAsset = Mockito.mock(AssetDto.class);
        Mockito.when(tailorMadePortfolioTradeAssetDto.getAsset()).thenReturn(tailorMadePortfolioAsset);
        Mockito.when(tailorMadePortfolioTradeAssetDto.getAsset().getAssetType())
                .thenReturn(AssetType.TAILORED_PORTFOLIO.getDisplayName());
        Mockito.when(tailorMadePortfolioAsset.getAssetName()).thenReturn("tailoredPortfolio");

        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto shareAsset = Mockito.mock(AssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAsset);
        Mockito.when(shareTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.SHARE.getDisplayName());
        Mockito.when(shareAsset.getAssetName()).thenReturn("shareAsset");

        List<TradeAssetDto> tradeAssetDtos = new ArrayList<>();

        Asset termDepositAsset = Mockito.mock(Asset.class);
        Mockito.when(termDepositAsset.getAssetType()).thenReturn(AssetType.TERM_DEPOSIT);
        Mockito.when(termDepositAsset.getAssetName()).thenReturn("Bt 3 months term deposit");
        Map<String, Asset> availableAssets = new HashMap<>();
        availableAssets.put("1234", termDepositAsset);

        tradeAssetDtos.add(managedFundTradeAssetDto);
        tradeAssetDtos.add(tailorMadePortfolioTradeAssetDto);
        tradeAssetDtos.add(managedPortfolioTradeAssetDto);
        tradeAssetDtos.add(shareTradeAssetDto);

        List<TradeAssetTypeDto> sortedTradeAssetDtos = tradeAssetDtoConverter.toTradeAssetTypeDtos(tradeAssetDtos,
                availableAssets);

        Assert.assertEquals(6, sortedTradeAssetDtos.size());
        Assert.assertEquals(AssetType.SHARE.getDisplayName(), sortedTradeAssetDtos.get(0).getKey());
        Assert.assertEquals(AssetType.MANAGED_FUND.getDisplayName(), sortedTradeAssetDtos.get(1).getKey());
        Assert.assertEquals(TradableAssetsDtoServiceHelper.WHOLESALE_PLUS, sortedTradeAssetDtos.get(2).getKey());
        Assert.assertEquals(AssetType.MANAGED_PORTFOLIO.getDisplayName(), sortedTradeAssetDtos.get(3).getKey());
        Assert.assertEquals(AssetType.TAILORED_PORTFOLIO.getDisplayName(), sortedTradeAssetDtos.get(4).getKey());
        Assert.assertEquals(AssetType.TERM_DEPOSIT.getDisplayName(), sortedTradeAssetDtos.get(5).getKey());
    }

    @Test
    public final void testtoTradeAssetTypeDtos_whenNoWholesalePlus_thenNotReturned() {
        TradeAssetDto managedFundTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedFundAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedFundTradeAssetDto.getAsset()).thenReturn(managedFundAsset);
        Mockito.when(managedFundTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.MANAGED_FUND.getDisplayName());
        Mockito.when(managedFundAsset.getAssetName()).thenReturn("managed fund");

        Asset mfAsset = Mockito.mock(Asset.class);
        Mockito.when(mfAsset.getAssetType()).thenReturn(AssetType.MANAGED_FUND);
        Map<String, Asset> availableAssets = new HashMap<>();
        availableAssets.put("1234", mfAsset);

        List<TradeAssetDto> tradeAssetDtos = new ArrayList<>();
        tradeAssetDtos.add(managedFundTradeAssetDto);
        List<TradeAssetTypeDto> sortedTradeAssetDtos = tradeAssetDtoConverter.toTradeAssetTypeDtos(tradeAssetDtos,
                availableAssets);

        Assert.assertEquals(1, sortedTradeAssetDtos.size());
        Assert.assertEquals(AssetType.MANAGED_FUND.getDisplayName(), sortedTradeAssetDtos.get(0).getKey());
    }
}
