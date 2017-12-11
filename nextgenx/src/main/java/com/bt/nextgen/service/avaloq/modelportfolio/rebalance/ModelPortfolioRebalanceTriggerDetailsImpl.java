package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelPortfolioRebalanceTriggerDetails;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@ServiceBean(xpath = "/doc")
public class ModelPortfolioRebalanceTriggerDetailsImpl implements ModelPortfolioRebalanceTriggerDetails {

    public static final String COMMON_PATH = "doc_head_list/doc_head/";

    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "rebal_trig_id/val")
    private String trigger;
    
    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "trx_date/val", converter = DateTimeTypeConverter.class)
    private DateTime tranasactionDate;

    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "imp_cont/val")
    private Integer totalAccountsCount;

    @NotNull
    @ServiceElement(xpath = COMMON_PATH + "rebal_det_cnt/val")
    private Integer totalRebalancesCount;

    @Override
    public String getTrigger() {
        return trigger;
    }

    @Override
    public DateTime getTranasactionDate() {
        return tranasactionDate;
    }

    @Override
    public Integer getTotalAccountsCount() {
        return totalAccountsCount;
    }

    @Override
    public Integer getTotalRebalancesCount() {
        return totalRebalancesCount;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public void setTranasactionDate(DateTime tranasactionDate) {
        this.tranasactionDate = tranasactionDate;
    }

    public void setTotalAccountsCount(Integer totalAccountsCount) {
        this.totalAccountsCount = totalAccountsCount;
    }

    public void setTotalRebalancesCount(Integer totalRebalancesCount) {
        this.totalRebalancesCount = totalRebalancesCount;
    }


}
