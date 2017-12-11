package com.bt.nextgen.api.account.v2.service.valuation;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.InvestmentValuationDto;
import com.bt.nextgen.api.account.v2.model.ValuationDto;
import com.bt.nextgen.api.account.v2.model.ValuationSummaryDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Deprecated
@Service("ValuationDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
class ValuationDtoServiceImpl implements ValuationDtoService {

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountIntegrationService;

    @Autowired
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Autowired
    private ValuationAggregator valuationAggregator;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public ValuationDto find(DatedValuationKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        String accountType = getAccountType(accountKey, serviceErrors);
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, key.getEffectiveDate(),
                key.getIncludeExternal(), serviceErrors);

        return buildValuationDto(key, accountType, valuation, serviceErrors);
    }

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public ValuationDto findFromCache(DatedValuationKey key, boolean clearCache, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));

        if (clearCache) {
            cachedPortfolioIntegrationService.clearAccountValuationCache(accountKey, key.getEffectiveDate());
        }
        WrapAccountValuation valuation = cachedPortfolioIntegrationService.loadWrapAccountValuation(accountKey,
                key.getEffectiveDate(), serviceErrors);

        return buildValuationDto(key, null, valuation, serviceErrors);
    }

    private String getAccountType(AccountKey accountKey, ServiceErrors serviceErrors) {
        WrapAccount account = accountIntegrationService.loadWrapAccountWithoutContainers(accountKey, serviceErrors);
        String accountType = null;
        if (account != null && account.getAccountStructureType() != null) {
            accountType = account.getAccountStructureType().name();
        }
        return accountType;
    }

    protected ValuationDto buildValuationDto(DatedValuationKey key, String accountType, WrapAccountValuation valuation,
            ServiceErrors serviceErrors) {

        if (valuation == null) {
            List<ValuationSummaryDto> dtoList = new ArrayList<>();
            return new ValuationDto(key, BigDecimal.ZERO, accountType, false, dtoList);
        }

        BigDecimal balance = valuation.getBalance();

        Map<AssetType, List<InvestmentValuationDto>> valuationsByCategory = valuationAggregator.getValuationsByCategory(
                valuation, serviceErrors);

        List<ValuationSummaryDto> summaryList = createValuationSummaryDtos(valuationsByCategory, balance);

        return new ValuationDto(key, BigDecimal.ZERO, accountType, valuation.getHasExternal(), summaryList);
    }

    private List<ValuationSummaryDto> createValuationSummaryDtos(
            Map<AssetType, List<InvestmentValuationDto>> valuationsByCategory, BigDecimal totalBalance) {

        ValuationSummaryDto summary;
        List<ValuationSummaryDto> valuationSummaryList = new ArrayList<>();

        for (Map.Entry<AssetType, List<InvestmentValuationDto>> entry : valuationsByCategory.entrySet()) {

            AssetType key = entry.getKey();
            List<InvestmentValuationDto> valuations = new ArrayList<>(entry.getValue());

            if (valuations == null || valuations.isEmpty()) {
                continue;
            }

            boolean allExternal = areAllAssetsExternal(valuations);

            BigDecimal balance = BigDecimal.ZERO;
            BigDecimal income = BigDecimal.ZERO;

            if (AssetType.MANAGED_PORTFOLIO == key) {
                for (InvestmentValuationDto valuation : valuations) {
                    balance = balance.add(valuation.getBalance());
                    income = income.add(valuation.getIncome());
                }
            } else {
                for (InvestmentValuationDto valuation : valuations) {
                    balance = balance.add(valuation.getBalance()).add(valuation.getIncome());
                    income = income.add(valuation.getIncome());
                }
            }

            Collections.sort(valuations, valuationComparator);

            summary = new ValuationSummaryDto(key, allExternal, PortfolioUtils.getValuationAsPercent(balance, totalBalance),
                    income, PortfolioUtils.getValuationAsPercent(income, totalBalance), valuations);

            valuationSummaryList.add(summary);
        }

        Collections.sort(valuationSummaryList, valuationCategoryComparator);

        return valuationSummaryList;
    }

    private static final Comparator<ValuationSummaryDto> valuationCategoryComparator = new Comparator<ValuationSummaryDto>() {
        @Override
        public int compare(ValuationSummaryDto o1, ValuationSummaryDto o2) {
            Integer o1SortOrder = AssetType.valueOf(o1.getAssetType()).getSortOrder();
            Integer o2SortOrder = AssetType.valueOf(o2.getAssetType()).getSortOrder();
            return o1SortOrder.compareTo(o2SortOrder);
        }
    };

    private static final Comparator<InvestmentValuationDto> valuationComparator = new Comparator<InvestmentValuationDto>() {
        @Override
        public int compare(InvestmentValuationDto o1, InvestmentValuationDto o2) {
            if (o1.getExternalAsset() && !o2.getExternalAsset()) {
                return 1;
            }
            if (!o1.getExternalAsset() && o2.getExternalAsset()) {
                return -1;
            }
            String o1Name = o1.getName();
            String o2Name = o2.getName();
            return o1Name.compareToIgnoreCase(o2Name);
        }
    };

    private boolean areAllAssetsExternal(List<InvestmentValuationDto> valuations) {
        boolean allExternal = true;
        for (InvestmentValuationDto valuation : valuations) {
            if (valuation.getExternalAsset() == null || !valuation.getExternalAsset()) {
                allExternal = false;
            }
        }
        return allExternal;
    }
}
