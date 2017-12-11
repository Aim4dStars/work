package com.bt.nextgen.api.fees.v2.model;

import java.math.BigDecimal;

public class FeeDto {
    private BigDecimal feeExcludingGST;
    private BigDecimal gst;
    private BigDecimal feeIncludingGST;

    public FeeDto(BigDecimal feeExcludingGST, BigDecimal gst, BigDecimal feeIncludingGST) {
        super();
        this.feeExcludingGST = feeExcludingGST;
        this.gst = gst;
        this.feeIncludingGST = feeIncludingGST;
    }

    public BigDecimal getFeeExcludingGST() {
        return feeExcludingGST;
    }

    public BigDecimal getGst() {
        return gst;
    }

    public BigDecimal getFeeIncludingGST() {
        return feeIncludingGST;
    }

}