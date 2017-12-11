package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.trading.v1.model.TermDepositTradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
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
public class TradableAssetsTypeDtoServiceTest {

    @InjectMocks
    private TradableAssetsTypeDtoServiceImpl tradableAssetsTypeDtoService;

    @Mock
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Mock
    private TradeAssetDtoConverter tradeAssetDtoConverter;

    @Mock
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Mock
    BankDateIntegrationService bankDateIntegrationService;

    List<ApiSearchCriteria> criteriaList;

    private List<Asset> brokerProductAssets;
    private Map<String, Asset> availableAssets;
    private WrapAccountValuation valuation;
    List<TradeAssetTypeDto> tradeAssetTypeDtos;
    TradeAssetTypeDto tradeAssetTypeDto;

    private Map<String, Asset> assetMap;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, "accountId",
                OperationType.STRING);
        criteriaList = new ArrayList<>();
        criteriaList.add(accountIdCriteria);

        valuation = Mockito.mock(WrapAccountValuation.class);

        Asset openAsset = Mockito.mock(Asset.class);
        Mockito.when(openAsset.getAssetId()).thenReturn("openAsset");
        Mockito.when(openAsset.getStatus()).thenReturn(AssetStatus.OPEN);
        assetMap = new HashMap<String, Asset>();

        brokerProductAssets = new ArrayList<>();
        brokerProductAssets.add(openAsset);

        assetMap.put(openAsset.getAssetId(), openAsset);

        availableAssets = new HashMap<>();
        availableAssets.put(openAsset.getAssetId(), openAsset);

        tradeAssetTypeDtos = new ArrayList<>();
        tradeAssetTypeDto = new TradeAssetTypeDto(AssetType.SHARE.getDisplayName(), AssetType.SHARE.getGroupDescription());
        tradeAssetTypeDtos.add(tradeAssetTypeDto);

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

        Mockito.when(assetIntegrationService.loadAvailableAssets(Mockito.any(BrokerKey.class), Mockito.any(ProductKey.class),
                Mockito.any(ServiceErrors.class))).thenReturn(brokerProductAssets);

        List<TradeAssetDto> termDepositTradeAssetDtoList = new ArrayList<>();
        TermDepositTradeAssetDto termDepositTradeAssetDto = Mockito.mock(TermDepositTradeAssetDto.class);
        termDepositTradeAssetDtoList.add(termDepositTradeAssetDto);

        Mockito.when(tradableAssetsDtoServiceHelper.loadValuation(Mockito.anyString(), Mockito.any(ServiceErrors.class)))
                .thenReturn(valuation);

        Mockito.when(tradableAssetsDtoServiceHelper.getValuationAssets(Mockito.any(WrapAccountValuation.class)))
                .thenReturn(new HashMap<String, Asset>());

        Mockito.when(tradeAssetDtoConverter.toAssetDto(Mockito.anyMapOf(String.class, Asset.class),
                Mockito.any(WrapAccountValuation.class), Mockito.anyMapOf(String.class, Asset.class), Mockito.anyBoolean(),
                Mockito.anyMap(), Mockito.any(DateTime.class))).thenReturn(new ArrayList<TradeAssetDto>());

        Mockito.when(tradeAssetDtoConverter.toTradeAssetTypeDtos(Mockito.anyListOf(TradeAssetDto.class),
                Mockito.anyMapOf(String.class, Asset.class))).thenReturn(tradeAssetTypeDtos);

        Mockito.when(bankDateIntegrationService.getBankDate(Mockito.any(ServiceErrors.class))).thenReturn(DateTime.now());
    }

    @Test
    public final void testSearch() {
        List<TradeAssetTypeDto> tradeAssetTypeDtos = tradableAssetsTypeDtoService.search(criteriaList, new FailFastErrorsImpl());

        Assert.assertEquals(1, tradeAssetTypeDtos.size());
        Assert.assertEquals(AssetType.SHARE.getDisplayName(), tradeAssetTypeDtos.get(0).getValue());
    }
}
