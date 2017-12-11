package com.bt.nextgen.api.trading.v1.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;

@RunWith(MockitoJUnitRunner.class)
public class TradableAssetsDtoServiceFilterTest {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");

    @InjectMocks
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Mock
    private DistributionAccountDtoService distributionAccountDtoService;

    @Mock
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    private DateTime bankDate;

    private ManagedPortfolioAccountValuation valuation;
    private ManagedPortfolioAccountValuation valuationNoBalance;
    private AccountHolding holding;
    private AccountHolding holdingNoBalance;

    Asset suspendedAsset;
    Asset terminatedAsset;
    Asset delistedAsset;
    Asset closedToNewAsset;
    Asset closedAsset;
    Asset openAsset;
    Asset prepaymentAsset;

    Asset assetNullDates;
    Asset assetBeforeStartDate;
    Asset assetAfterStartDate;
    Asset assetBeforeEndDate;
    Asset assetAfterEndDate;
    Asset assetAfterStartDateBeforeEndDate;

    private List<Asset> availableAssets;
    private Map<String, Asset> assetMap;
    private Map<String, Asset> filteredAssetMap;
    private Map<String, Asset> valuationAssetMap;

    private List<DistributionMethod> distributionMethods;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        suspendedAsset = Mockito.mock(Asset.class);
        Mockito.when(suspendedAsset.getAssetId()).thenReturn("suspendedAsset");
        Mockito.when(suspendedAsset.getStatus()).thenReturn(AssetStatus.SUSPENDED);

        terminatedAsset = Mockito.mock(Asset.class);
        Mockito.when(terminatedAsset.getAssetId()).thenReturn("terminatedAsset");
        Mockito.when(terminatedAsset.getStatus()).thenReturn(AssetStatus.TERMINATED);

        delistedAsset = Mockito.mock(Asset.class);
        Mockito.when(delistedAsset.getAssetId()).thenReturn("delistedAsset");
        Mockito.when(delistedAsset.getStatus()).thenReturn(AssetStatus.DELISTED);

        closedToNewAsset = Mockito.mock(Asset.class);
        Mockito.when(closedToNewAsset.getAssetId()).thenReturn("closedToNewAsset");
        Mockito.when(closedToNewAsset.getStatus()).thenReturn(AssetStatus.CLOSED_TO_NEW);

        closedAsset = Mockito.mock(Asset.class);
        Mockito.when(closedAsset.getAssetId()).thenReturn("closedAsset");
        Mockito.when(closedAsset.getStatus()).thenReturn(AssetStatus.CLOSED);

