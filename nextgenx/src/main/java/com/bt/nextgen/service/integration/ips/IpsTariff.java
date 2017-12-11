package com.bt.nextgen.service.integration.ips;

import java.math.BigDecimal;
import java.util.List;

public interface IpsTariff extends IpsTariffBoundary {

    public List<IpsTariffBoundary> getTariffBndList();
}
