package com.bt.nextgen.service.integration.rollover;

import org.joda.time.DateTime;

public interface SuperfundDetails {

    public String getUsi();

    public DateTime getValidFrom();

    public DateTime getValidTo();

    public String getActive();

    public String getAbn();

    public String getOrgName();

    public String getProductName();
}
