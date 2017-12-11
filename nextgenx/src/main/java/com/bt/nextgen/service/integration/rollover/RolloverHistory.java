package com.bt.nextgen.service.integration.rollover;

import org.joda.time.DateTime;

public interface RolloverHistory extends RolloverFund {

    public String getRolloverId();

    public String getFundMemberId();

    public DateTime getDateRequested();

    public RolloverStatus getRequestStatus();

    public RolloverOption getRolloverOption();

    public Boolean getInitiatedByPanorama();
}
