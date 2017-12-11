package com.bt.nextgen.api.rollover.v1.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.core.api.model.BaseDto;
import com.fasterxml.jackson.annotation.JsonView;

import java.math.BigDecimal;

public class RolloverFundDto extends BaseDto {

    private String accountId;
    @JsonView(JsonViews.Write.class)
    private String fundId;
    @JsonView(JsonViews.Write.class)
    private String fundName;
    @JsonView(JsonViews.Write.class)
    private String fundAbn;
    @JsonView(JsonViews.Write.class)
    private String fundUsi;
    private String rolloverType;
    private BigDecimal amount;

    public RolloverFundDto() {
        super();
    }

    public RolloverFundDto(String accountId) {
        super();
        this.accountId = accountId;
    }

    public RolloverFundDto(String accountId, String fundId, String fundName, String fundAbn, String fundUsi, String rolloverType,
            BigDecimal estimatedAmount) {
        this(accountId);
        this.fundAbn = fundAbn;
        this.fundId = fundId;
        this.fundName = fundName;
        this.fundUsi = fundUsi;
        this.rolloverType = rolloverType;
        this.amount = estimatedAmount;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getFundId() {
        return fundId;
    }

    public String getFundName() {
        return fundName;
    }

    public String getFundAbn() {
        return fundAbn;
    }

    public String getFundUsi() {
        return fundUsi;
    }

    public String getRolloverType() {
        return this.rolloverType;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }
}
