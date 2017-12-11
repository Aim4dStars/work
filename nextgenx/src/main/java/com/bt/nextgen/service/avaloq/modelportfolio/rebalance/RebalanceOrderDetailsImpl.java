package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceOrderDetails;

import java.math.BigDecimal;

@ServiceBean(xpath = "det")
public class RebalanceOrderDetailsImpl implements RebalanceOrderDetails {
    
    @ServiceElement(xpath = "client/val")
    private String account;

    @ServiceElement(xpath = "asset/val")
    private String asset;

    @ServiceElement(xpath = "mp_pref/val")
    private String preference;

    @ServiceElement(xpath = "model_wgt/val")
    private BigDecimal modelWeight;
    
    @ServiceElement(xpath = "trg_wgt/val")
    private BigDecimal targetWeight;

    @ServiceElement(xpath = "curr_wgt/val")
    private BigDecimal currentWeight;

    @ServiceElement(xpath = "diff_wgt/val")
    private BigDecimal diffWeight;

    @ServiceElement(xpath = "estim_trg_val/val")
    private BigDecimal targetValue;

    @ServiceElement(xpath = "curr_val/val")
    private BigDecimal currentValue;

    @ServiceElement(xpath = "estim_diff_val/val")
    private BigDecimal diffValue;
    
    @ServiceElement(xpath = "estim_trg_qty/val")
    private BigDecimal targetQuantity;

    @ServiceElement(xpath = "curr_qty/val")
    private BigDecimal currentQuantity;

    @ServiceElement(xpath = "estim_diff_qty/val")
    private BigDecimal diffQuantity;

    @ServiceElement(xpath = "buy_sell/val")
    private String orderType;

    @ServiceElement(xpath = "sell_off/val")
    private Boolean isSellAll;

    @ServiceElement(xpath = "order_val/val")
    private BigDecimal orderValue;

    @ServiceElement(xpath = "order_qty/val")
    private BigDecimal orderQuantity;

    @ServiceElement(xpath = "estim_final_wgt/val")
    private BigDecimal finalWeight;

    @ServiceElement(xpath = "estim_final_val/val")
    private BigDecimal finalValue;

    @ServiceElement(xpath = "estim_final_qty/val")
    private BigDecimal finalQuantity;

    @ServiceElement(xpath = "reason_for_excl/val")
    private String reasonForExclusion;

    @Override
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    @Override
    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    @Override
    public BigDecimal getModelWeight() {
        return modelWeight;
    }

    public void setModelWeight(BigDecimal modelWeight) {
        this.modelWeight = modelWeight;
    }

    @Override
    public BigDecimal getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(BigDecimal targetWeight) {
        this.targetWeight = targetWeight;
    }

    @Override
    public BigDecimal getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(BigDecimal currentWeight) {
        this.currentWeight = currentWeight;
    }

    @Override
    public BigDecimal getDiffWeight() {
        return diffWeight;
    }

    public void setDiffWeight(BigDecimal diffWeight) {
        this.diffWeight = diffWeight;
    }

    @Override
    public BigDecimal getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(BigDecimal targetValue) {
        this.targetValue = targetValue;
    }

    @Override
    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public BigDecimal getDiffValue() {
        return diffValue;
    }

    public void setDiffValue(BigDecimal diffValue) {
        this.diffValue = diffValue;
    }

    @Override
    public BigDecimal getTargetQuantity() {
        return targetQuantity;
    }

    public void setTargetQuantity(BigDecimal targetQuantity) {
        this.targetQuantity = targetQuantity;
    }

    @Override
    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    @Override
    public BigDecimal getDiffQuantity() {
        return diffQuantity;
    }

    public void setDiffQuantity(BigDecimal diffQuantity) {
        this.diffQuantity = diffQuantity;
    }

    @Override
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public Boolean getIsSellAll() {
        return isSellAll;
    }

    public void setIsSellAll(Boolean isSellAll) {
        this.isSellAll = isSellAll;
    }

    @Override
    public BigDecimal getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(BigDecimal orderValue) {
        this.orderValue = orderValue;
    }

    @Override
    public BigDecimal getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(BigDecimal orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    @Override
    public BigDecimal getFinalWeight() {
        return finalWeight;
    }

    public void setFinalWeight(BigDecimal finalWeight) {
        this.finalWeight = finalWeight;
    }

    @Override
    public BigDecimal getFinalValue() {
        return finalValue;
    }

    public void setFinalValue(BigDecimal finalValue) {
        this.finalValue = finalValue;
    }

    @Override
    public BigDecimal getFinalQuantity() {
        return finalQuantity;
    }

    public void setFinalQuantity(BigDecimal finalQuantity) {
        this.finalQuantity = finalQuantity;
    }

    @Override
    public String getReasonForExclusion() {
        return reasonForExclusion;
    }

    public void setReasonForExclusion(String reasonForExclusion) {
        this.reasonForExclusion = reasonForExclusion;
    }

}