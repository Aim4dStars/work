package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;
import java.util.List;

@Deprecated
public class ValuationMovementReportDto extends BaseDto {

    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private List<GrowthItemReportDto> growthItems;

    public ValuationMovementReportDto(BigDecimal openingBalance, BigDecimal closingBalance, List<GrowthItemReportDto> growthItems) {
        super();
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.growthItems = growthItems;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public List<GrowthItemReportDto> getGrowthItems() {
        return growthItems;
    }
}
