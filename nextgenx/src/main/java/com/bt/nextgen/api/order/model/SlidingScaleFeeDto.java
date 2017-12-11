package com.bt.nextgen.api.order.model;

import com.bt.nextgen.config.JsonViews;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleFeesComponent;
import com.bt.nextgen.service.avaloq.fees.SlidingScaleTiers;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.List;

public class SlidingScaleFeeDto {
    @JsonView(JsonViews.Write.class)
    private List<SlidingScaleTierDto> tiers;

    public SlidingScaleFeeDto() {
        super();
    }

    public SlidingScaleFeeDto(SlidingScaleFeesComponent component) {
        super();
        tiers = new ArrayList<SlidingScaleTierDto>();
        for (SlidingScaleTiers tier : component.getTiers()) {
            tiers.add(new SlidingScaleTierDto(tier));
        }
    }

    public List<SlidingScaleTierDto> getTiers() {
        return tiers;
    }

    public void setTiers(List<SlidingScaleTierDto> tiers) {
        this.tiers = tiers;
    }

}
