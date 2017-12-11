package com.bt.nextgen.api.fees.model;

import com.bt.nextgen.core.api.model.BaseDto;

import java.math.BigDecimal;

/**
 * Created User: F121237 Date: 13/06/17
 */
public class FlatPercentageFeeDto extends BaseDto implements FeesComponentDto {
    private BigDecimal rate = new BigDecimal(0).setScale(2);
    private String label;
    private String name;

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal tariffFactor) {
        this.rate = tariffFactor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
