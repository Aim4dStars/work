package com.bt.nextgen.api.fundpayment.model;

import org.joda.time.DateTime;

public class FundPaymentNoticeSearchDtoKey
{
    private DateTime startDate;
    private DateTime endDate;

    public DateTime getStartDate()
    {
        return startDate;
    }

    public void setStartDate(DateTime startDate)
    {
        this.startDate = startDate;
    }

    public DateTime getEndDate()
    {
        return endDate;
    }

    public void setEndDate(DateTime endDate)
    {
        this.endDate = endDate;
    }
}
