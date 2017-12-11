package com.bt.nextgen.service.avaloq.holdingbreach;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreach;
import com.bt.nextgen.service.integration.holdingbreach.HoldingBreachAsset;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ServiceBean(xpath = "bp", type = ServiceBeanType.CONCRETE)
public class HoldingBreachImpl implements HoldingBreach {
    @NotNull
    @ServiceElement(xpath = "bp_head_list/bp_head/bp/annot/ctx/id")
    private String accountId;

    @ServiceElement(xpath = "bp_head_list/bp_head/pv/val")
    private BigDecimal valuationAmount;

    @ServiceElementList(xpath = "asset_list/asset[asset_head_list/asset_head/cont_type/val='portf_dir']", type = HoldingBreachAssetImpl.class)
    private List<HoldingBreachAsset> breachAssets;

    @Override
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Override
    public BigDecimal getValuationAmount() {
        return valuationAmount;
    }

    public void setValuationAmount(BigDecimal valuationAmount) {
        this.valuationAmount = valuationAmount;
    }

    @Override
    public List<HoldingBreachAsset> getBreachAssets() {
        return breachAssets != null ? breachAssets : new ArrayList<HoldingBreachAsset>();
    }

    public void setBreachAssets(List<HoldingBreachAsset> breachAssets) {
        this.breachAssets = breachAssets;
    }
}
