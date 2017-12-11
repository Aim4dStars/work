package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetCountDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;

@RunWith(MockitoJUnitRunner.class)
public class TradableAssetsCountDtoServiceTest {

    @InjectMocks
    private TradableAssetsCountDtoServiceImpl tradableAssetsCountDtoService;

    @Mock
    TradableAssetsDtoService tradableAssetsDtoService;

    List<ApiSearchCriteria> criteriaList;
    List<TradeAssetDto> tradeAssetDtos;
    ServiceErrors serviceErrors;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        serviceErrors = new ServiceErrorsImpl();
        ApiSearchCriteria accountIdCriteria = new ApiSearchCriteria("accountId", SearchOperation.EQUALS, "accountId",
                OperationType.STRING);
        criteriaList = new ArrayList<>();
        criteriaList.add(accountIdCriteria);

        TradeAssetDto managedFundTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedFundAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedFundTradeAssetDto.getAsset()).thenReturn(managedFundAsset);
        Mockito.when(managedFundTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.MANAGED_FUND.getDisplayName());

        TradeAssetDto managedPortfolioTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto managedPortfolioAsset = Mockito.mock(AssetDto.class);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset()).thenReturn(managedPortfolioAsset);
        Mockito.when(managedPortfolioTradeAssetDto.getAsset().getAssetType())
                .thenReturn(AssetType.MANAGED_PORTFOLIO.getDisplayName());

        TradeAssetDto shareTradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto shareAsset = Mockito.mock(AssetDto.class);
        Mockito.when(shareTradeAssetDto.getAsset()).thenReturn(shareAsset);
        Mockito.when(shareTradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.SHARE.getDisplayName());

        TradeAssetDto tradeAssetDto = Mockito.mock(TradeAssetDto.class);
        AssetDto assetDto = Mockito.mock(AssetDto.class);
        Mockito.when(tradeAssetDto.getAsset()).thenReturn(assetDto);
        Mockito.when(tradeAssetDto.getAsset().getAssetType()).thenReturn(AssetType.TERM_DEPOSIT.getDisplayName());


        tradeAssetDtos = new ArrayList<>();
        tradeAssetDtos.add(tradeAssetDto);
        tradeAssetDtos.add(tradeAssetDto);
        tradeAssetDtos.add(managedFundTradeAssetDto);
        tradeAssetDtos.add(managedPortfolioTradeAssetDto);
        tradeAssetDtos.add(shareTradeAssetDto);

        Mockito.when(tradableAssetsDtoService.search(anyList(), (ServiceErrors) anyObject())).thenReturn(tradeAssetDtos);
    }

    @Test
    public final void testSearch() {
        List<TradeAssetCountDto> result = tradableAssetsCountDtoService.search(criteriaList, serviceErrors);
        Assert.assertEquals(13, result.size());
        for (TradeAssetCountDto assetCountDto : result){
            if(assetCountDto.getAssetType().equals("Managed fund")){
                Assert.assertEquals(1, assetCountDto.getCount());
            }
            if(assetCountDto.getAssetType().equals("Managed portfolio")){
                Assert.assertEquals(1, assetCountDto.getCount());
            }
            if(assetCountDto.getAssetType().equals("Listed security")){
                Assert.assertEquals(1, assetCountDto.getCount());
            }
            if(assetCountDto.getAssetType().equals("Term deposit")){
                Assert.assertEquals(2, assetCountDto.getCount());
            }
        }
    }
}
