package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;

public class CorporateActionNonProRataPriorityOfferAccountElectionDtoImpl implements CorporateActionAccountElectionDto {
    @JsonView(JsonViews.Write.class)
    private Integer optionId;

    @JsonView(JsonViews.Write.class)
    private BigDecimal units;

    public CorporateActionNonProRataPriorityOfferAccountElectionDtoImpl(Integer optionId, BigDecimal units) {
        this.optionId = optionId;
        this.units = units;
    }

    @Override
    public Integer getOptionId() {
        return optionId;
    }

    public BigDecimal getUnits() {
        return units;
    }
}
