package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.AccountKeyConverter;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;

import java.math.BigDecimal;

@ServiceBean(xpath = "cont")
public class RebalanceAccountImpl implements RebalanceAccount {
    @ServiceElement(xpath = "account/val", converter = AccountKeyConverter.class)
    private AccountKey account;

    @ServiceElement(xpath = "avsr/val", converter = BrokerKeyConverter.class)
    private BrokerKey adviser;

    @ServiceElement(xpath = "portf_val/val", converter = BigDecimalConverter.class)
    private BigDecimal value;

    @ServiceElement(xpath = "asset_class_breach/val")
    private Integer assetClassBreach;

    @ServiceElement(xpath = "tolrc_breach/val")
    private Integer toleranceBreach;

    @ServiceElement(xpath = "estim_buys/val")
    private Integer estimatedBuys;

    @ServiceElement(xpath = "estim_sells/val")
    private Integer estimatedSells;

    @ServiceElement(xpath = "excl_reason/val")
    private String systemExclusionReason;

    // Change this to match key type of next service when we get it.
    @ServiceElement(xpath = "rebal_det_doc_id/val")
    private String rebalDocId;

    @ServiceElement(xpath = "do_excl/val")
    private String userExcluded;

    @ServiceElement(xpath = "justif/val")
    private String userExclusionReason;

    @Override
    public AccountKey getAccount() {
        return account;
    }

    public void setAccount(AccountKey account) {
        this.account = account;
    }

    @Override
    public BrokerKey getAdviser() {
        return adviser;
    }

    public void setAdviser(BrokerKey adviser) {
        this.adviser = adviser;
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public Integer getAssetClassBreach() {
        return assetClassBreach;
    }

    public void setAssetClassBreach(Integer assetClassBreach) {
        this.assetClassBreach = assetClassBreach;
    }

    @Override
    public Integer getToleranceBreach() {
        return toleranceBreach;
    }

    public void setToleranceBreach(Integer toleranceBreach) {
        this.toleranceBreach = toleranceBreach;
    }

    @Override
    public Integer getEstimatedBuys() {
        return estimatedBuys;
    }

    public void setEstimatedBuys(Integer estimatedBuys) {
        this.estimatedBuys = estimatedBuys;
    }

    @Override
    public Integer getEstimatedSells() {
        return estimatedSells;
    }

    public void setEstimatedSells(Integer estimatedSells) {
        this.estimatedSells = estimatedSells;
    }

    @Override
    public String getSystemExclusionReason() {
        return systemExclusionReason;
    }

    public void setSystemExclusionReason(String systemExclusionReason) {
        this.systemExclusionReason = systemExclusionReason;
    }

    @Override
    public String getRebalDocId() {
        return rebalDocId;
    }

    public void setRebalDocId(String rebalDocId) {
        this.rebalDocId = rebalDocId;
    }

    @Override
    public Boolean getUserExcluded() {
        Boolean excluded = false;
        if (userExcluded != null) {
            excluded = Boolean.valueOf(userExcluded);
        }
        return excluded;
    }

    public void setUserExcluded(String userExcluded) {
        this.userExcluded = userExcluded;
    }

    @Override
    public String getUserExclusionReason() {
        return userExclusionReason;
    }

    public void setUserExclusionReason(String userExclusionReason) {
        this.userExclusionReason = userExclusionReason;
    }
}