package com.bt.nextgen.service.integration.rollover;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ContributionReceived {

    public String getContributionId();

    public String getDescription();

    public BigDecimal getAmount();

    public DateTime getPaymentDate();

    public RolloverContributionStatus getContributionStatus();

}
