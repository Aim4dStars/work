package com.bt.nextgen.service.integration.holdingbreach;

import org.joda.time.DateTime;

import java.util.List;

public interface HoldingBreachSummary {
    public DateTime getReportDate();

    public List<HoldingBreach> getHoldingBreaches();
}
