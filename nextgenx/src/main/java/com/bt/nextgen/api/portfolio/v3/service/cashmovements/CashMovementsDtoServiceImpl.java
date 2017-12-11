package com.bt.nextgen.api.portfolio.v3.service.cashmovements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.btfin.panorama.core.concurrent.AbstractConcurrentComplete;
import com.btfin.panorama.core.concurrent.Concurrent;
import com.btfin.panorama.core.concurrent.ConcurrentCallable;
import com.btfin.panorama.core.concurrent.ConcurrentResult;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.portfolio.v3.model.cashmovements.CashMovementsDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ParameterisedDatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.service.TermDepositPresentationService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.PortfolioIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountIntegrationServiceFactory;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.income.HoldingIncomeDetails;
import com.bt.nextgen.service.integration.income.IncomeIntegrationService;
import com.bt.nextgen.service.integration.income.SubAccountIncomeDetails;
import com.bt.nextgen.service.integration.income.WrapAccountIncomeDetails;
import com.bt.nextgen.service.integration.portfolio.cashmovements.CashMovement;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;

@Service("CashMovementsDtoServiceV3")
@Transactional(value = "springJpaTransactionManager")
@SuppressWarnings("squid:S1200") // Concurrency
public class CashMovementsDtoServiceImpl implements CashMovementsDtoService {
    @Autowired
    private PortfolioIntegrationServiceFactory portfolioIntegrationServiceFactory;

    @Autowired
    private AccountIntegrationServiceFactory accountIntegrationServiceFactory;

    @Autowired
    private IncomeIntegrationService incomeIntegrationService;

    @Autowired
    private AssetIntegrationService assetIntegrationService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    @Override
    public CashMovementsDto find(DatedValuationKey key, ServiceErrors serviceErrors) {
        BuildDtoProcess resultProcess = new BuildDtoProcess(key, serviceErrors);
        Concurrent
                .when(loadAccountDetail(key, serviceErrors), loadPortfolioValuation(key, serviceErrors),
                        loadCashMovement(key, serviceErrors), loadCashDividends(key, serviceErrors))
                .done(resultProcess).execute();
        return resultProcess.getDto();
    }

    private ConcurrentCallable<WrapAccountDetail> loadAccountDetail(final DatedValuationKey key,
                                                                    final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<WrapAccountDetail>() {
            @Override
            public WrapAccountDetail call() {
                AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
                return accountIntegrationServiceFactory.getInstance(getServiceType(key)).loadWrapAccountDetail(accountKey, serviceErrors);
            }
        };
    }

    private ConcurrentCallable<WrapAccountValuation> loadPortfolioValuation(final DatedValuationKey key,
                                                                            final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<WrapAccountValuation>() {
            @Override
            public WrapAccountValuation call() {
                AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
                return portfolioIntegrationServiceFactory.getInstance(getServiceType(key))
                                                         .loadWrapAccountValuation(accountKey, key.getEffectiveDate(), false,
                                                                 serviceErrors);
            }
        };
    }

    private ConcurrentCallable<Collection<CashMovement>> loadCashMovement(final DatedValuationKey key,
                                                                          final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<Collection<CashMovement>>() {
            @Override
            public Collection<CashMovement> call() {
                AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
                return portfolioIntegrationServiceFactory.getInstance(getServiceType(key))
                                                         .loadCashMovement(accountKey, key.getEffectiveDate(), serviceErrors);
            }
        };
    }

    private ConcurrentCallable<List<HoldingIncomeDetails>> loadCashDividends(final DatedValuationKey key,
                                                                             final ServiceErrors serviceErrors) {
        return new ConcurrentCallable<List<HoldingIncomeDetails>>() {
            @Override
            public List<HoldingIncomeDetails> call() {
                AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
                List<HoldingIncomeDetails> result = new ArrayList<>();
                List<WrapAccountIncomeDetails> incomes = incomeIntegrationService.loadIncomeAccruedDetails(accountKey,
                        key.getEffectiveDate(), serviceErrors);
                for (WrapAccountIncomeDetails income : incomes) {
                    List<SubAccountIncomeDetails> subaccountIncomes = income.getSubAccountIncomeDetailsList();
                    for (SubAccountIncomeDetails subaccountIncome : subaccountIncomes) {
                        if (subaccountIncome.isDirect()) {
                            result.addAll(subaccountIncome.getIncomes());
                        }
                    }
                }
                return result;
            }
        };
    }

