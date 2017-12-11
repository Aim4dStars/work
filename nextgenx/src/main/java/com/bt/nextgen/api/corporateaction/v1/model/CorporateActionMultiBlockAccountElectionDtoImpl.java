package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;


public class CorporateActionMultiBlockAccountElectionDtoImpl implements CorporateActionAccountElectionDto {
	@JsonView(JsonViews.Write.class)
	private Integer optionId;

	@JsonView(JsonViews.Write.class)
	private BigDecimal units;

	@JsonView(JsonViews.Write.class)
	private BigDecimal percent;

	public CorporateActionMultiBlockAccountElectionDtoImpl(Integer optionId, BigDecimal units, BigDecimal percent) {
		this.optionId = optionId;
		this.units = units;
		this.percent = percent;
	}

	@Override
	public Integer getOptionId() {
		return optionId;
	}

	@Override
	public BigDecimal getUnits() {
		return units;
	}

	public BigDecimal getPercent() {
		return percent;
	}
}
