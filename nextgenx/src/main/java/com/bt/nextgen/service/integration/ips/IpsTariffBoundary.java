package com.bt.nextgen.service.integration.ips;

import java.math.BigDecimal;

public interface IpsTariffBoundary {
    public BigDecimal getBoundFrom();

    public BigDecimal getBoundTo();

    public BigDecimal getTariffFactor();

    public BigDecimal getTariffOffset();

    public BigDecimal getMin();

    public BigDecimal getMax();
}
