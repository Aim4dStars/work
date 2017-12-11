package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

public class ModelPortfolioRebalanceTriggerDetailsDto extends BaseDto {
    private DateTime lastUpdateDate;
    private String trigger;
    private Integer impactedAccountsCount;

    public ModelPortfolioRebalanceTriggerDetailsDto(DateTime lastUpdateDate, String trigger, Integer impactedAccountsCount) {
        this.lastUpdateDate = lastUpdateDate;
        this.trigger = trigger;
        this.impactedAccountsCount = impactedAccountsCount;
    }

    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getTrigger() {
        return trigger;
    }

    public Integer getImpactedAccountsCount() {
        return impactedAccountsCount;
    }

}
