package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.service.avaloq.fees.FeesComponentType;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.fees.FlatPercentFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.fasterxml.jackson.annotation.JsonView;

public class OrderFeeDto {
    @JsonView(JsonViews.Write.class)
    private FeesType feeType;

    @JsonView(JsonViews.Write.class)
    private FeesComponentType structure;

    @JsonView(JsonViews.Write.class)
    private PercentFeeDto percentFee;

    @JsonView(JsonViews.Write.class)
    private SlidingScaleFeeDto slidingFee;

    public OrderFeeDto() {
        super();
    }

    public OrderFeeDto(FeesType type, FlatPercentFeesComponent component) {
        this.feeType = type;
        structure = FeesComponentType.PERCENTAGE_FEE;
        this.percentFee = new PercentFeeDto(component);
    }

    public OrderFeeDto(FeesType type, SlidingScaleFeesComponent component) {
        this.feeType = type;
        structure = FeesComponentType.SLIDING_SCALE_FEE;
        this.slidingFee = new SlidingScaleFeeDto(component);
    }

    public FeesType getFeeType() {
        return feeType;
    }

    public void setFeeType(FeesType feeType) {
        this.feeType = feeType;
    }

    public FeesComponentType getStructure() {
        return structure;
    }

    public void setStructure(FeesComponentType structure) {
        this.structure = structure;
    }

    public PercentFeeDto getPercentFee() {
        return percentFee;
    }

    public void setPercentFee(PercentFeeDto percentFee) {
        this.percentFee = percentFee;
    }

    public SlidingScaleFeeDto getSlidingFee() {
        return slidingFee;
    }

    public void setSlidingFee(SlidingScaleFeeDto slidingFee) {
        this.slidingFee = slidingFee;
    }
}
