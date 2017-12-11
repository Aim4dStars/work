package com.bt.nextgen.api.profile.model;

import java.util.Date;

public interface InvestorTncDetails
{
    boolean isApprover();

    void setApprover(boolean approver);

    boolean isAcceptedTnc();

    void setAcceptedTnc(boolean acceptedTnc);

    Date getTncSignDate();

    void setTncSignDate(Date tncSignDate);
}
