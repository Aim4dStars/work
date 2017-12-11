package com.bt.nextgen.service.avaloq.regularinvestment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.order.OrderItemImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;

@Service
public class OrderItemInitialiser {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    protected AssetIntegrationService assetService;

    public void initOrderItem(RegularInvestment regularInvestment, ServiceErrors serviceErrors) {
        List<String> assetIds = new ArrayList<>();
        for (OrderItem order : regularInvestment.getOrders()) {
            assetIds.add(order.getAssetId());
        }
        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);

        for (OrderItem order : regularInvestment.getOrders()) {
            Asset asset = assets.get(order.getAssetId());
            OrderItemImpl item = (OrderItemImpl) order;
            item.setAssetType(asset.getAssetType());
        }
    }

}
