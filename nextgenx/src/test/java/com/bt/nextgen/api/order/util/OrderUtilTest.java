package com.bt.nextgen.api.order.util;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.service.avaloq.asset.ShareAssetImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.order.OrderType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderUtilTest {
    private ShareAssetImpl shareAsset;
    private AssetDto shareAssetDto;

    @Before
    public void setUp() throws Exception {
        shareAsset = new ShareAssetImpl();
        shareAsset.setAssetType(AssetType.SHARE);
        shareAssetDto = new AssetDto(shareAsset, "Bhp", AssetType.SHARE.getDisplayName());
    }

    @Test
    public void testOrderTypeForStexLSOrder() throws Exception {
        OrderType stexOrderType = OrderType.STEX_BUY;
        String orderType = OrderUtil.getOrderType(stexOrderType, shareAssetDto);
        Assert.assertEquals("Buy", orderType);
    }

    @Test
    public void testOrderTypeForNonStexLSOrder() throws Exception {
        OrderType partialRedemptionOrderType = OrderType.PARTIAL_REDEMPTION;
        String orderType = OrderUtil.getOrderType(partialRedemptionOrderType, shareAssetDto);
        Assert.assertEquals("Partial redemption", orderType);
    }
}
