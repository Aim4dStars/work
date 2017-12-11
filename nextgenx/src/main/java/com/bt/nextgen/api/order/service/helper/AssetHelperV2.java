package com.bt.nextgen.api.order.service.helper;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.on;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.service.AssetDtoConverter;
import com.bt.nextgen.api.asset.service.AssetDtoConverterV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.order.OrderItem;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AssetHelperV2 {

    @Autowired
    protected BrokerIntegrationService brokerService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    protected AccountIntegrationService accountIntegrationService;

    @Autowired
    protected AssetDtoConverterV2 assetDtoConverter;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    protected AssetIntegrationService assetService;


    public Map<String, AssetDto> getAssetsForOrders(AccountKey accountKey, List<OrderItem> orders, ServiceErrors serviceErrors) {
        List<String> assetIds = new ArrayList<>();
        List<TermDepositInterestRate> termDepositInterestRates = new ArrayList<>();
        for (OrderItem order : orders) {
            assetIds.add(order.getAssetId());
        }

        WrapAccountDetail account = accountIntegrationService.loadWrapAccountDetail(accountKey, serviceErrors);
        BrokerKey adviserKey = account.getAdviserKey();
        Broker broker = brokerService.getBroker(adviserKey, serviceErrors);

        Map<String, Asset> assets = assetService.loadAssets(assetIds, serviceErrors);
        List<Asset> assetList = new ArrayList<>(assets.values());
        final List<String> assetIdList = Lambda.collect(assetList, on(Asset.class).getAssetId());
        TermDepositAssetRateSearchKey termDepositAssetRateSearchKey = new TermDepositAssetRateSearchKey(account.getProductKey(),broker.getDealerKey(),null,account.getAccountStructureType(),DateTime.now(),assetIdList);
        termDepositInterestRates = assetService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors);

        return assetDtoConverter.toAssetDto(assets, termDepositInterestRates);
    }
}
