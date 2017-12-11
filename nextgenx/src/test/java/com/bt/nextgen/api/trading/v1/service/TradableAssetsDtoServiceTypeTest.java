package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class TradableAssetsDtoServiceTypeTest {

    @InjectMocks
    private TradableAssetsTypeDtoServiceImpl tradableAssetsTypeDtoService;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Mock
    private TradeAssetDtoConverter tradeAssetDtoConverter;

    @Mock
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Mock
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Mock
    BankDateIntegrationService bankDateIntegrationService;

    List<ApiSearchCriteria> criteriaList;

    private Map<String, Asset> filterAssetsForCriteria;
    private Map<String, Asset> availableAssets;

    private WrapAccountValuation valuation;
    List<TradeAssetDto> tradeAssetDtos;

    private Map<String, Asset> assetMap;

    private List<TradeAssetTypeDto> tradeAssetTypeDtos;

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

        availableAssets = null;

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

        TradeAssetDto managedFundTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedFundAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedFundTradeAssetDto.getAsset()).thenReturn(managedFundAsset);
        Mockito.when(managedFundTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.MANAGED_FUND.getDisplayName());
        Mockito.when(managedFundAsset.getAssetName()).thenReturn("managedFund");

        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAsset);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset().getAssetType())
                .thenReturn(AssetType.MANAGED_PORTFOLIO.getDisplayName());
        Mockito.when(managedPortfolioAsset.getAssetName()).thenReturn("managedPortfolio");

        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto shareAsset = Mockito.mock(AssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAsset);
        Mockito.when(shareTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.SHARE.getDisplayName());
        Mockito.when(shareAsset.getAssetName()).thenReturn("shareAsset");

        TradeAssetDto tradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(tradeAssetDto.getAsset()).thenReturn(assetDto);
        Mockito.when(assetDto.getAssetId()).thenReturn("tradeAssetId");

        tradeAssetDtos = new ArrayList<>();
        tradeAssetDtos.add(tradeAssetDto);

        tradeAssetDtos.add(managedFundTradeAssetDto);
        tradeAssetDtos.add(managedPortfolioTradeAssetDto);
        tradeAssetDtos.add(shareTradeAssetDto);

        TradeAssetTypeDto managedFundTypeDto = Mockito.mock(TradeAssetTypeDto.class);
        Mockito.when(managedFundTypeDto.getLabel()).thenReturn(AssetType.MANAGED_FUND.getGroupDescription());
        Mockito.when(managedFundTypeDto.getValue()).thenReturn(AssetType.MANAGED_FUND.getDisplayName());

        TradeAssetTypeDto managedPortfolioTypeDto = Mockito.mock(TradeAssetTypeDto.class);
        Mockito.when(managedPortfolioTypeDto.getLabel()).thenReturn(AssetType.MANAGED_PORTFOLIO.getGroupDescription());
        Mockito.when(managedPortfolioTypeDto.getValue()).thenReturn(AssetType.MANAGED_PORTFOLIO.getDisplayName());

        TradeAssetTypeDto shareTypeDto = Mockito.mock(TradeAssetTypeDto.class);
        Mockito.when(shareTypeDto.getLabel()).thenReturn(AssetType.SHARE.getGroupDescription());
        Mockito.when(shareTypeDto.getValue()).thenReturn(AssetType.SHARE.getGroupDescription());

        tradeAssetTypeDtos = new ArrayList<>();

        tradeAssetTypeDtos.add(managedFundTypeDto);
        tradeAssetTypeDtos.add(managedPortfolioTypeDto);
        tradeAssetTypeDtos.add(shareTypeDto);

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(DateTime.now());
    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.asset.service.TradableAssetsDtoServiceImpl#search(java.util.List, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testSearch() {

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

        Mockito.when(assetService.loadAvailableAssets(Mockito.mock(BrokerKey.class), Mockito.mock(ProductKey.class),
                Mockito.mock(ServiceErrors.class))).thenReturn(new ArrayList(availableAssets.values()));

        Mockito.when(tradableAssetsDtoServiceHelper.loadValuation(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(valuation);

        Mockito.when(tradeAssetDtoConverter.toAssetDto(Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(WrapAccountValuation.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyBoolean(),
                Mockito.anyMap(), Mockito.any(DateTime.class))).thenReturn(tradeAssetDtos);

        Mockito.when(tradeAssetDtoConverter.toTradeAssetTypeDtos(Mockito.anyListOf(TradeAssetDto.class), Mockito.anyMap()))
                .thenReturn(tradeAssetTypeDtos);

        List<TradeAssetTypeDto> returnedTradeAssetDtos = tradableAssetsTypeDtoService.search(criteriaList,
                new FailFastErrorsImpl());

        Assert.assertEquals(3, returnedTradeAssetDtos.size());
        Assert.assertEquals("Managed funds", returnedTradeAssetDtos.get(0).getLabel());
        Assert.assertEquals("Managed fund", returnedTradeAssetDtos.get(0).getValue());

        Assert.assertEquals("Managed portfolios", returnedTradeAssetDtos.get(1).getLabel());
        Assert.assertEquals("Managed portfolio", returnedTradeAssetDtos.get(1).getValue());

        Assert.assertEquals("Listed securities", returnedTradeAssetDtos.get(2).getLabel());
        Assert.assertEquals("Listed securities", returnedTradeAssetDtos.get(2).getValue());

    }
}
