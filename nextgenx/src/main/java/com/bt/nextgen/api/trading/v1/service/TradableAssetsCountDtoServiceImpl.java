package com.bt.nextgen.api.trading.v1.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.trading.v1.model.TradeAssetCountDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TradableAssetsCountDtoServiceImpl implements TradableAssetsCountDtoService {

    @Autowired
    private TradableAssetsDtoService tradableAssetsDtoService;

    /**
     * This method returns the list of assets and their count based on the search criteria
     *
     * @param criteriaList list of search/filter criteria
     * @return
     */
    @Override
    public List<TradeAssetCountDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        // This implementation might change later when we have proper filter requirements
        List<TradeAssetDto> availableTradeAssets = tradableAssetsDtoService.search(criteriaList, serviceErrors);
        return getAssetCount(availableTradeAssets);
    }

    private List<TradeAssetCountDto> getAssetCount(List<TradeAssetDto> availableTradeAssets) {
        List<TradeAssetCountDto> tradeAssetCountDtoList = new ArrayList<>();
        List<String> assetTypeList = Lambda.extract(availableTradeAssets, Lambda.on(TradeAssetDto.class).getAsset().getAssetType());
        for (AssetType assetType : AssetType.values()) {
                tradeAssetCountDtoList.add(new TradeAssetCountDto(assetType.getDisplayName(),
                        Collections.frequency(assetTypeList, assetType.getDisplayName())));
        }
        return tradeAssetCountDtoList;
    }
}
