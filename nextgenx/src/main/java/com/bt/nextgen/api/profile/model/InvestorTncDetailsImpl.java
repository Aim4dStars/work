package com.bt.nextgen.api.profile.model;

import java.util.Date;

public class InvestorTncDetailsImpl implements InvestorTncDetails
{
    private boolean approver;
    private boolean acceptedTnc;
    private Date tncSignDate;

    public boolean isApprover()
    {
        return approver;
    }

    public void setApprover(boolean approver)
    {
        this.approver = approver;
    }

    public boolean isAcceptedTnc()
    {
        return acceptedTnc;
    }

    public void setAcceptedTnc(boolean acceptedTnc)
    {
        this.acceptedTnc = acceptedTnc;
    }

    public Date getTncSignDate()
    {
        Date date = tncSignDate;
        return date;
    }

    public void setTncSignDate(Date tncSignDate)
    {
        this.tncSignDate = tncSignDate != null ? new Date(tncSignDate.getTime()) : null;
    }
}
