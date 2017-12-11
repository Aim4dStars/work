package com.bt.nextgen.api.fees.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: L069552
 * Date: 26/12/15
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class PercentageAssetDto {

    private List<String> assetClasses;
    private BigDecimal tariffFactor = new BigDecimal(0).setScale(2);
    private Boolean isTailored;

    public Boolean getIsTailored() {
        return isTailored;
    }

    public void setIsTailored(Boolean isTailored) {
        this.isTailored = isTailored;
    }

    public List getAssetClasses() {
        return assetClasses;
    }

    public void setAssetClasses(List assetClass) {
        this.assetClasses = assetClass;
    }

    public BigDecimal getTariffFactor() {
        return tariffFactor;
    }

    public void setTariffFactor(BigDecimal tariffFactor) {
        this.tariffFactor = tariffFactor;
    }


    public BigDecimal getMinFees() {
        return minFees;
    }

    public void setMinFees(BigDecimal minFees) {
        this.minFees = minFees;
    }

    public BigDecimal getMaxFees() {
        return maxFees;
    }

    public void setMaxFees(BigDecimal maxFees) {
        this.maxFees = maxFees;
    }


    private BigDecimal minFees;
    private BigDecimal maxFees;


}