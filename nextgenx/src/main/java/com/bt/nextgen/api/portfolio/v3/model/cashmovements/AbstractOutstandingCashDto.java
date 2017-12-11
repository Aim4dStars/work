package com.bt.nextgen.api.portfolio.v3.model.cashmovements;

import com.bt.nextgen.core.api.model.BaseDto;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public abstract class AbstractOutstandingCashDto extends BaseDto {
    private String category;

    public AbstractOutstandingCashDto(String category) {
        this.category = category;
    }

    public AbstractOutstandingCashDto() {
        category = null;
    }

    @NotNull
    public abstract List<OutstandingCash> getOutstanding();

    public BigDecimal getAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        for (OutstandingCash outstandingItem : getOutstanding()) {
            amount = amount.add(outstandingItem.getAmount());
        }
        return amount;
    }

    public String getCategory() {
        return category;
    }

    protected void addOutstandingCash(List<OutstandingCash> outstanding) {

    }
}
