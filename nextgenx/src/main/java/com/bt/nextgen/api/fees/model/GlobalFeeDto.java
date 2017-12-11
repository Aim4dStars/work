package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: L069552
 * Date: 28/12/15
 * Time: 7:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class GlobalFeeDto extends BaseDto implements FeesComponentDto {

    private boolean managedFund;
    private boolean managedPortfolio;
    private boolean cash;
    private String label;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    private boolean termDeposit;
    private boolean share;

    private BigDecimal minimumFee;
    private BigDecimal maximumFee;

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }
}