        openAsset = Mockito.mock(Asset.class);
        Mockito.when(openAsset.getAssetId()).thenReturn("openAsset");
        Mockito.when(openAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(openAsset.isPrepayment()).thenReturn(false);

        prepaymentAsset = Mockito.mock(Asset.class);
        Mockito.when(prepaymentAsset.getAssetId()).thenReturn("prepaymentAsset");
        Mockito.when(prepaymentAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(prepaymentAsset.isPrepayment()).thenReturn(true);

        assetNullDates = Mockito.mock(Asset.class);
        Mockito.when(assetNullDates.getStartDate()).thenReturn(null);
        Mockito.when(assetNullDates.getEndDate()).thenReturn(null);

        assetBeforeStartDate = Mockito.mock(Asset.class);
        Mockito.when(assetBeforeStartDate.getStartDate()).thenReturn(formatter.parseDateTime("31/12/2014"));

        assetAfterStartDate = Mockito.mock(Asset.class);
        Mockito.when(assetAfterStartDate.getStartDate()).thenReturn(formatter.parseDateTime("02/01/2015"));

        assetBeforeEndDate = Mockito.mock(Asset.class);
        Mockito.when(assetBeforeEndDate.getEndDate()).thenReturn(formatter.parseDateTime("31/12/2014"));

        assetAfterEndDate = Mockito.mock(Asset.class);
        Mockito.when(assetAfterEndDate.getEndDate()).thenReturn(formatter.parseDateTime("02/01/2015"));

        assetAfterStartDateBeforeEndDate = Mockito.mock(Asset.class);
        Mockito.when(assetAfterStartDateBeforeEndDate.getStartDate()).thenReturn(formatter.parseDateTime("02/01/2015"));
        Mockito.when(assetAfterStartDateBeforeEndDate.getEndDate()).thenReturn(formatter.parseDateTime("31/12/2014"));

        valuation = Mockito.mock(ManagedPortfolioAccountValuation.class);
        Mockito.when(valuation.getAsset()).thenReturn(openAsset);
        Mockito.when(valuation.getAvailableBalance()).thenReturn(new BigDecimal("50000"));

        valuationNoBalance = Mockito.mock(ManagedPortfolioAccountValuation.class);
        Mockito.when(valuationNoBalance.getAsset()).thenReturn(openAsset);
        Mockito.when(valuationNoBalance.getAvailableBalance()).thenReturn(new BigDecimal("0"));

        holding = Mockito.mock(AccountHolding.class);
        Mockito.when(holding.getAsset()).thenReturn(openAsset);
        Mockito.when(holding.getAvailableBalance()).thenReturn(new BigDecimal("50000"));
        Mockito.when(holding.getAvailableUnits()).thenReturn(new BigDecimal("50000"));

        holdingNoBalance = Mockito.mock(AccountHolding.class);
        Mockito.when(holdingNoBalance.getAsset()).thenReturn(openAsset);
        Mockito.when(holdingNoBalance.getAvailableBalance()).thenReturn(new BigDecimal("0"));
        Mockito.when(holdingNoBalance.getAvailableUnits()).thenReturn(new BigDecimal("0"));

        valuationAssetMap = new HashMap<String, Asset>();
        valuationAssetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);

        assetMap = new HashMap<String, Asset>();
        assetMap.put(suspendedAsset.getAssetId(), suspendedAsset);
        assetMap.put(terminatedAsset.getAssetId(), terminatedAsset);
        assetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        assetMap.put(closedAsset.getAssetId(), closedAsset);
        assetMap.put(openAsset.getAssetId(), openAsset);

        filteredAssetMap = new HashMap<String, Asset>();
        filteredAssetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        filteredAssetMap.put(closedAsset.getAssetId(), closedAsset);
        filteredAssetMap.put(suspendedAsset.getAssetId(), suspendedAsset);

        availableAssets = new ArrayList<>();
        availableAssets.add(suspendedAsset);
        availableAssets.add(terminatedAsset);
        availableAssets.add(closedToNewAsset);
        availableAssets.add(closedAsset);
        availableAssets.add(openAsset);

        distributionMethods = new ArrayList<>();
        distributionMethods.add(DistributionMethod.CASH);
        distributionMethods.add(DistributionMethod.REINVEST);

        bankDate = formatter.parseDateTime("01/01/2015");
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#filterAvailableAssetsList(java.util.List, java.util.Map)}
     * .
     */
    @Test
    public final void testFilterAvailableAssetsList_whenNoFilteredAssets_thenAssetsMatch() {
        Map<String, Asset> filteredAssets = tradableAssetsDtoServiceFilter.filterAvailableAssetsList(availableAssets, assetMap);
        Assert.assertEquals(5, filteredAssets.size());
    }

    @Test
    public final void testFilterAvailableAssetsList_whenFilteredAssets_thenAssetsMatch() {
        Map<String, Asset> filteredAssets = tradableAssetsDtoServiceFilter.filterAvailableAssetsList(availableAssets,
                filteredAssetMap);
        Assert.assertEquals(3, filteredAssets.size());
    }

    @Test
    public final void testIsAssetSellable_whenFilteredAssetsAndBalance_thenIsSellable() {
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetSellable(openAsset, new BigDecimal("50000.00"), bankDate));
    }

