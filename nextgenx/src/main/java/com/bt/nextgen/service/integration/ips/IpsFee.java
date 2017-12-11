package com.bt.nextgen.service.integration.ips;

import com.bt.nextgen.service.avaloq.fees.FeesType;

import java.util.List;

public interface IpsFee extends IpsTariffBoundary {
    public FeesType getMasterBookKind();

    public FeesType getBookKind();

    public List<IpsTariff> getTariffList();

}
