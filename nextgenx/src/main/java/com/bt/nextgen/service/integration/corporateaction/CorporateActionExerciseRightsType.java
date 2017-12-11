package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionExerciseRightsType {
	LAPSE(1), PARTIAL(2), FULL(3);

	private int id;

	CorporateActionExerciseRightsType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
