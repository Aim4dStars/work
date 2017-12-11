package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsTariff;

import java.math.BigDecimal;
import java.util.List;

@ServiceBean(xpath = "fee")
public class IpsFeeImpl implements IpsFee {

    @ServiceElement(xpath = "btfg_master_book_kind_id/val", staticCodeCategory = "FEE_TYPE")
    private FeesType masterBookKind;

    @ServiceElement(xpath = "btfg_book_kind_id/val", staticCodeCategory = "FEE_TYPE")
    private FeesType bookKind;

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

    @ServiceElement(xpath = "tariff_list/tariff", type = IpsTariffImpl.class)
    private List<IpsTariff> tariffList;

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
    public List<IpsTariff> getTariffList() {
        return tariffList;
    }

    @Override
    public FeesType getMasterBookKind() {
        return masterBookKind;
    }

    @Override
    public FeesType getBookKind() {
        return bookKind;
    }

    public void setMasterBookKind(FeesType masterBookKind) {
        this.masterBookKind = masterBookKind;
    }

    public void setBookKind(FeesType bookKind) {
        this.bookKind = bookKind;
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

    public void setTariffList(List<IpsTariff> tariffList) {
        this.tariffList = tariffList;
    }

}
