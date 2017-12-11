package com.bt.nextgen.service.avaloq.insurance.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface PolicyTracking {

    public String getFNumber();
    public String getPolicyNumber();
    public PolicyType getPolicyType();
    public PolicySubType getPolicySubType();
    public PolicyStatusCode getPolicyStatus();
    public String getAccountNumber();
    public String getInstitutionName();
    public PaymentType getPaymentType();
    public BigDecimal getPremium();
    public BigDecimal getProposedPremium();
    public PremiumFrequencyType getPaymentFrequency();
    public BigDecimal getRenewalCommission();
    public DateTime getRenewalCalendarDay();
    public DateTime getCommencementDate();
}
