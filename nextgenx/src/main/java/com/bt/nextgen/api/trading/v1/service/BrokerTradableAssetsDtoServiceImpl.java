package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice.TermDepositTradeAssetService;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("squid:S1200")
public class BrokerTradableAssetsDtoServiceImpl implements BrokerTradableAssetsDtoService {
    private static final Logger logger = LoggerFactory.getLogger(BrokerTradableAssetsDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private TradeAssetDtoConverter tradeAssetDtoConverter;

    @Autowired
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Autowired
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Autowired
    private TermDepositTradeAssetService termDepositTradeAssetService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProductIntegrationService productService;

    @Override
    public List<TradeAssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        logger.debug("BrokerTradableAssetsDtoService invoked with arguments  {}", Arrays.toString(criteriaList.toArray()));

        String query = null;
        Collection<String> assetIds = new ArrayList<>();
        Collection<AssetType> assetTypes = new ArrayList<>();
        String productId = null;

        for (ApiSearchCriteria parameter : criteriaList) {
            switch (parameter.getProperty()) {
            case "productId":
                productId = parameter.getValue().trim();
                break;
            case "query":
                query = parameter.getValue().trim();
                break;
            case "assetIds":
                assetIds = Arrays.asList(StringUtils.split(parameter.getValue(), ","));
                break;
            case "assetType":
                assetTypes.addAll(getAssetTypes(parameter.getValue()));
                break;
            default:
                break;
            }
        }

        if (productId == null && assetTypes.contains(AssetType.INDEX)) {
            return getBenchmarkAssets(query, assetIds, serviceErrors);
        }

        return getTradeAssetDtos(ProductKey.valueOf(productId), query, assetIds, assetTypes, serviceErrors);
    }

    protected List<TradeAssetDto> getBenchmarkAssets(String query, Collection<String> assetIds, ServiceErrors serviceErrors) {

        Map<String, Asset> filteredAssets = tradableAssetsDtoServiceHelper.getFilteredAssets(false, assetIds, query, null,
                Collections.singleton(AssetType.INDEX), false, serviceErrors);

        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);

        Map<String, List<DistributionMethod>> assetDistributionMethods = Collections.emptyMap();
        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(filteredAssets, null, filteredAssets, false,
                assetDistributionMethods, bankDate);

        return tradeAssetDtos;
    }

    protected List<TradeAssetDto> getTradeAssetDtos(ProductKey productKey, String query, Collection<String> assetIds,
            Collection<AssetType> assetTypes, ServiceErrors serviceErrors) {

        Map<String, Asset> filteredAssets = tradableAssetsDtoServiceHelper.getFilteredAssets(false, assetIds, query, null,
                assetTypes, false, serviceErrors);

        Map<String, List<DistributionMethod>> assetDistributionMethods = tradableAssetsDtoServiceHelper
                .loadDistributionMethods(filteredAssets.values());
        //TODO - UPS REFACTOR1 - This needs to be changed to be account specific
        BrokerKey brokerKey = userProfileService.getDealerGroupBroker().getDealerKey();
        List<Asset> availableAssets = assetIntegrationService.loadAvailableAssets(brokerKey, productKey, serviceErrors);
        Map<String, Asset> filteredAvailableAssets = tradableAssetsDtoServiceFilter.filterAvailableAssetsList(availableAssets,
                filteredAssets);

        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);
        List<TradeAssetDto> tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(filteredAvailableAssets, null, filteredAssets,
                false, assetDistributionMethods, bankDate);

        return tradeAssetDtos;
    }

    protected Collection<AssetType> getAssetTypes(String strValue) {
        Collection<AssetType> assetTypes = new ArrayList<>();
        String[] types = StringUtils.split(strValue, "|");
        for (String type : types) {
            AssetType at = AssetType.valueOf(type);
            if (at != null) {
                assetTypes.add(at);
                if (AssetType.SHARE == at) {
                    assetTypes.add(AssetType.OPTION);
                    assetTypes.add(AssetType.BOND);
                }
            }
        }
        return assetTypes;
    }
}