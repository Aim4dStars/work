package com.bt.nextgen.api.trading.v1.util;

import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedFundAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ManagedPortfolioAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.ShareAccountValuationImpl;
import com.bt.nextgen.service.avaloq.portfolio.valuation.WrapAccountValuationImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.collection.AvaloqCollectionIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.product.ProductIdentifier;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TradableAssetsDtoServiceHelperTest {

    @InjectMocks
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Mock
    private DistributionAccountDtoService distributionAccountDtoService;

    @Mock
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Mock
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    public AvaloqCollectionIntegrationService collectionService;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    private Asset managedFundAsset;
    private Asset managedPortfolioAsset;
    private Asset shareAsset;
    private Map<String, Asset> assetMap;

    private List<DistributionMethod> distributionMethods;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        managedFundAsset = Mockito.mock(Asset.class);
        Mockito.when(managedFundAsset.getAssetId()).thenReturn("managedFundAssetId");
        Mockito.when(managedFundAsset.getAssetName()).thenReturn("managed fund asset");

        managedPortfolioAsset = Mockito.mock(Asset.class);
        Mockito.when(managedPortfolioAsset.getAssetId()).thenReturn("managedPortfolioAssetId");
        Mockito.when(managedPortfolioAsset.getAssetName()).thenReturn("managed portfolio asset");

        shareAsset = Mockito.mock(Asset.class);
        Mockito.when(shareAsset.getAssetId()).thenReturn("shareAssetId");
        Mockito.when(shareAsset.getAssetName()).thenReturn("share asset");


        Asset suspendedAsset = Mockito.mock(Asset.class);
        Mockito.when(suspendedAsset.getAssetId()).thenReturn("suspendedAsset");
        Mockito.when(suspendedAsset.getStatus()).thenReturn(AssetStatus.SUSPENDED);

        Asset terminatedAsset = Mockito.mock(Asset.class);
        Mockito.when(terminatedAsset.getAssetId()).thenReturn("terminatedAsset");
        Mockito.when(terminatedAsset.getStatus()).thenReturn(AssetStatus.TERMINATED);

        Asset closedToNewAsset = Mockito.mock(Asset.class);
        Mockito.when(closedToNewAsset.getAssetId()).thenReturn("closedToNewAsset");
        Mockito.when(closedToNewAsset.getStatus()).thenReturn(AssetStatus.CLOSED_TO_NEW);

        Asset closedAsset = Mockito.mock(Asset.class);
        Mockito.when(closedAsset.getAssetId()).thenReturn("closedAsset");
        Mockito.when(closedAsset.getStatus()).thenReturn(AssetStatus.CLOSED);

        Asset openAsset = Mockito.mock(Asset.class);
        Mockito.when(openAsset.getAssetId()).thenReturn("openAsset");
        Mockito.when(openAsset.getStatus()).thenReturn(AssetStatus.OPEN);

        assetMap = new HashMap<String, Asset>();

        assetMap.put(suspendedAsset.getAssetId(), suspendedAsset);
        assetMap.put(terminatedAsset.getAssetId(), terminatedAsset);
        assetMap.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        assetMap.put(closedAsset.getAssetId(), closedAsset);
        assetMap.put(openAsset.getAssetId(), openAsset);

        distributionMethods = new ArrayList<>();
        distributionMethods.add(DistributionMethod.CASH);
        distributionMethods.add(DistributionMethod.REINVEST);

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#loadDistributionMethods(com.btfin.panorama.service.integration.account.WrapAccountDetail, java.util.Collection)}
     * .
     */
    @Test
    public final void testLoadDistributionMethods() {
        WrapAccountDetail wrapAccountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(distributionAccountDtoService.getAvailableDistributionMethod(Mockito.any(Asset.class)))
                .thenReturn(distributionMethods);
        Map<String, List<DistributionMethod>> assetDistributionMethods = tradableAssetsDtoServiceHelper
                .loadDistributionMethods(assetMap.values());

        Assert.assertEquals(5, assetDistributionMethods.size());
        Assert.assertNotNull(assetDistributionMethods.values().iterator());
        List<DistributionMethod> distributionMethodsReturned = assetDistributionMethods.values().iterator().next();
        Assert.assertEquals(DistributionMethod.CASH, distributionMethodsReturned.get(0));
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#loadValuation(java.lang.String, java.lang.String, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testLoadValuation() {
        WrapAccountValuation valuation = Mockito.mock(WrapAccountValuation.class);
        Mockito.when(valuation.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));

        Mockito.when(portfolioService.loadWrapAccountValuation(Mockito.any(AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        WrapAccountValuation returnedValuation = tradableAssetsDtoServiceHelper
                .loadValuation("C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C", new FailFastErrorsImpl());

        Assert.assertEquals("accountId", returnedValuation.getAccountKey().getId());

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#loadAccount(com.bt.nextgen.service.ServiceErrors, java.lang.String)}
     * .
     */
    @Test
    public final void testLoadAccount() {

        WrapAccountDetail mockedWrapAccountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(mockedWrapAccountDetail.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(accountService.loadWrapAccountDetail(Mockito.any(AccountKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(mockedWrapAccountDetail);
        WrapAccountDetail returnedWrapAccountDetail = tradableAssetsDtoServiceHelper
                .loadAccount("C77D12EEFB4C2E219EB58C96951346085CCD746B57EA0B6C", new FailFastErrorsImpl());
        Assert.assertEquals("accountId", returnedWrapAccountDetail.getAccountKey().getId());
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#loadBroker(com.btfin.panorama.service.integration.account.WrapAccountDetail, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testLoadBroker() {

        WrapAccountDetail mockedWrapAccountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(mockedWrapAccountDetail.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(mockedWrapAccountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("adviserId"));

        Broker adviser = Mockito.mock(Broker.class);
        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class))).thenReturn(adviser);
        Mockito.when(adviser.getDealerKey()).thenReturn(BrokerKey.valueOf("dealerId"));
        BrokerKey returnedBrokerKey = tradableAssetsDtoServiceHelper.loadBroker(mockedWrapAccountDetail,
                new FailFastErrorsImpl());

        Assert.assertEquals("dealerId", returnedBrokerKey.getId());

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#loadDirectProductKey(com.btfin.panorama.service.integration.account.WrapAccountDetail)}
     * .
     */
    @Test
    public final void testLoadDirectProductKey() {

        ProductIdentifier productIdentifier = Mockito.mock(ProductIdentifier.class);
        Mockito.when(productIdentifier.getProductKey()).thenReturn(ProductKey.valueOf("productId"));
        List<SubAccount> subAccounts = new ArrayList<SubAccount>();
        SubAccount subAccount = Mockito.mock(SubAccount.class);
        Mockito.when(subAccount.getSubAccountType()).thenReturn(ContainerType.DIRECT);
        Mockito.when(subAccount.getProductIdentifier()).thenReturn(productIdentifier);
        subAccounts.add(subAccount);

        WrapAccountDetail mockedWrapAccountDetail = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(mockedWrapAccountDetail.getAccountKey()).thenReturn(AccountKey.valueOf("accountId"));
        Mockito.when(mockedWrapAccountDetail.getSubAccounts()).thenReturn(subAccounts);

        ProductKey returnedProductkey = tradableAssetsDtoServiceHelper.loadDirectProductKey(mockedWrapAccountDetail);
        Assert.assertEquals("productId", returnedProductkey.getId());

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceFilter#loadDistributionMethods(com.btfin.panorama.service.integration.account.WrapAccountDetail, java.util.Collection)}
     * .
     */
    @Test
    public final void testValuationAssets() {
        WrapAccountValuation val = Mockito.mock(WrapAccountValuationImpl.class);
        tradableAssetsDtoServiceHelper.getValuationAssets(val);

        List<SubAccountValuation> subAccounts = new ArrayList<>();

        ManagedPortfolioAccountValuationImpl mpVal = new ManagedPortfolioAccountValuationImpl();
        Asset mpAsset = Mockito.mock(Asset.class);
        Mockito.when(mpAsset.getAssetId()).thenReturn("mpasset");
        Mockito.when(mpAsset.getAssetType()).thenReturn(AssetType.MANAGED_PORTFOLIO);
        mpVal.setAsset(mpAsset);
        subAccounts.add(mpVal);

        ManagedFundAccountValuationImpl mfVal = new ManagedFundAccountValuationImpl();
        Asset mfAsset = Mockito.mock(Asset.class);
        Mockito.when(mfAsset.getAssetId()).thenReturn("mfasset");
        AccountHolding mfHolding = Mockito.mock(AccountHolding.class);
        Mockito.when(mfHolding.getAsset()).thenReturn(mfAsset);
        mfVal.addHoldings(Collections.singletonList(mfHolding));
        subAccounts.add(mfVal);

        ShareAccountValuationImpl shareVal = new ShareAccountValuationImpl(AssetType.SHARE);
        Asset shareAsset = Mockito.mock(Asset.class);
        Mockito.when(shareAsset.getAssetId()).thenReturn("shareasset");
        AccountHolding shareHolding = Mockito.mock(AccountHolding.class);
        Mockito.when(shareHolding.getAsset()).thenReturn(shareAsset);
        shareVal.addHoldings(Collections.singletonList(shareHolding));
        subAccounts.add(shareVal);

        Mockito.when(val.getSubAccountValuations()).thenReturn(subAccounts);

        Map<String, Asset> valAssets = tradableAssetsDtoServiceHelper.getValuationAssets(val);

        Assert.assertEquals(3, valAssets.size());
        Assert.assertEquals(mpAsset, valAssets.get("mpasset"));
        Assert.assertEquals(mfAsset, valAssets.get("mfasset"));
        Assert.assertEquals(shareAsset, valAssets.get("shareasset"));
    }

    @Test
    public final void testFilterWholesalePlus_whenNoWholesalePlus_thenRemoved() {
        Map<String, Asset> filteredAssets = new HashMap<>();
        filteredAssets.put("managedFundAssetId", managedFundAsset);
        filteredAssets.put("managedPortfolioAssetId", managedPortfolioAsset);
        filteredAssets.put("shareAssetId", shareAsset);

        tradableAssetsDtoServiceHelper.filterWholesalePlus(filteredAssets);
        Assert.assertEquals(0, filteredAssets.size());
    }

    @Test
    public final void testFilterWholesalePlus_whenWholesalePlus_thenRetained() {
        Asset wholesaleAsset = Mockito.mock(Asset.class);
        Mockito.when(wholesaleAsset.getAssetId()).thenReturn("wholesaleAssetId");
        Mockito.when(wholesaleAsset.getAssetName()).thenReturn("AMP Capital Wholesale Plus Corporate Bond Fund");

        Map<String, Asset> filteredAssets = new HashMap<>();
        filteredAssets.put("managedFundAssetId", managedFundAsset);
        filteredAssets.put("managedPortfolioAssetId", managedPortfolioAsset);
        filteredAssets.put("shareAssetId", shareAsset);
        filteredAssets.put("wholesaleAssetId", wholesaleAsset);

        tradableAssetsDtoServiceHelper.filterWholesalePlus(filteredAssets);
        Assert.assertEquals(1, filteredAssets.size());
        Assert.assertEquals("AMP Capital Wholesale Plus Corporate Bond Fund",
                filteredAssets.get("wholesaleAssetId").getAssetName());
    }

    @Test
    public final void testGetAssetsToExclude_forDirectAdviser() {
        List<String> collectionList = new ArrayList<>();
        collectionList.add("12345");
        collectionList.add("45678");

        Broker mockAdviser = Mockito.mock(Broker.class);
        WrapAccountDetail mockedWrapAccountDetail = Mockito.mock(WrapAccountDetail.class);

        Mockito.when(mockedWrapAccountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("OE.BTDIRECT"));
        Mockito.when(mockAdviser.isDirectInvestment()).thenReturn(Boolean.TRUE);

        Mockito.when(brokerService.getBroker(Mockito.any(BrokerKey.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(mockAdviser);
        Mockito.when(collectionService.loadAssetsForCollection(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(collectionList);

        List<String> assetIds = tradableAssetsDtoServiceHelper.getAssetsToExclude(mockedWrapAccountDetail,
                new FailFastErrorsImpl());
        Assert.assertEquals(2, assetIds.size());
    }

    @Test
    public final void testGetAssetsToExclude_forNonDirectAdviser() {
        List<String> collectionList = new ArrayList<>();
        collectionList.add("12345");
        collectionList.add("45678");

        Broker mockAdviser = Mockito.mock(Broker.class);
        WrapAccountDetail mockedWrapAccountDetail = Mockito.mock(WrapAccountDetail.class);

        Mockito.when(mockedWrapAccountDetail.getAdviserKey()).thenReturn(BrokerKey.valueOf("OE.BTDIRECT"));
        Mockito.when(mockAdviser.isDirectInvestment()).thenReturn(Boolean.FALSE);

        List<String> assetIds = tradableAssetsDtoServiceHelper.getAssetsToExclude(mockedWrapAccountDetail,
                new FailFastErrorsImpl());
        Assert.assertEquals(0, assetIds.size());
    }
}
