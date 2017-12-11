package com.bt.nextgen.service.integration.corporateaction;

public enum CorporateActionNonProRataPriorityOfferType {
	TAKE_UP(0), LAPSE(1);

	private int id;

	CorporateActionNonProRataPriorityOfferType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
