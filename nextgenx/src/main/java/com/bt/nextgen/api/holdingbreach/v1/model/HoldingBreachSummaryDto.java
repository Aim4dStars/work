package com.bt.nextgen.api.holdingbreach.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.util.List;

public class HoldingBreachSummaryDto extends BaseDto {
    private final DateTime reportDate;
    private final List<HoldingBreachDto> holdingBreaches;

    public HoldingBreachSummaryDto(DateTime reportDate, List<HoldingBreachDto> holdingBreaches) {
        super();
        this.reportDate = reportDate;
        this.holdingBreaches = holdingBreaches;
    }

    public DateTime getReportDate() {
        return reportDate;
    }

    public List<HoldingBreachDto> getHoldingBreaches() {
        return holdingBreaches;
    }
}
