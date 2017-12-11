package com.bt.nextgen.service.integration.modelportfolio.rebalance;

import java.math.BigDecimal;

public interface RebalanceOrderDetails {

    public String getAccount();

    public String getAsset();

    public String getPreference();

    public BigDecimal getModelWeight();

    public BigDecimal getTargetWeight();

    public BigDecimal getCurrentWeight();

    public BigDecimal getDiffWeight();

    public BigDecimal getTargetValue();

    public BigDecimal getCurrentValue();

    public BigDecimal getDiffValue();

    public BigDecimal getTargetQuantity();

    public BigDecimal getCurrentQuantity();

    public BigDecimal getDiffQuantity();

    public String getOrderType();

    public Boolean getIsSellAll();

    public BigDecimal getOrderValue();

    public BigDecimal getOrderQuantity();

    public BigDecimal getFinalWeight();

    public BigDecimal getFinalValue();

    public BigDecimal getFinalQuantity();

    public String getReasonForExclusion();

}