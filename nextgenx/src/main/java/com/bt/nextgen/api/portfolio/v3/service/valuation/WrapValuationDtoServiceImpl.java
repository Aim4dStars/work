package com.bt.nextgen.api.portfolio.v3.service.valuation;

import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.portfolio.CachePortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.wrap.integration.PortfolioValuationIntegrationServiceImpl;
import com.bt.nextgen.service.wrap.integration.portfolio.ThirdPartyValuation;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by L062605 on 5/09/2017.
 */
@Service("ValuationDtoServiceV3")
@Profile({"WrapOffThreadImplementation"})
@Transactional(value = "springJpaTransactionManager")
public class WrapValuationDtoServiceImpl implements CacheableValuationDtoService {

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    @Qualifier("cacheAvaloqPortfolioIntegrationService")
    private CachePortfolioIntegrationService cachedPortfolioIntegrationService;

    @Autowired
    private PortfolioValuationIntegrationServiceImpl portfolioValuationIntegrationService;

    @Autowired
    private ValuationAggregator valuationAggregator;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public ValuationDto find(DatedValuationKey key, ServiceErrors serviceErrors) {
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccountValuation valuation = portfolioValuationIntegrationService.loadWrapAccountValuation(accountKey, key.getEffectiveDate(),
                key.getIncludeExternal(), serviceErrors);
        return buildValuationDto(key, valuation, serviceErrors);
    }

    protected ValuationDto buildValuationDto(DatedValuationKey key, WrapAccountValuation valuation, ServiceErrors serviceErrors) {

        if (valuation == null) {
            List<ValuationSummaryDto> dtoList = new ArrayList<>();
            return new ValuationDto(key, BigDecimal.ZERO, false, dtoList);
        }

        BigDecimal balance = valuation.getBalance();

        Map<AssetType, List<InvestmentValuationDto>> valuationsByCategory = valuationAggregator.getValuationsByCategory(valuation,
                serviceErrors);

        String thirdPartySource = null;
        if (valuation instanceof ThirdPartyValuation) {
            thirdPartySource = ((ThirdPartyValuation) valuation).getThirdPartySource();
        }
        List<ValuationSummaryDto> summaryList = createValuationSummaryDtos(valuationsByCategory, key, balance, thirdPartySource);

        return new ValuationDto(key, BigDecimal.ZERO, valuation.getHasExternal(), summaryList);
    }

    private List<ValuationSummaryDto> createValuationSummaryDtos(
            Map<AssetType, List<InvestmentValuationDto>> valuationsByCategory, DatedValuationKey valuationKey,
            BigDecimal totalBalance, String thirdPartySource) {

        ValuationSummaryDto summary;
        List<ValuationSummaryDto> valuationSummaryList = new ArrayList<>();

        for (Map.Entry<AssetType, List<InvestmentValuationDto>> entry : valuationsByCategory.entrySet()) {

            AssetType key = entry.getKey();
            List<InvestmentValuationDto> valuations = new ArrayList<>(entry.getValue());

            if (valuations.isEmpty()) {
                continue;
            }
            Collections.sort(valuations, new ValuationComparator());

            summary = new ValuationSummaryDto(key, totalBalance, valuations);
            summary.setThirdPartySource(thirdPartySource);
            valuationSummaryList.add(summary);
        }

        Collections.sort(valuationSummaryList, new ValuationCategoryComparator());

        return valuationSummaryList;
    }

    @Override
    public ValuationDto findFromCache(DatedValuationKey key, boolean clearCache, ServiceErrors serviceErrors) {
        return null;
    }
}
