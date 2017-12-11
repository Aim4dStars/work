package com.bt.nextgen.api.modelportfolio.v2.model.rebalance;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;
import com.fasterxml.jackson.annotation.JsonView;

public class ModelPortfolioExclusionDto extends BaseDto implements KeyedDto<AccountRebalanceKey> {

    @JsonView(JsonViews.Write.class)
    private AccountRebalanceKey rebalanceKey;

    @JsonView(JsonViews.Write.class)
    private String exclusionReason;

    @JsonView(JsonViews.Write.class)
    private ExclusionStatus exclusionStatus;

    public AccountRebalanceKey getKey() {
        return rebalanceKey;
    }

    public void setKey(AccountRebalanceKey rebalanceKey) {
        this.rebalanceKey = rebalanceKey;
    }

    public String getExclusionReason() {
        return exclusionReason;
    }

    public void setExclusionReason(String exclusionReason) {
        this.exclusionReason = exclusionReason;
    }

    public ExclusionStatus getExclusionStatus() {
        return exclusionStatus;
    }

    public void setExclusionStatus(ExclusionStatus exclusionStatus) {
        this.exclusionStatus = exclusionStatus;
    }

}