    private Map<AssetKey, TermDepositPresentation> getTermDepositsFromMovements(DatedValuationKey key,
                                                                                Map<Pair<String, DateTime>, Asset> assetMap,
                                                                                ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        Map<AssetKey, TermDepositPresentation> tds = new HashMap<>();
        for (Entry<Pair<String, DateTime>, Asset> asset : assetMap.entrySet()) {
            if (((Asset) asset.getValue()).getAssetType() == AssetType.TERM_DEPOSIT) {
                Pair<String, DateTime> assetPair = asset.getKey();
                if (tds.get(AssetKey.valueOf(assetPair.getLeft())) == null) {
                    tds.put(AssetKey.valueOf(assetPair.getLeft()), termDepositPresentationService.getTermDepositPresentation(
                            accountKey, assetPair.getLeft(), serviceErrors));
                }
            }
        }
        return tds;
    }

    private Map<AssetKey, TermDepositPresentation> getTermDepositsFromIncomes(DatedValuationKey key,
                                                                              List<HoldingIncomeDetails> incomes,
                                                                              ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        Map<AssetKey, TermDepositPresentation> tds = new HashMap<>();
        for (HoldingIncomeDetails income : incomes) {
            if (income.getAsset().getAssetType() == AssetType.TERM_DEPOSIT) {
                tds.put(AssetKey.valueOf(income.getAsset().getAssetId()), termDepositPresentationService
                        .getTermDepositPresentation(accountKey, income.getAsset().getAssetId(), serviceErrors));
            }
        }
        return tds;
    }

    private Map<Pair<String, DateTime>, Asset> getMovementAssets(Collection<CashMovement> movements,
                                                                 ServiceErrors serviceErrors) {
        Map<DateTime, Set<String>> assetSetMap = new HashMap<>();
        Map<Pair<String, DateTime>, Asset> result = new HashMap<>();
        for (CashMovement movement : movements) {
            addAssetToMap(assetSetMap, movement.getSettlementDate(), movement.getAssetKey().getId());
        }
        for (Entry<DateTime, Set<String>> entry : assetSetMap.entrySet()) {
            Map<String, Asset> assets = assetIntegrationService.loadAssets(entry.getValue(), entry.getKey(), serviceErrors);
            for (Asset asset : assets.values()) {
                result.put(new ImmutablePair<String, DateTime>(asset.getAssetId(), entry.getKey()), asset);
            }
        }
        return result;
    }

    void addAssetToMap(Map<DateTime, Set<String>> assetSetMap, DateTime effectiveDate, String assetId) {
        Set<String> assetSet = assetSetMap.get(effectiveDate);
        if (assetSet == null) {
            assetSet = new HashSet<>();
            assetSetMap.put(effectiveDate, assetSet);
        }
        assetSet.add(assetId);
    }

    private String getServiceType(final DatedValuationKey key) {
        if (key instanceof ParameterisedDatedValuationKey) {
            Map<String, String> parameters = ((ParameterisedDatedValuationKey) key).getParameters();

            return parameters.get("serviceType");
        }

        return null;
    }

    private class BuildDtoProcess extends AbstractConcurrentComplete {
        private CashMovementsDto dto;
        private DatedValuationKey key;
        private ServiceErrors errors;

        public BuildDtoProcess(DatedValuationKey key, ServiceErrors errors) {
            this.key = key;
            this.errors = errors;
        }

        @Override
        public void run() {
            Map<AssetKey, TermDepositPresentation> tds = new HashMap<>();
            List<? extends ConcurrentResult<?>> r = this.getResults();
            List<HoldingIncomeDetails> incomes = (List<HoldingIncomeDetails>) r.get(3).getResult();
            Map<Pair<String, DateTime>, Asset> assetMap = getMovementAssets((Collection<CashMovement>) r.get(2).getResult(),
                    errors);
            tds.putAll(getTermDepositsFromMovements(key, assetMap, errors));
            tds.putAll(getTermDepositsFromIncomes(key, incomes, errors));
            dto = new CashMovementsDto(key, assetMap, tds, (WrapAccountDetail) r.get(0).getResult(),
                    (WrapAccountValuation) r.get(1).getResult(), (Collection<CashMovement>) r.get(2).getResult(),
                    (List<HoldingIncomeDetails>) r.get(3).getResult());
        }

        CashMovementsDto getDto() {
            return dto;
        }
    }
}
