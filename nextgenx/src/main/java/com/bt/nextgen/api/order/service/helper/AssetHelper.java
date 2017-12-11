package com.bt.nextgen.api.order.service.helper;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.order.OrderItem;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("AssetHelperV0.1")
public class AssetHelper {

    @Autowired
    protected BrokerIntegrationService brokerService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    protected AccountIntegrationService accountIntegrationService;

    @Autowired
    protected AssetDtoConverter assetDtoConverter;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    protected AssetIntegrationService assetService;

    public Map<String, AssetDto> getAssetsForOrders(AccountKey accountKey, List<OrderItem> orders, ServiceErrors serviceErrors) {
        List<String> assetIds = new ArrayList<>();
        for (OrderItem order : orders) {
            assetIds.add(order.getAssetId());
        }

        WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        BrokerKey adviserKey = account.getAdviserKey();
        Broker broker = brokerService.getBroker(adviserKey, serviceErrors);

        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);
        List<Asset> assetList = new ArrayList<>(assets.values());
        Map<String, TermDepositAssetDetail> termDepositAssetDetails = assetService.loadTermDepositRates(broker.getDealerKey(),
                DateTime.now(), assetList, serviceErrors);

        return assetDtoConverter.toAssetDto(assets, termDepositAssetDetails);
    }
}