    @Test
    public final void testIsAssetSellable_whenAmountNull_thenNotSellable() {
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetSellable(openAsset, null, bankDate));
    }

    @Test
    public final void testIsAssetSellable_whenAmountZero_thenNotSellable() {
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetSellable(openAsset, new BigDecimal("0.00"), bankDate));
    }

    @Test
    public final void testIsAssetSellable_whenIsPrepayment_thenNotSellable() {
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetSellable(prepaymentAsset, new BigDecimal("50000.00"), bankDate));
    }

    @Test
    public final void testIsAssetBuyable_whenIsNotPrepayment_thenBuyable() {
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetBuyable(openAsset, true, bankDate));
    }

    @Test
    public final void testIsAssetBuyable_whenIsPrepayment_thenNotBuyable() {
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetBuyable(prepaymentAsset, true, bankDate));
    }

    @Test
    public final void testIsValidAmount() {
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isValidAmount(new BigDecimal("50000.00")));
    }

    @Test
    public final void testIsAssetDateTradable() {
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetDateTradable(assetNullDates, bankDate));
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetDateTradable(assetBeforeStartDate, bankDate));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetDateTradable(assetAfterStartDate, bankDate));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetDateTradable(assetBeforeEndDate, bankDate));
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetDateTradable(assetAfterEndDate, bankDate));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetDateTradable(assetAfterStartDateBeforeEndDate, bankDate));
    }

    @Test
    public final void testIsAssetStatusSellable() {
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetStatusSellable(openAsset));
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetStatusSellable(closedAsset));
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetStatusSellable(closedToNewAsset));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusSellable(suspendedAsset));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusSellable(terminatedAsset));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusSellable(delistedAsset));
    }

    @Test
    public final void testIsAssetStatusBuyable() {
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(openAsset, false));
        Assert.assertTrue(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(closedToNewAsset, true));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(closedToNewAsset, false));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(closedAsset, false));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(suspendedAsset, false));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(terminatedAsset, false));
        Assert.assertFalse(tradableAssetsDtoServiceFilter.isAssetStatusBuyable(delistedAsset, false));
    }

    @Test
    public final void testFilterAssetsForAdviserGroup_withMatchingExclusions() {
        ArrayList<String> assetsToExclude = new ArrayList<>();
        assetsToExclude.add(closedAsset.getAssetId());
        assetsToExclude.add(terminatedAsset.getAssetId());

        assetMap = new HashMap<>();
        assetMap.put(suspendedAsset.getAssetId(), suspendedAsset);
        assetMap.put(terminatedAsset.getAssetId(), terminatedAsset);
        assetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        assetMap.put(closedAsset.getAssetId(), closedAsset);
        assetMap.put(openAsset.getAssetId(), openAsset);

        tradableAssetsDtoServiceFilter.filterAssetsForAdviserGroup(assetMap, assetsToExclude);

        Assert.assertEquals(assetMap.size(), 3);
        Assert.assertNotNull(assetMap.get(openAsset.getAssetId()));
        Assert.assertNotNull(assetMap.get(suspendedAsset.getAssetId()));
        Assert.assertNotNull(assetMap.get(closedToNewAsset.getAssetId()));
    }

    @Test
    public final void testFilterAssetsForAdviserGroup_withNoMatchingExclusions() {
        ArrayList<String> assetsToExclude = new ArrayList<>();
        assetsToExclude.add(terminatedAsset.getAssetId());

        assetMap = new HashMap<>();
        assetMap.put(suspendedAsset.getAssetId(), suspendedAsset);
        assetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        assetMap.put(closedAsset.getAssetId(), closedAsset);
        assetMap.put(openAsset.getAssetId(), openAsset);

        tradableAssetsDtoServiceFilter.filterAssetsForAdviserGroup(assetMap, assetsToExclude);

        Assert.assertEquals(assetMap.size(), 4);
        Assert.assertNotNull(assetMap.get(openAsset.getAssetId()));
        Assert.assertNotNull(assetMap.get(suspendedAsset.getAssetId()));
        Assert.assertNotNull(assetMap.get(closedAsset.getAssetId()));
        Assert.assertNotNull(assetMap.get(closedToNewAsset.getAssetId()));
    }
}
