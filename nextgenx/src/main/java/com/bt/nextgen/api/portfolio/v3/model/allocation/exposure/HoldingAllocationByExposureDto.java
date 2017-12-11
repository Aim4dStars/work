package com.bt.nextgen.api.portfolio.v3.model.allocation.exposure;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.lambdaj.Lambda;

import com.bt.nextgen.api.portfolio.v3.model.allocation.AssetAllocationDto;
import com.bt.nextgen.api.portfolio.v3.model.allocation.sector.HoldingAllocationBySourceDto;
import com.bt.nextgen.api.portfolio.v3.service.allocation.HoldingSource;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;

public class HoldingAllocationByExposureDto extends BaseDto implements AllocationByExposureDto, AssetAllocationDto {

    private final Asset asset;

    private final BigDecimal totalPortfolioBalance;

    private final Map<String, BigDecimal> alloc;

    private List<HoldingAllocationBySourceDto> children = new ArrayList<>();

    /**
     * @param totalPortfolioBalance
     * @param alloc
     * @param isIncome
     */
    public HoldingAllocationByExposureDto(List<HoldingSource> holdings, BigDecimal totalPortfolioBalance,
            Map<String, BigDecimal> alloc, boolean isIncome) {
        super();
        for(HoldingSource holding: holdings){
            this.children.add(new HoldingAllocationBySourceDto(holding, totalPortfolioBalance, isIncome));
        }
        this.asset = holdings.get(0).getSource();
        this.totalPortfolioBalance = totalPortfolioBalance;        
        this.alloc = alloc;        
    }

    @Override
    public BigDecimal getBalance() {
        return Lambda.sumFrom(this.children).getBalance();
    }

    @Override
    public BigDecimal getInternalBalance() {
        if (this.children.get(0).isExternal()) {
            return BigDecimal.ZERO;
        }
        return getBalance();
    }

    @Override
    public BigDecimal getExternalBalance() {
        if (this.children.get(0).isExternal()) {
            return getBalance();
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Boolean getIsExternal() {
        return this.children.get(0).isExternal();
    }

    @Override
    public Map<String, BigDecimal> getAllocationDollar() {
        Map<String, BigDecimal> totalsMap = totalsMap();
        if (alloc != null) {

            for (Entry<String, BigDecimal> entry : alloc.entrySet()) {
                if (!(entry.getValue().equals(BigDecimal.ZERO))) {
                    totalsMap.put(entry.getKey(), getBalance().multiply(entry.getValue()));
                }
            }
        }
        return totalsMap;
    }

    @Override
    public Map<String, BigDecimal> getAssetAllocationPercentage() {
        if (alloc != null) {
            return alloc;
        } else
            return new LinkedHashMap<String, BigDecimal>();
    }

    @Override
    public Map<String, BigDecimal> getAccountAllocationPercentage() {
        Map<String, BigDecimal> totalsMap = totalsMap();
        if (alloc != null) {
            for (Entry<String, BigDecimal> entry : alloc.entrySet()) {
                if (!(entry.getValue().equals(BigDecimal.ZERO))) {

                    BigDecimal accountAllocPercent = PortfolioUtils.getValuationAsPercent(getBalance(), totalPortfolioBalance)
                            .multiply(entry.getValue());
                    totalsMap.put(entry.getKey(), accountAllocPercent);
                }
            }
        }

        return totalsMap;
    }

    private Map<String, BigDecimal> totalsMap() {
        Map<String, BigDecimal> categoriesMap = new LinkedHashMap<>();
        for (AssetClass category : AssetClass.values()) {
            categoriesMap.put(category.name(), BigDecimal.ZERO);
        }
        return categoriesMap;
    }

    @Override
    public BigDecimal getAccountPercent() {
        if (BigDecimal.ZERO.compareTo(totalPortfolioBalance) == 0) {
            return BigDecimal.ZERO;
        }
        return getBalance().divide(totalPortfolioBalance, 8, RoundingMode.HALF_UP);
    }

    @Override
    public String getSource() {
        return this.children.get(0).getSource();
    }

    public String getName() {
        return asset.getAssetName();
    }

    public String getAssetCode() {
        return asset.getAssetCode();
    }

    public String getAssetId() {
        return asset.getAssetId();
    }

    public String getAssetSector() {
        return asset.getAssetClass() == null ? AssetClass.CASH.getDescription() : asset.getAssetClass().getDescription();
    }
}
