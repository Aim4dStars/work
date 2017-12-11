package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

public class CorporateActionRatio {
    private BigDecimal oldStock;
    private BigDecimal newStock;
    private BigDecimal ratio;

    public CorporateActionRatio(BigDecimal oldStock, BigDecimal newStock) {
        this.oldStock = oldStock;
        this.newStock = newStock;

        if (oldStock != null && newStock != null) {
            ratio = newStock.setScale(4).divide(oldStock.setScale(4), 4, BigDecimal.ROUND_HALF_DOWN);
        }
    }

    public BigDecimal getOldStock() {
        return oldStock;
    }

    public BigDecimal getNewStock() {
        return newStock;
    }

    public BigDecimal getRatio() {
        return ratio;
    }
}
