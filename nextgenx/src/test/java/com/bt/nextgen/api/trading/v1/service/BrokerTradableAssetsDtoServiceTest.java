package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrokerTradableAssetsDtoServiceTest {

    @InjectMocks
    private BrokerTradableAssetsDtoServiceImpl tradableAssetsDtoService;

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
    BankDateIntegrationService bankDateIntegrationService;

    @Mock
    UserProfileService userProfileService;

    List<ApiSearchCriteria> criteriaList;

    private Broker adviser;
    private Map<String, Asset> filterAssetsForCriteria;
    private List<String> brokerProductAssets;
    private Map<String, Asset> availableAssets;

    List<TradeAssetDto> tradeAssetDtos;

    private Map<String, Asset> assetMap;
    private Map<String, List<DistributionMethod>> assetDistributionMethods;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

        filterAssetsForCriteria = new HashMap<String, Asset>();

        ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, "all", OperationType.STRING);
        ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, "SHARE|MANAGED_FUND",
                OperationType.STRING);

        criteriaList = new ArrayList<>();
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);

        adviser = Mockito.mock(Broker.class);
        brokerProductAssets = null;
        availableAssets = null;
        tradeAssetDtos = null;

        Asset suspendedAsset = Mockito.mock(Asset.class);
        Mockito.when(suspendedAsset.getAssetId()).thenReturn("suspendedAsset");
        Mockito.when(suspendedAsset.getStatus()).thenReturn(AssetStatus.SUSPENDED);
        Mockito.when(suspendedAsset.getAssetType()).thenReturn(AssetType.SHARE);

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

        Mockito.when(
                assetIntegrationService.loadAssetsForCriteria(Mockito.anyCollection(), Mockito.anyString(),
                        Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(filterAssetsForCriteria);

        Mockito.when(
                assetIntegrationService.loadAvailableAssetsForBrokerAndProduct(Mockito.any(BrokerKey.class),
                        Mockito.any(ProductKey.class), Mockito.any(ServiceErrors.class))).thenReturn(brokerProductAssets);

        Mockito.when(
                tradableAssetsDtoServiceFilter.filterAvailableAssetsList(Mockito.anyListOf(Asset.class),
                        Mockito.anyMapOf(String.class, Asset.class))).thenReturn(availableAssets);

        List<TradeAssetDto> termDepositTradeAssetDtoList = new ArrayList<>();
        TermDepositTradeAssetDto termDepositTradeAssetDto = Mockito.mock(TermDepositTradeAssetDto.class);
        termDepositTradeAssetDtoList.add(termDepositTradeAssetDto);

        Mockito.when(tradableAssetsDtoServiceHelper.loadDistributionMethods(Mockito.anyCollection())).thenReturn(
                assetDistributionMethods);

        Mockito.when(
                tradeAssetDtoConverter.toAssetDto(Mockito.anyMapOf(String.class, Asset.class),
                        Mockito.any(WrapAccountValuation.class), Mockito.anyMapOf(String.class, Asset.class),
                        Mockito.anyBoolean(), Mockito.anyMap(), Mockito.any(DateTime.class))).thenReturn(tradeAssetDtos);

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(DateTime.now());

        Broker broker = Mockito.mock(Broker.class);
        when(broker.getKey()).thenReturn(BrokerKey.valueOf("testUser"));
        Mockito.when(userProfileService.getDealerGroupBroker()).thenReturn(broker);
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.BrokerTradableAssetsDtoServiceImpl#search(java.util.List, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testSearchBrokerProducts() {
        List<TradeAssetDto> returnedTradeAssetDtos = tradableAssetsDtoService.search(criteriaList, new FailFastErrorsImpl());

        Assert.assertEquals(1, returnedTradeAssetDtos.size());
        Assert.assertEquals("tradeAssetId", returnedTradeAssetDtos.get(0).getAsset().getAssetId());
    }

    @Test
    public final void testBenchmarkSearch() {

        Asset indexAsset = Mockito.mock(Asset.class);
        Mockito.when(indexAsset.getAssetId()).thenReturn("indexAssetId");
        Mockito.when(indexAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        Mockito.when(indexAsset.getAssetType()).thenReturn(AssetType.INDEX);
        assetMap = new HashMap<String, Asset>();

        assetMap.put(indexAsset.getAssetId(), indexAsset);

        Map<String, Asset> filterAssets = new HashMap<String, Asset>();
        filterAssets.put(indexAsset.getAssetId(), indexAsset);

        Mockito.when(
                assetIntegrationService.loadAssetsForCriteria(Mockito.anyCollection(), Mockito.anyString(),
                        Mockito.anyCollection(), Mockito.any(ServiceErrors.class))).thenReturn(filterAssets);

        ApiSearchCriteria queryCriteria = new ApiSearchCriteria("query", SearchOperation.EQUALS, "all", OperationType.STRING);
        ApiSearchCriteria assetTypeCriteria = new ApiSearchCriteria("assetType", SearchOperation.EQUALS, "INDEX",
                OperationType.STRING);
        criteriaList = new ArrayList<>();
        criteriaList.add(queryCriteria);
        criteriaList.add(assetTypeCriteria);

        List<TradeAssetDto> tradeAssetDtos = new ArrayList<>();
        TradeAssetDto tradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(tradeAssetDto.getAsset()).thenReturn(assetDto);
        Mockito.when(assetDto.getAssetId()).thenReturn("indexAssetId");
        tradeAssetDtos.add(tradeAssetDto);
        Mockito.when(
                tradeAssetDtoConverter.toAssetDto(Mockito.anyMapOf(String.class, Asset.class),
                        Mockito.any(WrapAccountValuation.class), Mockito.anyMapOf(String.class, Asset.class),
                        Mockito.anyBoolean(), Mockito.anyMap(), Mockito.any(DateTime.class))).thenReturn(tradeAssetDtos);

        List<TradeAssetDto> returnedTradeAssetDtos = tradableAssetsDtoService.search(criteriaList, new FailFastErrorsImpl());
        Assert.assertEquals("indexAssetId", returnedTradeAssetDtos.get(0).getAsset().getAssetId());
    }
}
