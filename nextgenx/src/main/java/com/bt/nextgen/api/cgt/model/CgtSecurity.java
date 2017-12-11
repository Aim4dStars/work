package com.bt.nextgen.api.cgt.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface CgtSecurity {

    Object getKey();

    String getSecurityCode();

    String getSecurityName();

    String getSecurityType();

    BigDecimal getAmount();

    void setAmount(BigDecimal amount);

    Integer getQuantity();

    void setQuantity(Integer quantity);

    DateTime getDate();

    void setDate(DateTime date);

    DateTime getTaxDate();

    void setTaxDate(DateTime taxDate);

    BigDecimal getTaxAmount();

    void setTaxAmount(BigDecimal taxAmount);

    BigDecimal getCostBase();

    void setCostBase(BigDecimal costBase);

    BigDecimal getCalculatedCost();

    void setCalculatedCost(BigDecimal calculatedCost);

    String getCostCode();

    void setCostCode(String costCode);

    BigDecimal getGrossGain();

    void setGrossGain(BigDecimal grossGain);

    BigDecimal getCalculatedGain();

    void setCalculatedGain(BigDecimal calculatedGain);

    String getGainCode();

    void setGainCode(String gainCode);

    Integer getDaysHeld();

    void setDaysHeld(Integer daysHeld);

    BigDecimal getIndexedCostBase();

    void setIndexedCostBase(BigDecimal indexedCostBase);

    BigDecimal getReducedCostBase();

    void setReducedCostBase(BigDecimal reducedCostBase);

    String getParentInvId();

    void setParentInvId(String parentInvId);

    String getParentInvCode();

    void setParentInvCode(String parentInvCode);

    String getParentInvName();

    void setParentInvName(String parentInvName);

    String getParentInvType();

    void setParentInvType(String parentInvType);

    BigDecimal getCostBaseGain();

    void setCostBaseGain(BigDecimal costBaseGain);

}