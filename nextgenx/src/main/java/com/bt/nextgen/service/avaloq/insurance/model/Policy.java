package com.bt.nextgen.service.avaloq.insurance.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

public interface Policy {

    public PolicyType getPolicyType();

    public PolicySubType getPolicySubType();

    public String getPolicyNumber();

    public String getAccountNumber();

    public PremiumFrequencyType getPolicyFrequency();

    public BigDecimal getPremium();

    public BigDecimal getProposedPremium();

    public PolicyStatusCode getStatus();

    public List<PersonImpl> getOwners();

    public List<PersonImpl> getBeneficiaries();

    public CommissionStructureType getCommissionStructure();

    public String getPortfolioNumber();

    public String getParentPolicyNumber();

    public Boolean isSharedPolicy();

    public DateTime getCommencementDate();

    public DateTime getRenewalCalendarDay();

    public DateTime getPaidToDate();

    public BigDecimal getRenewalPercent();

    public CommissionState getCommissionState();

    public BigDecimal getDialDown();

    public String getBenefitPeriodFactor();

    public String getBenefitPeriodTerm();

    public Integer getWaitingPeriod();

    public List<PolicyLifeImpl> getPolicyLifes();

    public BigDecimal getIPIncomeRatioPercent();

    public String getAccountId();
}