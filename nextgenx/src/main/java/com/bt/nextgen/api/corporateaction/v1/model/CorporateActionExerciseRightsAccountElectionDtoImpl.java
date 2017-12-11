package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;


public class CorporateActionExerciseRightsAccountElectionDtoImpl implements CorporateActionAccountElectionDto {
	@JsonView(JsonViews.Write.class)
	private Integer optionId;

	@JsonView(JsonViews.Write.class)
	private BigDecimal units;

	@JsonView(JsonViews.Write.class)
	private BigDecimal oversubscribe;

	public CorporateActionExerciseRightsAccountElectionDtoImpl(Integer optionId, BigDecimal units, BigDecimal oversubscribe) {
		this.optionId = optionId;
		this.units = units;
		this.oversubscribe = oversubscribe;
	}

	@Override
	public Integer getOptionId() {
		return optionId;
	}

	public BigDecimal getUnits() {
		return units;
	}

	public BigDecimal getOversubscribe() {
		return oversubscribe;
	}
}
