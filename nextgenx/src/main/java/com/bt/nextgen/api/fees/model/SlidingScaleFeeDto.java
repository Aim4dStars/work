package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

public class SlidingScaleFeeDto extends BaseDto implements FeesComponentDto {
    private boolean managedFund;
    private boolean managedPortfolio;
    private boolean cash;
    private boolean termDeposit;
    private boolean share;
    private List<SlidingScaleFeeTierDto> slidingScaleFeeTier;
    private BigDecimal minimumFee;
    private BigDecimal maximumFee;
    private String label;
    private String name;
    private BigDecimal spclDiscount;
    private int assetCount;

    public BigDecimal getSpclDiscount() {
        return spclDiscount;
    }

    public void setSpclDiscount(BigDecimal spclDiscount) {
        this.spclDiscount = spclDiscount;
    }

    public boolean isManagedFund() {
        return managedFund;
    }

    public void setManagedFund(boolean managedFund) {
        this.managedFund = managedFund;
    }

    public boolean isManagedPortfolio() {
        return managedPortfolio;
    }

    public void setManagedPortfolio(boolean managedPortfolio) {
        this.managedPortfolio = managedPortfolio;
    }

    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
    }

    public boolean isTermDeposit() {
        return termDeposit;
    }

    public void setTermDeposit(boolean termDeposit) {
        this.termDeposit = termDeposit;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }

    public List<SlidingScaleFeeTierDto> getSlidingScaleFeeTier() {
        return slidingScaleFeeTier;
    }

    public void setSlidingScaleFeeTier(List<SlidingScaleFeeTierDto> slidingScaleFeeTier) {
        this.slidingScaleFeeTier = slidingScaleFeeTier;
    }

    public BigDecimal getMinimumFee() {
        return minimumFee;
    }

    public void setMinimumFee(BigDecimal minimumFee) {
        this.minimumFee = minimumFee;
    }

    public BigDecimal getMaximumFee() {
        return maximumFee;
    }

    public void setMaximumFee(BigDecimal maximumFee) {
        this.maximumFee = maximumFee;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAssetCount() {
        return assetCount;
    }

    public void setAssetCount(int assetCount) {
        this.assetCount = assetCount;
    }

}
