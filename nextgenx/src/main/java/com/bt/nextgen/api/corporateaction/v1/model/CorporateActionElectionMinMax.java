package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigDecimal;

public class CorporateActionElectionMinMax {
	private BigDecimal minimum;
	private BigDecimal maximum;
	private BigDecimal step;

	public CorporateActionElectionMinMax(BigDecimal minimum, BigDecimal maximum, BigDecimal step) {
		this.minimum = minimum;
		this.maximum = maximum;
		this.step = step;
	}

	public BigDecimal getMinimum() {
		return minimum;
	}

	public BigDecimal getMaximum() {
		return maximum;
	}

	public BigDecimal getStep() {
		return step;
	}
}
