package com.bt.nextgen.api.portfolio.v3.model.performance;

import com.bt.nextgen.api.portfolio.v3.model.DateRangeAccountKey;
import com.bt.nextgen.core.api.model.KeyedDto;

public class PerformanceReportDto  extends PerformanceBaseDto implements KeyedDto<DateRangeAccountKey> {

    private DateRangeAccountKey key;

    @Override
    public DateRangeAccountKey getKey() {
        return key;
    }

    public void setKey(DateRangeAccountKey key) {
        this.key = key;
    }
}
