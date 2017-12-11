package com.bt.nextgen.api.trading.v1.service.termdeposittradeassetservice;

import ch.lambdaj.Lambda;
import static ch.lambdaj.Lambda.filter;
import ch.lambdaj.function.convert.Converter;
import ch.lambdaj.function.convert.PropertyExtractor;
import com.bt.nextgen.api.trading.v1.model.TermDepositInterestRateKey;
import com.bt.nextgen.api.trading.v1.model.TradeAssetDto;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.TermDepositAssetBuilder;
import com.bt.nextgen.api.trading.v1.service.assetbuilder.TermDepositAssetBuilderV2;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceFilter;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.TermDepositAssetImpl;
import com.btfin.panorama.service.integration.account.Account;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.account.DistributionMethod;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.TermDepositAssetRateSearchKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TermDepositTradeAssetServiceV2 {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private TermDepositAssetBuilderV2 termDepositAssetBuilder;

    @Autowired
    private TradableAssetsDtoServiceFilter tradableAssetsDtoServiceFilter;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;


    @SuppressWarnings({"squid:S00107"})
    public List<TradeAssetDto> loadTermDepositTradeAssets(Collection<String> assetIds, String query, TermDepositInterestRateKey termDepositInterestRateKey,
                                                         List<Asset> availableAssets, Map<String, Asset> filteredAvailableAssets, ServiceErrors serviceErrors) {
        List<TermDepositInterestRate> tdAssetInterestRates = loadTermDepositAssetDetails(assetIds, query, termDepositInterestRateKey,
                availableAssets, filteredAvailableAssets,serviceErrors);
        return termDepositAssetBuilder.buildTradeAssets(tdAssetInterestRates, filteredAvailableAssets);
    }

    /**
     * Loads the term deposit asset details for a given query criteria
     */
    protected List<TermDepositInterestRate> loadTermDepositAssetDetails(Collection<String> assetIds, String query,
                                                                        TermDepositInterestRateKey termDepositInterestRateKey,List<Asset> availableAssets, Map<String, Asset> filteredAvailableAssets,
                                                                        ServiceErrors serviceErrors) {
        List<TermDepositInterestRate> termDepositInterestRateList =  new ArrayList<>();
        Map<String, Asset> tdAvailableAssets = new HashMap<>();

        if (assetIds == null || assetIds.isEmpty()) {
            // add all term deposit assets found from the term deposit rates cache
            termDepositInterestRateList.addAll(
                    assetIntegrationService.getTermDepositRatesForCriteria(termDepositInterestRateKey.getDealerGroupKey(), termDepositInterestRateKey.getWhiteLabelProductKey(), termDepositInterestRateKey.getAccountStructureType(), query,
                            termDepositInterestRateKey.getBankDate(), serviceErrors));

            List<String> tdAssetIds = Lambda.convert(termDepositInterestRateList, new Converter<TermDepositInterestRate, String>() {
                @Override
                public String convert(TermDepositInterestRate termDepositInterestRate) {

                    return termDepositInterestRate.getAssetKey().getId();
                }
            });

            tdAvailableAssets.putAll(tradableAssetsDtoServiceFilter.filterAvailableAssetsList(availableAssets,
                    assetIntegrationService.loadAssets(tdAssetIds, serviceErrors)));
        }

        // add all term deposit assets found from the asset cache
        if (filteredAvailableAssets != null && !filteredAvailableAssets.isEmpty()) {

            List<Asset> filteredTDAssets = filter(new LambdaMatcher<Asset>() {
                @Override
                protected boolean matchesSafely(Asset asset) {
                    return AssetType.TERM_DEPOSIT.equals(asset.getAssetType());
                }
            }, filteredAvailableAssets.values());

            List<String> assets = Lambda.convert(filteredTDAssets, new PropertyExtractor<Asset, String>(
                    "assetId"));

            TermDepositAssetRateSearchKey termDepositAssetRateSearchKey =  new TermDepositAssetRateSearchKey(termDepositInterestRateKey.getWhiteLabelProductKey(),termDepositInterestRateKey.getDealerGroupKey(),null,
                    termDepositInterestRateKey.getAccountStructureType(),termDepositInterestRateKey.getBankDate(),assets);
            termDepositInterestRateList.addAll(assetIntegrationService.loadTermDepositRates(termDepositAssetRateSearchKey, serviceErrors));
        }

        // should never be null, just placating sonar
        if (filteredAvailableAssets != null) {
            filteredAvailableAssets.putAll(tdAvailableAssets);
        }

        return termDepositInterestRateList;
    }
}