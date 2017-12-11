package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.ips.IpsTariff;
import com.bt.nextgen.service.integration.ips.IpsTariffBoundary;

import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "tariff")
public class IpsTariffImpl implements IpsTariff {

    @ServiceElement(xpath = "bound_from/val")
    private BigDecimal boundFrom;

    @ServiceElement(xpath = "bound_to/val")
    private BigDecimal boundTo;

    @ServiceElement(xpath = "tariff_factor/val")
    private BigDecimal tariffFactor;

    @ServiceElement(xpath = "tariff_offset/val")
    private BigDecimal tariffOffset;

    @ServiceElement(xpath = "min/val")
    private BigDecimal min;

    @ServiceElement(xpath = "max/val")
    private BigDecimal max;

    @ServiceElement(xpath = "tariff_bnd_list/tariff_bnd", type = IpsTariffBoundaryImpl.class)
    private List<IpsTariffBoundary> tariffBndList;

    @Override
    public BigDecimal getBoundFrom() {
        return boundFrom;
    }

    @Override
    public BigDecimal getBoundTo() {
        return boundTo;
    }

    @Override
    public BigDecimal getTariffFactor() {
        return tariffFactor;
    }

    @Override
    public BigDecimal getTariffOffset() {
        return tariffOffset;
    }

    @Override
    public BigDecimal getMin() {
        return min;
    }

    @Override
    public BigDecimal getMax() {
        return max;
    }

    @Override
    public List<IpsTariffBoundary> getTariffBndList() {
        return tariffBndList;
    }

    public void setBoundFrom(BigDecimal boundFrom) {
        this.boundFrom = boundFrom;
    }

    public void setBoundTo(BigDecimal boundTo) {
        this.boundTo = boundTo;
    }

    public void setTariffFactor(BigDecimal tariffFactor) {
        this.tariffFactor = tariffFactor;
    }

    public void setTariffOffset(BigDecimal tariffOffset) {
        this.tariffOffset = tariffOffset;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public void setTariffBndList(List<IpsTariffBoundary> tariffBndList) {
        this.tariffBndList = tariffBndList;
    }

}
