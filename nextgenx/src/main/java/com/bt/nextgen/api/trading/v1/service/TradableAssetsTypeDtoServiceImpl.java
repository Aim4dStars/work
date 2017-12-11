package com.bt.nextgen.api.trading.v1.service;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.model.TradeAssetTypeDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TradableAssetsTypeDtoServiceImpl implements TradableAssetsTypeDtoService {
    @Autowired
    private TradeAssetDtoConverter tradeAssetDtoConverter;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Override
    public List<TradeAssetTypeDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        String accountId = null;
        BrokerKey brokerKey = null;

        for (ApiSearchCriteria parameter : criteriaList) {
            if ("accountId".equals(parameter.getProperty())) {
                accountId = parameter.getValue();
            }
        }

        if (accountId == null) {
            throw new IllegalArgumentException("Unsupported search");
        }

        WrapAccountDetail account = tradableAssetsDtoServiceHelper.loadAccount(accountId, serviceErrors);
        brokerKey = tradableAssetsDtoServiceHelper.loadBroker(account, serviceErrors);
        ProductKey directProductKey = tradableAssetsDtoServiceHelper.loadDirectProductKey(account);

        Map<String, Asset> availableAssets = getAssetMap(
                assetService.loadAvailableAssets(brokerKey, directProductKey, serviceErrors));

        WrapAccountValuation valuation = tradableAssetsDtoServiceHelper.loadValuation(accountId, serviceErrors);
        Map<String, Asset> valuationAssets = tradableAssetsDtoServiceHelper.getValuationAssets(valuation);

        Map<String, Asset> assets = new HashMap<>();
        assets.putAll(availableAssets);
        assets.putAll(valuationAssets);

        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);

        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(availableAssets, valuation, assets, false,
                new HashMap<String, List<DistributionMethod>>(), bankDate);

        return tradeAssetDtoConverter.toTradeAssetTypeDtos(tradeAssetDtos, availableAssets);
    }

    private Map<String, Asset> getAssetMap(List<Asset> assets) {
        Map<String, Asset> assetMap = Lambda.map(assets, new Converter<Asset, String>() {
            @Override
            public String convert(Asset asset) {
                return asset.getAssetId();
            }
        });
        return assetMap;
    }
}
