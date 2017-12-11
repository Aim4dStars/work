package com.bt.nextgen.api.trading.v1.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetStatus;

@Component
public class TradableAssetsDtoServiceFilter {
    /**
     * filter the available assets to those that match the query criteria
     * 
     * @return
     */
    public Map<String, Asset> filterAvailableAssetsList(List<Asset> availableAssets, final Map<String, Asset> filteredAssets) {
        Map<String, Asset> filteredAvailableAssets = new HashMap<>();
        for (Asset asset : availableAssets) {
            if (filteredAssets.containsKey(asset.getAssetId())) {
                filteredAvailableAssets.put(asset.getAssetId(), asset);
            }
        }

        return filteredAvailableAssets;
    }

    public void filterAssetsForAdviserGroup(Map<String, Asset> filteredAvailableAssets, final List<String> assetsToExclude) {
        if (CollectionUtils.isNotEmpty(assetsToExclude)) {
            for (String assetId : assetsToExclude) {
                if (filteredAvailableAssets.containsKey(assetId)) {
                    filteredAvailableAssets.remove(assetId);
                }
            }
        }
    }

    public boolean isAssetSellable(Asset asset, BigDecimal availableAmount, DateTime bankDate) {
        if (isAssetStatusSellable(asset) && isAssetDateTradable(asset, bankDate) && isValidAmount(availableAmount)
                && !asset.isPrepayment()) {
            return true;
        }
        return false;
    }

    protected boolean isAssetStatusSellable(Asset asset) {
        if (AssetStatus.TERMINATED != asset.getStatus() && AssetStatus.DELISTED != asset.getStatus()
                && AssetStatus.SUSPENDED != asset.getStatus()) {
            return true;
        }
        return false;
    }

    public boolean isAssetBuyable(Asset asset, boolean isHeld, DateTime bankDate) {
        if (isAssetStatusBuyable(asset, isHeld) && isAssetDateTradable(asset, bankDate) && !asset.isPrepayment()) {
            return true;
        }
        return false;
    }

    protected boolean isAssetStatusBuyable(Asset asset, boolean isHeld) {
        if (asset.getStatus() == null || asset.getStatus() == AssetStatus.OPEN
                || (asset.getStatus() == AssetStatus.CLOSED_TO_NEW && isHeld)) {
            return true;
        }
        return false;
    }

    protected boolean isAssetDateTradable(Asset asset, DateTime bankDate) {
        if (asset.getStartDate() == null && asset.getEndDate() == null) {
            return true;
        }

        if ((asset.getStartDate() == null || !bankDate.isBefore(asset.getStartDate()))
                && (asset.getEndDate() == null || !bankDate.isAfter(asset.getEndDate()))) {
            return true;
        }

        return false;
    }

    protected boolean isValidAmount(BigDecimal availableAmount) {
        if (availableAmount != null && availableAmount.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        return false;
    }
}
