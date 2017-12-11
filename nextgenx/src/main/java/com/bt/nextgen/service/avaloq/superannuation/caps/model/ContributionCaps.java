package com.bt.nextgen.service.avaloq.superannuation.caps.model;


import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ContributionCaps
{
    public DateTime getFinancialYearStartDate();

    public BigDecimal getConcessionalCap();

    public BigDecimal getNonConcessionalCap();

    public void setFinancialYearStartDate(DateTime financialYearStartDate);

    public void setConcessionalCap(BigDecimal concessionalCap);

    public void setNonConcessionalCap(BigDecimal nonConcessionalCap);
}