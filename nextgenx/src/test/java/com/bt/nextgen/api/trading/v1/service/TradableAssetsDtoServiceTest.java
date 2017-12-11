package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.account.v2.service.DistributionAccountDtoService;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.trading.v1.model.TermDepositInterestRateKey;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDtoV2;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice.TermDepositTradeAssetService;
import com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice.TermDepositTradeAssetServiceV2;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRateImpl;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TradableAssetsDtoServiceTest {

    @InjectMocks
    private TradableAssetsDtoServiceImpl tradableAssetsDtoService;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private BrokerIntegrationService brokerService;

    @Mock
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Mock
    private TradeAssetDtoConverter tradeAssetDtoConverter;

    @Mock
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Mock
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Mock
    TermDepositTradeAssetService termDepositTradeAssetService;

    @Mock
    private DistributionAccountDtoService distributionAccountDtoService;

    @Mock
    BankDateIntegrationService bankDateIntegrationService;

    @Mock
    FeatureTogglesService featureTogglesService;

    @Mock
    FeatureToggles featureToggles;

    @Mock
    TermDepositTradeAssetServiceV2 termDepositTradeAssetServiceV2;

    List<ApiSearchCriteria> criteriaList;

    private Map<String, Asset> filterAssetsForCriteria;
    private List<String> brokerProductAssets;
    private Map<String, Asset> availableAssets;
    private Map<String, Asset> termDepositassetMap;
    private WrapAccountValuation valuation;
    List<TradeAssetDto> tradeAssetDtos;

    private Map<String, Asset> assetMap;
    private Map<String, List<DistributionMethod>> assetDistributionMethods;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        filterAssetsForCriteria = new HashMap<String, Asset>();

        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, "accountId",
                OperationType.STRING);
        ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, "all", OperationType.STRING);
        ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, "all",
                OperationType.STRING);

        criteriaList = new ArrayList<>();

        criteriaList.add(accountIdCriteria);
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);

        brokerProductAssets = null;
        availableAssets = null;
        termDepositassetMap = null;
        valuation = Mockito.mock(WrapAccountValuation.class);
        tradeAssetDtos = null;

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

        filterAssetsForCriteria.put(terminatedAsset.getAssetId(), terminatedAsset);
        filterAssetsForCriteria.put(closedToNewAsset.getAssetId(), closedToNewAsset);
        filterAssetsForCriteria.put(closedAsset.getAssetId(), closedAsset);
        filterAssetsForCriteria.put(openAsset.getAssetId(), openAsset);

        availableAssets = new HashMap<>();
        availableAssets.put(openAsset.getAssetId(), openAsset);

        tradeAssetDtos = new ArrayList<>();
        TradeAssetDto tradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(tradeAssetDto.getAsset()).thenReturn(assetDto);
        Mockito.when(assetDto.getAssetId()).thenReturn("tradeAssetId");
        tradeAssetDtos.add(tradeAssetDto);

        List<DistributionMethod> distributionMethods = new ArrayList<>();
        distributionMethods.add(DistributionMethod.CASH);
        distributionMethods.add(DistributionMethod.REINVEST);

        assetDistributionMethods = new HashMap<>();

        assetDistributionMethods.put("assetId", distributionMethods);

        WrapAccountDetail account = Mockito.mock(WrapAccountDetail.class);
        BrokerKey brokerKey = BrokerKey.valueOf("brokerId");
        ProductKey directProductKey = ProductKey.valueOf("productId");

        Mockito.when(tradableAssetsDtoServiceHelper.loadAccount(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(account);
        Mockito.when(
                tradableAssetsDtoServiceHelper.loadBroker(Mockito.any(WrapAccountDetail.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(brokerKey);

        Mockito.when(tradableAssetsDtoServiceHelper.loadDirectProductKey(Mockito.any(WrapAccountDetail.class)))
                .thenReturn(directProductKey);

        Mockito.when(assetIntegrationService.loadAssetsForCriteria(Mockito.anyCollection(), Mockito.anyString(),
                Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(filterAssetsForCriteria);

        Mockito.when(assetIntegrationService.loadAvailableAssetsForBrokerAndProduct(Mockito.any(BrokerKey.class),
                Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerProductAssets);

        Mockito.when(tradableAssetsDtoServiceFilter.filterAvailableAssetsList(Mockito.anyListOf(Asset.class),
                Mockito.anyMapOf(String.class, Asset.class))).thenReturn(availableAssets);

        List<TradeAssetDto> termDepositTradeAssetDtoList = new ArrayList<>();
        TermDepositTradeAssetDto termDepositTradeAssetDto = Mockito.mock(TermDepositTradeAssetDto.class);
        termDepositTradeAssetDtoList.add(termDepositTradeAssetDto);

        Mockito.when(termDepositTradeAssetService.loadTermDepositTradeAssets(Mockito.anyCollection(), Mockito.anyString(),
                Mockito.any(BrokerKey.class), Mockito.anyListOf(Asset.class), Mockito.anyMap(), Mockito.any(ServiceErrors.class)))
                .thenReturn(termDepositTradeAssetDtoList);

        Mockito.when(assetIntegrationService.loadAssets(Mockito.anyListOf(String.class), Mockito.any(ServiceErrors.class)))
                .thenReturn(termDepositassetMap);

        Mockito.when(tradableAssetsDtoServiceHelper.loadValuation(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(valuation);

        Mockito.when(tradableAssetsDtoServiceHelper.loadDistributionMethods(Mockito.anyCollection()))
                .thenReturn(assetDistributionMethods);

        Mockito.when(tradeAssetDtoConverter.toAssetDto(Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(WrapAccountValuation.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyBoolean(),
                Mockito.anyMap(), Mockito.any(DateTime.class))).thenReturn(tradeAssetDtos);

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(DateTime.now());
        Mockito.when(featureTogglesService.findOne(any(ServiceErrors.class))).thenReturn(featureToggles);
    }


    @Test
    public final void testSearch() {
        List<TradeAssetDto> returnedTradeAssetDtos = tradableAssetsDtoService.search(criteriaList, new FailFastErrorsImpl());

        Assert.assertEquals(2, returnedTradeAssetDtos.size());
        Assert.assertEquals("tradeAssetId", returnedTradeAssetDtos.get(0).getAsset().getAssetId());
    }


    @Test
    public final void testSearch_With_NewTDRates() {
        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, "accountId",
                OperationType.STRING);
        ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, "maturity", OperationType.STRING);
        ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, "Term deposit",
                OperationType.STRING);

        criteriaList = new ArrayList<>();

        criteriaList.add(accountIdCriteria);
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);
        FeatureToggles termDepositToggle = new FeatureToggles();
        termDepositToggle.setFeatureToggle("feature.termDepositToggle", true);
        when(featureToggles.getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE)).thenReturn(true);
        List<TradeAssetDto> termDepositTradeAssetDtoV2List = new ArrayList<>();
        TermDepositTradeAssetDtoV2 termDepositTradeAssetDtoV2 = Mockito.mock(TermDepositTradeAssetDtoV2.class);
        termDepositTradeAssetDtoV2List.add(termDepositTradeAssetDtoV2);

        Mockito.when(termDepositTradeAssetServiceV2.loadTermDepositTradeAssets(Mockito.anyList(), Mockito.anyString(),
                Mockito.any(TermDepositInterestRateKey.class), Mockito.anyListOf(Asset.class), Mockito.anyMap(),Mockito.any(ServiceErrors.class))).thenReturn(termDepositTradeAssetDtoV2List);


        List<TradeAssetDto> returnedTradeAssetDtos = tradableAssetsDtoService.search(criteriaList, new FailFastErrorsImpl());

        Assert.assertEquals(2, returnedTradeAssetDtos.size());
        Assert.assertEquals("tradeAssetId", returnedTradeAssetDtos.get(0).getAsset().getAssetId());
    }

    @Test
    public final void testGetAssetTypes_whenShare_thenOptionsIncluded() {
        Collection<AssetType> assetTypes = tradableAssetsDtoService.getAssetTypes(AssetType.SHARE.getDisplayName());

        Assert.assertEquals(3, assetTypes.size());
        Assert.assertTrue(assetTypes.contains(AssetType.SHARE));
        Assert.assertTrue(assetTypes.contains(AssetType.OPTION));
        Assert.assertTrue(assetTypes.contains(AssetType.BOND));
    }

    @Test
    public final void testGetAssetTypes_whenNotShare_thenSingleton() {
        Collection<AssetType> mfAssetTypes = tradableAssetsDtoService.getAssetTypes(AssetType.MANAGED_FUND.getDisplayName());
        Assert.assertEquals(1, mfAssetTypes.size());
        Assert.assertTrue(mfAssetTypes.contains(AssetType.MANAGED_FUND));

        Collection<AssetType> mpAssetTypes = tradableAssetsDtoService.getAssetTypes(AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Assert.assertEquals(1, mpAssetTypes.size());
        Assert.assertTrue(mpAssetTypes.contains(AssetType.MANAGED_PORTFOLIO));

        Collection<AssetType> tdAssetTypes = tradableAssetsDtoService.getAssetTypes(AssetType.TERM_DEPOSIT.getDisplayName());
        Assert.assertEquals(1, tdAssetTypes.size());
        Assert.assertTrue(tdAssetTypes.contains(AssetType.TERM_DEPOSIT));
    }
    
    @Test
    public final void testGetAssetTypes_whenMoreThanOneAssetTypes_thenCollection() {
        String assetTypeValues = (AssetType.SHARE.getDisplayName() + "," + AssetType.MANAGED_FUND.getDisplayName() + "," 
                + AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Collection<AssetType> assetTypes = tradableAssetsDtoService.getAssetTypes(assetTypeValues);
        Assert.assertEquals(5, assetTypes.size());
    }
}
