package com.bt.nextgen.api.account.v2.model;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class GrowthItemDto {
    private BigDecimal balance;
    private String code;
    private String displayName;
    private List<GrowthItemDto> growthItems;

    public GrowthItemDto() {
    }

    public GrowthItemDto(BigDecimal balance, String code, String displayName, List<GrowthItemDto> growthItems) {
        super();
        this.balance = balance;
        this.code = code;
        this.displayName = displayName;
        this.growthItems = growthItems;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<GrowthItemDto> getGrowthItems() {
        return growthItems;
    }
}