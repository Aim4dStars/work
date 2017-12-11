package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

public class FundsAllocationDto extends BaseDto {
    @JsonView(JsonViews.Write.class)
    private String accountId;

    @JsonView(JsonViews.Write.class)
    private BigDecimal allocation;

    public FundsAllocationDto() { // default constructor
    }

    public FundsAllocationDto(String accountId, BigDecimal allocation) {
        super();
        this.accountId = accountId;
        this.allocation = allocation;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAllocation() {
        return allocation;
    }

    public void setAllocation(BigDecimal allocation) {
        this.allocation = allocation;
    }

}
