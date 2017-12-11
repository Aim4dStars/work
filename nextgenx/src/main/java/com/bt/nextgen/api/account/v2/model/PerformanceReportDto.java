package com.bt.nextgen.api.account.v2.model;

import com.bt.nextgen.core.api.model.KeyedDto;

@Deprecated
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
