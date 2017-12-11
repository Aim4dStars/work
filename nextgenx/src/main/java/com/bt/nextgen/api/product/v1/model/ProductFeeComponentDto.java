package com.bt.nextgen.api.product.v1.model;

import com.bt.nextgen.core.api.model.BaseDto;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public class ProductFeeComponentDto extends BaseDto {
    private String feeComponentName;

    private DateTime feeDateFrom;

    private DateTime feeDateTo;

    private BigDecimal capFactor;

    private BigDecimal capMin;

    private BigDecimal capMax;

    private BigDecimal capOffSet;

    private BigDecimal tariffFactorMax;

    private BigDecimal tariffOffSetFactorMax;

    private String feeType;

    public String getFeeComponentName() {
        return feeComponentName;
    }

    public void setFeeComponentName(final String feeComponentName) {
        this.feeComponentName = feeComponentName;
    }

    public DateTime getFeeDateFrom() {
        return feeDateFrom;
    }

    public void setFeeDateFrom(final DateTime feeDateFrom) {
        this.feeDateFrom = feeDateFrom;
    }

    public DateTime getFeeDateTo() {
        return feeDateTo;
    }

    public void setFeeDateTo(final DateTime feeDateTo) {
        this.feeDateTo = feeDateTo;
    }

    public BigDecimal getCapFactor() {
        return capFactor;
    }

    public void setCapFactor(final BigDecimal capFactor) {
        this.capFactor = capFactor;
    }

    public BigDecimal getCapMin() {
        return capMin;
    }

    public void setCapMin(final BigDecimal capMin) {
        this.capMin = capMin;
    }

    public BigDecimal getCapMax() {
        return capMax;
    }

    public void setCapMax(final BigDecimal capMax) {
        this.capMax = capMax;
    }

    public BigDecimal getCapOffSet() {
        return capOffSet;
    }

    public void setCapOffSet(final BigDecimal capOffSet) {
        this.capOffSet = capOffSet;
    }

    public BigDecimal getTariffFactorMax() {
        return tariffFactorMax;
    }

    public void setTariffFactorMax(final BigDecimal tariffFactorMax) {
        this.tariffFactorMax = tariffFactorMax;
    }

    public BigDecimal getTariffOffSetFactorMax() {
        return tariffOffSetFactorMax;
    }

    public void setTariffOffSetFactorMax(final BigDecimal tariffOffSetFactorMax) {
        this.tariffOffSetFactorMax = tariffOffSetFactorMax;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(final String feeType) {
        this.feeType = feeType;
    }
}
