package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;

import com.bt.nextgen.config.JsonViews;

public class CorporateActionSelectedOptionDto {
	@JsonView(JsonViews.Write.class)
	private Integer optionId;

	@JsonView(JsonViews.Write.class)
	private BigDecimal units;

	@JsonView(JsonViews.Write.class)
	private BigDecimal percent;

	@JsonView(JsonViews.Write.class)
	private BigDecimal oversubscribe;

	public CorporateActionSelectedOptionDto() {
		// Empty constructor
	}

	public CorporateActionSelectedOptionDto(Integer optionId, BigDecimal units, BigDecimal percent, BigDecimal oversubscribe) {
		this.optionId = optionId;
		this.units = units;
		this.percent = percent;
		this.oversubscribe = oversubscribe;
	}

	public Integer getOptionId() {
		return optionId;
	}

	public CorporateActionSelectedOptionDto setOptionId(Integer optionId) {
		this.optionId = optionId;
		return this;
	}

	public BigDecimal getUnits() {
		return units;
	}

	public CorporateActionSelectedOptionDto setUnits(BigDecimal units) {
		this.units = units;
		return this;
	}

	public BigDecimal getPercent() {
		return percent;
	}

	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}

	public BigDecimal getOversubscribe() {
		return oversubscribe;
	}

	public CorporateActionSelectedOptionDto setOversubscribe(BigDecimal oversubscribe) {
		this.oversubscribe = oversubscribe;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		CorporateActionSelectedOptionDto other = (CorporateActionSelectedOptionDto) obj;

		return objectsEqual(this.optionId, other.optionId) && decimalsEqual(this.units, other.units) &&
			decimalsEqual(this.percent, other.percent) && decimalsEqual(this.oversubscribe, other.oversubscribe);
	}

	private boolean objectsEqual(Object thisObject, Object otherObject) {
		if (thisObject == null && otherObject == null) {
			return true;
		}

		if (thisObject == null) {
			return false;
		}

		return thisObject.equals(otherObject);
	}

	private boolean decimalsEqual(BigDecimal thisObject, BigDecimal otherObject) {
		if (thisObject == null && otherObject == null) {
			return true;
		}

		if (thisObject == null || otherObject == null) {
			return false;
		}

		return thisObject.compareTo(otherObject) == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		BigDecimal units = this.units != null ? this.units : BigDecimal.ZERO;
		BigDecimal percent = this.percent != null ? this.percent : BigDecimal.ZERO;
		BigDecimal oversubscribe = this.oversubscribe != null ? this.oversubscribe : BigDecimal.ZERO;

		result = prime * result + this.optionId.hashCode();
		result = prime * result + units.hashCode();
		result = prime * result + percent.hashCode();
		result = prime * result + oversubscribe.hashCode();

		return result;
	}
}
