package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ModelPortfolioRebalanceTriggerDto extends BaseDto {
    private String trigerStatus;
    private String triggerType;
    private DateTime mostRecentTriggerDate;
    private Integer totalAccountsCount;
    private Integer totalRebalancesCount;
    private List<ModelPortfolioRebalanceTriggerDetailsDto> rebalanceTriggerDetails = new ArrayList<ModelPortfolioRebalanceTriggerDetailsDto>();

    public ModelPortfolioRebalanceTriggerDto(String trigerStatus, String triggerType, DateTime mostRecentTriggerDate,
            Integer totalAccountsCount,
            Integer totalRebalancesCount) {
        super();
        this.trigerStatus = trigerStatus;
        this.triggerType = triggerType;
        this.mostRecentTriggerDate = mostRecentTriggerDate;
        this.totalAccountsCount = totalAccountsCount;
        this.totalRebalancesCount = totalRebalancesCount;
    }

    public String getTriggerStatus() {
        return trigerStatus;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public DateTime getMostRecentTriggerDate() {
        return mostRecentTriggerDate;
    }

    public Integer getTotalAccountsCount() {
        return totalAccountsCount;
    }

    public Integer getTotalRebalancesCount() {
        return totalRebalancesCount;
    }

    public List<ModelPortfolioRebalanceTriggerDetailsDto> getRebalanceTriggerDetails() {
        return rebalanceTriggerDetails;
    }

}
