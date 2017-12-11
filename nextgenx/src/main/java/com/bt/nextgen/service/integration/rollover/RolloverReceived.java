package com.bt.nextgen.service.integration.rollover;

import org.joda.time.DateTime;

public interface RolloverReceived extends RolloverFund {

    public DateTime getReceivedDate();

    public RolloverContributionStatus getContributionStatus();

}
