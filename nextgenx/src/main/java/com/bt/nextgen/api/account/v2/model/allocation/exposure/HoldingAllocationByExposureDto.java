package com.bt.nextgen.api.account.v2.model.allocation.exposure;

import com.bt.nextgen.api.account.v2.model.allocation.AssetAllocationDto;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

@Deprecated
public class HoldingAllocationByExposureDto extends BaseDto implements AllocationByExposureDto, AssetAllocationDto {

    private final String name;

    private final String assetCode;

    private final BigDecimal totalPortfolioBalance;

    private final BigDecimal balance;

    private final Map<String, BigDecimal> alloc;

    private final Boolean isExternal;

    private final String source;

    private final String assetId;

    private final String assetSector;

    /**
     * @param totalPortfolioBalance
     * @param balance
     * @param isExternal
     * @param source
     * @param alloc
     */
    public HoldingAllocationByExposureDto(Asset asset, BigDecimal totalPortfolioBalance, BigDecimal balance,
            Boolean isExternal,
            String source,
            Map<String, BigDecimal> alloc) {
        super();
        this.name = asset.getAssetName();
        this.assetId = asset.getAssetId();
        this.assetCode = asset.getAssetCode();
        this.assetSector = asset.getAssetClass() == null ? AssetClass.CASH.getDescription() : asset.getAssetClass()
                .getDescription();
        this.totalPortfolioBalance = totalPortfolioBalance;
        this.balance = balance;
        this.alloc = alloc;
        this.isExternal = isExternal;
        this.source = source;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public BigDecimal getInternalBalance() {
        if (isExternal) {
            return BigDecimal.ZERO;
        }
        return balance;
    }

    @Override
    public BigDecimal getExternalBalance() {
        if (isExternal) {
            return balance;
        }
        return BigDecimal.ZERO;
    }

    @Override
    public Boolean getIsExternal() {
        return isExternal;
    }

    @Override
    public Map<String, BigDecimal> getAllocationDollar() {
        Map<String, BigDecimal> totalsMap = totalsMap();
        if (alloc != null) {

            for (Entry<String, BigDecimal> entry : alloc.entrySet()) {
                if (!(entry.getValue().equals(BigDecimal.ZERO))) {
                    totalsMap.put(entry.getKey(), balance.multiply(entry.getValue()));
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

                    BigDecimal accountAllocPercent = PortfolioUtils.getValuationAsPercent(balance, totalPortfolioBalance)
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
        return balance.divide(totalPortfolioBalance, 8, RoundingMode.HALF_UP);
    }

    @Override
    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetSector() {
        return assetSector;
    }
}
