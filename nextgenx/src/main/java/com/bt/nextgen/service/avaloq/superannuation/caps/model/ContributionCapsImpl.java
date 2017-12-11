package com.bt.nextgen.service.avaloq.superannuation.caps.model;


import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;

@ServiceBean(xpath = "//bp_list/bp/bp_head_list/bp_head", type = ServiceBeanType.CONCRETE)
public class ContributionCapsImpl implements ContributionCaps
{
    @ServiceElement(xpath = "sa_conc_cap/val")
    private BigDecimal concessionalCap;

    @ServiceElement(xpath = "sa_nconc_cap/val")
    private BigDecimal nonConcessionalCap;

    @ServiceElement(xpath = "fy_date/val", converter = IsoDateTimeConverter.class)
    private DateTime financialYearStartDate;


    @Override
    public BigDecimal getConcessionalCap() {
        return concessionalCap;
    }

    @Override
    public BigDecimal getNonConcessionalCap() {
        return nonConcessionalCap;
    }

    @Override
    public DateTime getFinancialYearStartDate() {
        return financialYearStartDate;
    }

    public void setConcessionalCap(BigDecimal concessionalCap) {
        this.concessionalCap = concessionalCap;
    }

    public void setNonConcessionalCap(BigDecimal nonConcessionalCap) {
        this.nonConcessionalCap = nonConcessionalCap;
    }

    public void setFinancialYearStartDate(DateTime financialYearStartDate) {
        this.financialYearStartDate = financialYearStartDate;
    }
}