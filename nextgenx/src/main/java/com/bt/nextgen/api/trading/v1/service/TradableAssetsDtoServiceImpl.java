package com.bt.nextgen.api.trading.v1.service;

import com.bt.nextgen.api.trading.v1.model.TermDepositInterestRateKey;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice.TermDepositTradeAssetService;
import com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice.TermDepositTradeAssetServiceV2;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
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
import java.util.List;
import java.util.Map;

/**
 * Suppressed squid:S1200 and squid:MethodCyclomaticComplexity as this class has already been broken up. squid:S1151 is the switch
 * statement which i don't think needs changing
 */
@SuppressWarnings({ "squid:S1200", "squid:S1151", "squid:MethodCyclomaticComplexity" })
@Service
public class TradableAssetsDtoServiceImpl implements TradableAssetsDtoService {
    public static final String ALL = "all";
    public static final String HELD = "held";

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
    private TermDepositTradeAssetServiceV2 termDepositTradeAssetServiceV2;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    private static final Logger logger = LoggerFactory.getLogger(TradableAssetsDtoServiceImpl.class);

    @Override
    public List<TradeAssetDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        logger.debug("TradableAssetsDtoService invoked with arguments  {}", Arrays.toString(criteriaList.toArray()));

        String accountId = null;
        String query = null;
        Collection<String> assetIds = new ArrayList<>();
        Collection<AssetType> assetTypes = null;
        boolean wholesalePlus = false;
        boolean assetTypeHeld = false;
        boolean filterAal = false;

        for (ApiSearchCriteria parameter : criteriaList) {
            switch (parameter.getProperty()) {
                case "accountId":
                    accountId = parameter.getValue();
                    break;
                case "query":
                    query = parameter.getValue().trim();
                    break;
                case "assetIds":
                    assetIds = Arrays.asList(StringUtils.split(parameter.getValue(), ","));
                    break;
                case "assetType":
                    if (HELD.equalsIgnoreCase(parameter.getValue())) {
                        assetTypeHeld = true;
                    } else if (TradableAssetsDtoServiceHelper.WHOLESALE_PLUS.equalsIgnoreCase(parameter.getValue())) {
                        wholesalePlus = true;
                        assetTypes = getAssetTypes(AssetType.MANAGED_FUND.getDisplayName());
                    } else if (!ALL.equalsIgnoreCase(parameter.getValue())) {
                        assetTypes = getAssetTypes(parameter.getValue());
                    }
                    break;
                case "filterAal":
                    filterAal = Boolean.valueOf(parameter.getValue());
                    break;
                default:
                    break;
            }
        }

        if (accountId == null) {
            throw new IllegalArgumentException("Unsupported search");
        }

        return getTradeAssetDtos(accountId, query, assetIds, assetTypes, wholesalePlus, assetTypeHeld, filterAal, serviceErrors);
    }

    @SuppressWarnings("squid:S00107")
    protected List<TradeAssetDto> getTradeAssetDtos(String accountId, String query, Collection<String> assetIds,
            Collection<AssetType> assetTypes, boolean wholesalePlus, boolean assetTypeHeld, boolean filterAal, ServiceErrors serviceErrors) {
        WrapAccountDetail account = tradableAssetsDtoServiceHelper.loadAccount(accountId, serviceErrors);
        BrokerKey brokerKey = tradableAssetsDtoServiceHelper.loadBroker(account, serviceErrors);
        ProductKey directProductKey = tradableAssetsDtoServiceHelper.loadDirectProductKey(account);
        WrapAccountValuation valuation = tradableAssetsDtoServiceHelper.loadValuation(accountId, serviceErrors);
        Map<String, Asset> valuationAssets = tradableAssetsDtoServiceHelper.getValuationAssets(valuation);
        ProductKey productKey = account.getProductKey();

        Map<String, Asset> filteredAssets = tradableAssetsDtoServiceHelper.getFilteredAssets(assetTypeHeld, assetIds, query,
                valuationAssets, assetTypes, wholesalePlus, serviceErrors);

        Map<String, List<DistributionMethod>> assetDistributionMethods = tradableAssetsDtoServiceHelper
                .loadDistributionMethods(filteredAssets.values());

        List<Asset> availableAssets = assetIntegrationService.loadAvailableAssets(brokerKey, directProductKey, serviceErrors);
        Map<String, Asset> filteredAvailableAssets = tradableAssetsDtoServiceFilter.filterAvailableAssetsList(availableAssets,
                filteredAssets);

        // Additional filter to limit the assets shown to Pilot/Staff/PIV/General users
        tradableAssetsDtoServiceFilter.filterAssetsForAdviserGroup(filteredAvailableAssets,
                tradableAssetsDtoServiceHelper.getAssetsToExclude(account, serviceErrors));

        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);

        List<TradeAssetDto> tradeAssetDtos;
        if (filterAal) {
            tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(filteredAvailableAssets, valuation, filteredAssets,
                assetTypeHeld, assetDistributionMethods, bankDate);
        } else {
            tradeAssetDtos = tradeAssetDtoConverter.toAssetDto(filteredAssets, valuation, filteredAssets, assetTypeHeld,
                    assetDistributionMethods, bankDate);
        }

        if (!assetTypeHeld && (assetTypes == null || assetTypes.contains(AssetType.TERM_DEPOSIT))) {
            // Retrieve Term Deposit Trade asset dtos and add them to existing tradeassetdtos
            boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl()).getFeatureToggle("termDepositToggle");
            if(termDepositToggle) {
                TermDepositInterestRateKey termDepositInterestRateKey = new TermDepositInterestRateKey(productKey,brokerKey,account.getAccountStructureType(),bankDate);
                tradeAssetDtos.addAll(termDepositTradeAssetServiceV2.loadTermDepositTradeAssets(assetIds, query, termDepositInterestRateKey,
                        availableAssets, filteredAvailableAssets,serviceErrors));
            } else {
            tradeAssetDtos.addAll(termDepositTradeAssetService.loadTermDepositTradeAssets(assetIds, query, brokerKey,
                    availableAssets, filteredAvailableAssets, serviceErrors));
            }
        }

        return tradeAssetDtos;
    }
    
    protected Collection<AssetType> getAssetTypes(String assetTypeValues) {
        Collection<AssetType> assetTypes = new ArrayList<>();
        String[] types = StringUtils.split(assetTypeValues, ",");
        for (String type : types) {
            AssetType assetType = AssetType.forDisplay(type);
            if (assetType != null) {
                assetTypes.add(assetType);
                if (AssetType.SHARE == assetType) {
                    assetTypes.add(AssetType.OPTION);
                    assetTypes.add(AssetType.BOND);
                }
            }
        }
        return assetTypes;
    }
}
