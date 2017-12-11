package com.bt.nextgen.api.account.v2.model;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class GrowthItemReportDto {

    private BigDecimal balance;
    private String displayName;
    private Boolean isNested;
    private List<GrowthItemReportDto> growthItems;

    public GrowthItemReportDto(BigDecimal balance, String displayName, Boolean isNested, List<GrowthItemReportDto> growthItems) {
        super();
        this.balance = balance;
        this.displayName = displayName;
        this.isNested = isNested;
        this.growthItems = growthItems;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Boolean getIsNested() {
        return isNested;
    }

    public List<GrowthItemReportDto> getGrowthItems() {
        return growthItems;
    }

}
