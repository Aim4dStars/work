package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.ips.IpsKeyConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalance;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTrigger;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "/ips")
public class ModelPortfolioRebalanceImpl implements ModelPortfolioRebalance {
    
    public static final String COMMON_PATH = "ips_head_list/ips_head/";
    
    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "ips_id/val", converter = IpsKeyConverter.class)
    private IpsKey ipsKey;
    
    @ServiceElement(xpath = COMMON_PATH + "ips_status_id/val")
    private String ipsStatus;

    @ServiceElement(xpath = COMMON_PATH + "last_rebal_date/val", converter = DateTimeTypeConverter.class)
    private DateTime lastRebalanceDate;

    @ServiceElement(xpath = COMMON_PATH + "submit/val")
    private String submitInProgress;

    @ServiceElement(xpath = COMMON_PATH + "last_rebal_user/val")    
    private String userName;
    
    @ServiceElement(xpath = COMMON_PATH + "imp_cont/val")
    private Integer totalAccountsCount;
    
    @ServiceElement(xpath = COMMON_PATH + "rebal_det_cnt/val")
    private Integer totalRebalancesCount;

    @ServiceElementList(xpath = "rebal_trig_status_list/rebal_trig_status/rebal_grp_list/rebal_grp", type = ModelPortfolioRebalanceTriggerImpl.class)
    private List<ModelPortfolioRebalanceTrigger> rebalanceTriggers;
    
    @Override
    public IpsKey getIpsKey() {
        return ipsKey;
    }
    
    @Override
    public String getIpsStatus() {
        return ipsStatus;
    }

    @Override
    public DateTime getLastRebalanceDate() {
        return lastRebalanceDate;
    }

    @Override
    public Integer getTotalAccountsCount() {
        return totalAccountsCount;
    }

    @Override
    public Integer getTotalRebalancesCount() {
        return totalRebalancesCount;
    }
    
    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public Boolean getSubmitInProgress() {
        boolean inProgress = false;
        if (submitInProgress != null) {
            inProgress = Boolean.valueOf(submitInProgress);
        }
        return inProgress;
    }

    @Override
    public List<ModelPortfolioRebalanceTrigger> getRebalanceTriggers() {
        if (rebalanceTriggers == null)
            return Collections.emptyList();
        return rebalanceTriggers;
    }

    public void setIpsKey(IpsKey ipsKey) {
        this.ipsKey = ipsKey;
    }

    public void setIpsStatus(String ipsStatus) {
        this.ipsStatus = ipsStatus;
    }

    public void setLastRebalanceDate(DateTime lastRebalanceDate) {
        this.lastRebalanceDate = lastRebalanceDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTotalAccountsCount(Integer totalAccountsCount) {
        this.totalAccountsCount = totalAccountsCount;
    }

    public void setTotalRebalancesCount(Integer totalRebalancesCount) {
        this.totalRebalancesCount = totalRebalancesCount;
    }

    public void setRebalanceTriggers(List<ModelPortfolioRebalanceTrigger> rebalanceGroups) {
        this.rebalanceTriggers = rebalanceGroups;
    }
}
