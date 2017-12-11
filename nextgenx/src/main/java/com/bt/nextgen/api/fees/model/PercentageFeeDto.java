package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PercentageFeeDto extends BaseDto implements FeesComponentDto {
    private String name;
    private String label;
    private BigDecimal managedFund;
    private BigDecimal managedPortfolio;
    private BigDecimal cash;
    private BigDecimal termDeposit;
    private BigDecimal share;
    private BigDecimal minimumFee;
    private BigDecimal maximumFee;
    private List<PercentageAssetDto> listAssets = new ArrayList<>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getManagedFund() {
        return managedFund;
    }

    public void setManagedFund(BigDecimal managedFund) {
        this.managedFund = managedFund;
    }

    public BigDecimal getManagedPortfolio() {
        return managedPortfolio;
    }

    public void setManagedPortfolio(BigDecimal managedPortfolio) {
        this.managedPortfolio = managedPortfolio;
    }

    public BigDecimal getShare() {
        return share;
    }

    public void setShare(BigDecimal share) {
        this.share = share;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getTermDeposit() {
        return termDeposit;
    }

    public void setTermDeposit(BigDecimal termDeposit) {
        this.termDeposit = termDeposit;
    }

    public List<PercentageAssetDto> getListAssets() {
        return listAssets;
    }

    public void setListAssets(List<PercentageAssetDto> listAssets) {
        this.listAssets = listAssets;
    }


}
