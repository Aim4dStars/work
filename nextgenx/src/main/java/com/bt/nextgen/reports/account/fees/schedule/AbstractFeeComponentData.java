package com.bt.nextgen.reports.account.fees.schedule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

public abstract class AbstractFeeComponentData {
    private final String name;

    public AbstractFeeComponentData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract List<? extends Object> getChildren();

    public List<String> getAssetTypes()
    {
        return Collections.emptyList();
    }

    protected BigDecimal fixPercentage(BigDecimal incorrect) {
        // The fees api is using incorrect scaling on percentage values throughout. We
        // need need to fix it up before passing it onto the reporting tier
        return incorrect.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
    }
}
