package com.bt.nextgen.api.ips.model;

import java.math.BigDecimal;

public class IpsFeeDto {

    private BigDecimal boundFrom = BigDecimal.ZERO;
    private BigDecimal boundTo = BigDecimal.ZERO;
    private BigDecimal fee;

    public IpsFeeDto(BigDecimal boundFrom, BigDecimal boundTo, BigDecimal fee) {
        super();
        this.boundFrom = boundFrom;
        this.boundTo = boundTo;
        this.fee = fee;
    }

    public BigDecimal getBoundFrom() {
        return boundFrom;
    }

    public BigDecimal getBoundTo() {
        return boundTo;
    }

    public BigDecimal getFee() {
        return fee;
    }

}
