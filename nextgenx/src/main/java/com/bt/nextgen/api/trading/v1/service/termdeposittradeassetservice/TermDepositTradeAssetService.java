package com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.TermDepositAssetBuilder;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TermDepositTradeAssetService {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private TermDepositAssetBuilder termDepositAssetBuilder;

    @Autowired
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    public List<TradeAssetDto> loadTermDepositTradeAssets(Collection<String> assetIds, String query, BrokerKey brokerKey,
            List<Asset> availableAssets, Map<String, Asset> filteredAvailableAssets, ServiceErrors serviceErrors) {
        Map<String, TermDepositAssetDetail> tdAssetDetailMap = loadTermDepositAssetDetails(assetIds, query, brokerKey,
                availableAssets, filteredAvailableAssets, serviceErrors);
        return termDepositAssetBuilder.buildTradeAssets(tdAssetDetailMap, filteredAvailableAssets);
    }

    /**
     * Loads the term deposit asset details for a given query criteria
     */
    protected Map<String, TermDepositAssetDetail> loadTermDepositAssetDetails(Collection<String> assetIds, String query,
            BrokerKey brokerKey, List<Asset> availableAssets, Map<String, Asset> filteredAvailableAssets,
            ServiceErrors serviceErrors) {
        Map<String, TermDepositAssetDetail> tdAssetDetailMap = new HashMap<>();
        Map<String, Asset> tdAvailableAssets = new HashMap<>();

        DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);

        if (assetIds == null || assetIds.isEmpty()) {
            // add all term deposit assets found from the term deposit rates cache
            tdAssetDetailMap.putAll(
                    assetIntegrationService.loadTermDepositRatesForCriteria(brokerKey, query, bankDate, serviceErrors));

            List<String> tdAssetIds = Lambda.convert(tdAssetDetailMap,
                    new PropertyExtractor<TermDepositAssetDetail, String>("assetId"));
            tdAvailableAssets.putAll(tradableAssetsDtoServiceFilter.filterAvailableAssetsList(availableAssets,
                    assetIntegrationService.loadAssets(tdAssetIds, serviceErrors)));
        }

        // add all term deposit assets found from the asset cache
        if (filteredAvailableAssets != null && !filteredAvailableAssets.isEmpty()) {
            tdAssetDetailMap.putAll(assetIntegrationService.loadTermDepositRates(brokerKey, bankDate,
                    (List<Asset>) Lambda.collect(filteredAvailableAssets.values()), serviceErrors));
        }

        // should never be null, just placating sonar
        if (filteredAvailableAssets != null) {
            filteredAvailableAssets.putAll(tdAvailableAssets);
        }

        return tdAssetDetailMap;
    }
}
