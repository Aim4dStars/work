package com.bt.nextgen.api.account.v2.service.allocation;

import com.bt.nextgen.api.account.v2.model.DatedValuationKey;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AggregateAllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.AllocationByExposureDto;
import com.bt.nextgen.api.account.v2.model.allocation.exposure.KeyedAllocByExposureDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
@Service
@Transactional(value = "springJpaTransactionManager")
public class AllocationByExposureDtoServiceImpl implements AllocationByExposureDtoService
{
    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    public PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    public AssetIntegrationService assetIntegrationService;

    @Autowired
    public ExposureAggregator exposureAggregator;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public KeyedAllocByExposureDto find(DatedValuationKey key, ServiceErrors serviceErrors) {
        // Retrieve valuations
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, key.getEffectiveDate(),
                key.getIncludeExternal(), serviceErrors);

        KeyedAllocByExposureDto emptyResult = new KeyedAllocByExposureDto(null, new ArrayList<AllocationByExposureDto>(),
                key, Boolean.FALSE);
        if (valuation == null || valuation.getSubAccountValuations().isEmpty())
            return emptyResult;

        BigDecimal totalPortfBal = valuation.getBalance();

        List<SubAccountValuation> subAccountValuations = valuation.getSubAccountValuations();

        Map<AssetKey, AssetAllocation> assetAllocations = getAllocationMap(subAccountValuations, key.getEffectiveDate(),
                serviceErrors);

        AggregateAllocationByExposureDto totalAllocation = exposureAggregator.aggregateAllocations(accountKey,
                valuation.getSubAccountValuations(), assetAllocations, totalPortfBal, serviceErrors);

        KeyedAllocByExposureDto resultDto = new KeyedAllocByExposureDto(totalAllocation.getName(),
                totalAllocation.getAllocations(), key, valuation.getHasExternal());

        return resultDto;
    }

    private Map<AssetKey, AssetAllocation> getAllocationMap(List<SubAccountValuation> valuations, DateTime effectiveDate,
            ServiceErrors serviceErrors) {
        List<AssetKey> assetKeys = new ArrayList<>();
        for (SubAccountValuation valuation : valuations) {
            List<AccountHolding> holdings = valuation.getHoldings();
            for (AccountHolding holding : holdings) {
                if (holding.getAsset().getAssetType() == AssetType.MANAGED_FUND) {
                    assetKeys.add(AssetKey.valueOf(holding.getAsset().getAssetId()));
                }
            }
        }
        if (assetKeys.isEmpty()) {
            return Collections.emptyMap();
        } else {
            return assetIntegrationService.loadAssetAllocations(assetKeys, effectiveDate, serviceErrors);
        }
    }

}
