package com.bt.nextgen.api.order.util;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.order.OrderType;

public final class OrderUtil {
    private OrderUtil() {

    }

    public static String getOrderType(OrderType outputOrderType, AssetDto assetDto) {
        String orderType = outputOrderType == null ? null : outputOrderType.getDisplayName();
        if (assetDto != null && AssetType.SHARE == AssetType.forDisplay(assetDto.getAssetType())
                && OrderType.STEX_BUY == outputOrderType) {
            orderType = OrderType.PURCHASE.getDisplayName();
        }
        return orderType;
    }
}
