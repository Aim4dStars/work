package com.bt.nextgen.service.avaloq.insurance.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface PolicyApplications {

    public DateTime getApplicationReceivedDate();
    public String getInsuredPersonGivenName();
    public String getInsuredPersonLastName();
    public BigDecimal getTotalPremium();
    public PolicyStatusCode getPolicyStatus();
    public String getInsuranceAdviserId();
    public String getCustomerNumber();
    public String getPolicyNumber();
}
