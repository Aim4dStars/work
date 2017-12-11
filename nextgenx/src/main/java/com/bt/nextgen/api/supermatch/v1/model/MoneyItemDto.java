package com.bt.nextgen.api.supermatch.v1.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ApiModel(value = "MoneyItem", description = " Money items for the monies held by ATO. These monies are not-rolloverable")
public class MoneyItemDto {

    @ApiModelProperty(value = "Fund balance")
    private BigDecimal balance;

    @ApiModelProperty(value = "Money category", notes = "Expected values are: [CoContributions, SuperannuationGuarantee, UnclaimedTemporaryResident]")
    private String category;

    public MoneyItemDto(BigDecimal balance, String category) {
        this.balance = balance != null ? balance.setScale(2, RoundingMode.HALF_EVEN) : BigDecimal.ZERO;
        this.category = category;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCategory() {
        return category;
    }
}
