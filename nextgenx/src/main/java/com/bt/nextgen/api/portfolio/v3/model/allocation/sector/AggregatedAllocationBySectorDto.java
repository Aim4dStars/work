package com.bt.nextgen.api.portfolio.v3.model.allocation.sector;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.integration.asset.AssetClass;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AggregatedAllocationBySectorDto extends BaseDto implements AllocationBySectorDto {
    private String name;
    private List<AllocationBySectorDto> allocations;

    public AggregatedAllocationBySectorDto(AggregatedAllocationBySectorDto sector) {
        this.allocations = sector.allocations;
        this.name = sector.name;
    }

    public AggregatedAllocationBySectorDto(String name, List<AllocationBySectorDto> allocations) {
        this.allocations = allocations;
        this.name = name;
    }

    @Override
    public BigDecimal getUnits() {
        boolean hasUnits = false;
        BigDecimal units = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationBySectorDto allocation : allocations) {
                if (allocation.getUnits() != null) {
                    hasUnits = true;
                    units = units.add(allocation.getUnits());
                }
            }
        }
        return hasUnits ? units : null;
    }

    @Override
    public BigDecimal getBalance() {
        BigDecimal balance = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationBySectorDto allocation : allocations) {
                balance = balance.add(allocation.getBalance());
            }
        }
        return balance;
    }

    @Override
    public BigDecimal getInternalBalance() {
        BigDecimal internalBalance = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationBySectorDto allocation : allocations) {
                internalBalance = internalBalance.add(allocation.getInternalBalance());
            }
        }
        return internalBalance;
    }

    @Override
    public BigDecimal getExternalBalance() {
        BigDecimal externalBalance = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationBySectorDto allocation : allocations) {
                externalBalance = externalBalance.add(allocation.getExternalBalance());
            }
        }
        return externalBalance;
    }

    @Override
    public BigDecimal getAllocationPercentage() {
        BigDecimal percentage = BigDecimal.ZERO;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationBySectorDto allocation : allocations) {
                percentage = percentage.add(allocation.getAllocationPercentage());
            }
        }
        return percentage;
    }

    @Override
    public String getName() {
        return name;
    }

    public List<AllocationBySectorDto> getAllocations() {
        return allocations;
    }

    @Override
    public Boolean getPending() {
        boolean pending = true;
        if (allocations != null && !(allocations.isEmpty())) {
            for (AllocationBySectorDto allocation : allocations) {
                if (!allocation.getPending()) {
                    pending = false;
                }
            }
        }
        return pending;
    }

    public Boolean getIsExternal() {
        if (allocations == null) {
            return false;
        }
        for (AllocationBySectorDto allocation : allocations) {
            if (!allocation.getIsExternal()) {
                return false;
            }
        }
        return true;
    }

    public String getSource() {
        if (allocations == null) {
            return null;
        }
        Set<String> sources = new HashSet<>();
        sources.addAll(Lambda.extract(allocations, Lambda.on(AllocationBySectorDto.class).getSource()));
        if (sources.size() == 1) {
            return sources.iterator().next();
        }
        return null;
    }

    public String getAssetSector() {
        if (allocations == null) {
            return null;
        }
        Set<String> sources = new HashSet<>();
        sources.addAll(Lambda.extract(allocations, Lambda.on(AllocationBySectorDto.class).getAssetSector()));
        if (sources.size() == 1) {
            return sources.iterator().next();
        }
        return AssetClass.OTHER.getDescription();
    }

}
