package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.modelportfolio.TriggerStatus;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTrigger;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetails;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;


@ServiceBean(xpath = "/rebal_grp")
public class ModelPortfolioRebalanceTriggerImpl implements ModelPortfolioRebalanceTrigger {

    public static final String COMMON_PATH = "rebal_grp_head_list/rebal_grp_head/";
        
    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "rebal_grp_id/val")
    private String triggerType;

    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime mostRecentTriggerDate;

    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "imp_cont/val")
    private Integer totalAccountsCount;

    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "rebal_det_cnt/val")
    private Integer totalRebalancesCount;

    @ServiceElementList(xpath = "doc_list/doc", type = ModelPortfolioRebalanceTriggerDetailsImpl.class)
    private List<ModelPortfolioRebalanceTriggerDetails> rebalanceTriggerDetails;

    @ServiceElement(xpath = "../../rebal_trig_status_head_list/rebal_trig_status_head/rebal_trig_status_id/val", staticCodeCategory = "IPS_TRIG_REBAL_STATUS")
    private TriggerStatus triggerStatus;
    
    @Override
    public String getTriggerType() {
        return triggerType;
    }

    @Override
    public DateTime getMostRecentTriggerDate() {
        return mostRecentTriggerDate;
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
    public List<ModelPortfolioRebalanceTriggerDetails> getRebalanceTriggerDetails() {
        if (rebalanceTriggerDetails == null)
            return Collections.emptyList();
        return rebalanceTriggerDetails;
    }

    @Override
    public TriggerStatus getStatus() {
        return triggerStatus;
    }


    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public void setMostRecentTriggerDate(DateTime mostRecentTriggerDate) {
        this.mostRecentTriggerDate = mostRecentTriggerDate;
    }

    public void setTotalAccountsCount(Integer totalAccountsCount) {
        this.totalAccountsCount = totalAccountsCount;
    }

    public void setTotalRebalancesCount(Integer totalRebalancesCount) {
        this.totalRebalancesCount = totalRebalancesCount;
    }

    public void setRebalanceGroupDocs(List<ModelPortfolioRebalanceTriggerDetails> rebalanceGroupDocs) {
        this.rebalanceTriggerDetails = rebalanceGroupDocs;
    }
    
    public void setStatus(TriggerStatus status) {
        this.triggerStatus = status;
    }

}
