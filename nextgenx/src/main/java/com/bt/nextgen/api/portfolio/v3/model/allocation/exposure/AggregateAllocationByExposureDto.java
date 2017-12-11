package com.bt.nextgen.api.portfolio.v3.model.allocation.exposure;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.asset.AssetClass;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AggregateAllocationByExposureDto extends BaseDto implements AllocationByExposureDto {
    private String name;
    private List<AllocationByExposureDto> allocations;

    public AggregateAllocationByExposureDto(String name, List<AllocationByExposureDto> allocations) {
        super();
        this.name = name;
        this.allocations = allocations;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationByExposureDto allocation : allocations) {
                balance = balance.add(allocation.getBalance());
            }
        }
        return balance;
    }

    @Override
    public BigDecimal getInternalBalance() {
        BigDecimal internalBalance = BigDecimal.ZERO;
        if (allocations != null && !allocations.isEmpty()) {
            for (AllocationByExposureDto allocation : allocations) {
                internalBalance = internalBalance.add(allocation.getInternalBalance());
            }
        }
        return internalBalance;
    }

    @Override
    public BigDecimal getExternalBalance() {
        BigDecimal externalBalance = BigDecimal.ZERO;
        if (allocations != null && !allocations.isEmpty()) {
            for (AllocationByExposureDto allocation : allocations) {
                externalBalance = externalBalance.add(allocation.getExternalBalance());
            }
        }
        return externalBalance;
    }

    @Override
    public Boolean getIsExternal() {
        for (AllocationByExposureDto allocation : allocations) {
            if (!allocation.getIsExternal()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BigDecimal getAccountPercent() {
        BigDecimal accountPercent = BigDecimal.ZERO;

        Map<String, BigDecimal> allocations = getAccountAllocationPercentage();
        for (BigDecimal categoryPercent : allocations.values()) {
            accountPercent = accountPercent.add(categoryPercent);
        }
        return accountPercent;
    }

    public List<AllocationByExposureDto> getAllocations() {
        return allocations;
    }

    @Override
    public Map<String, BigDecimal> getAllocationDollar() {
        Map<String, BigDecimal> totalsMap = totalsMap();

        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationByExposureDto allocation : allocations) {
                if (allocation != null && !(allocation.getAllocationDollar().isEmpty()))
                    addAllocValues(totalsMap, allocation.getAllocationDollar());

            }
        }

        return totalsMap;
    }

    private void addAllocValues(Map<String, BigDecimal> totalsMap, Map<String, BigDecimal> alloc) {

        for (Entry<String, BigDecimal> entry : totalsMap.entrySet()) {

            totalsMap.put(entry.getKey(), totalsMap.get(entry.getKey()).add(alloc.get(entry.getKey())));
        }
    }

    private Map<String, BigDecimal> totalsMap() {
        Map<String, BigDecimal> categoriesMap = new LinkedHashMap<>();
        for (AssetClass category : AssetClass.values()) {
            categoriesMap.put(category.name(), BigDecimal.ZERO);
        }
        return categoriesMap;
    }

    @Override
    public Map<String, BigDecimal> getAssetAllocationPercentage() {
        Map<String, BigDecimal> assetAllocationPercentTotals = totalsMap();
        BigDecimal sumAllocByDollar = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationByExposureDto allocation : allocations) {
                if (allocation != null && !(allocation.getAllocationDollar().isEmpty()))
                    sumAllocByDollar = sumAllocByDollar.add((BigDecimal) Lambda.sum(allocation.getAllocationDollar().values()));
            }
        }

        if (!(getAllocationDollar().isEmpty())) {
            Iterator<String> iter = assetAllocationPercentTotals.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();
                assetAllocationPercentTotals.put(key,
                        PortfolioUtils.getValuationAsPercent(getAllocationDollar().get(key), sumAllocByDollar));
            }
        }
        return assetAllocationPercentTotals;
    }

    @Override
    public Map<String, BigDecimal> getAccountAllocationPercentage() {
        Map<String, BigDecimal> accountAllocationPercentTotals = totalsMap();
        for (AllocationByExposureDto allocation : allocations) {
            if (allocation != null && !(allocation.getAccountAllocationPercentage().isEmpty()))
                addAllocValues(accountAllocationPercentTotals, allocation.getAccountAllocationPercentage());

        }
        return accountAllocationPercentTotals;
    }

    @Override
    public String getSource() {
        if (allocations == null) {
            return null;
        }
        final Set<String> sources = new HashSet<>();
        sources.addAll(Lambda.extract(allocations, Lambda.on(AllocationByExposureDto.class).getSource()));
        if (!sources.isEmpty() && sources.size() == 1) {
            return sources.iterator().next();
        }
        return getSource(sources);
    }

    /**
     * @param sources
     * @return
     */
    private String getSource(final Set<String> sources) {
        String sameSource = null;
        for (String source : sources) {
            sameSource = sameSource == null ? source : sameSource;
            if (sameSource == null || sameSource.equalsIgnoreCase(source)) {
                return null;
            }
        }
        return sameSource;
    }
}
