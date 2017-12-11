package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.core.repository.CorporateActionSavedParticipation;

public class CorporateActionSavedDetails {
	private CorporateActionResponseCode responseCode;
	private CorporateActionSavedParticipation savedParticipation;

	public CorporateActionSavedDetails(CorporateActionResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public CorporateActionSavedDetails(CorporateActionResponseCode responseCode,
									   CorporateActionSavedParticipation savedParticipation) {
		this.responseCode = responseCode;
		this.savedParticipation = savedParticipation;
	}

	public CorporateActionResponseCode getResponseCode() {
		return responseCode;
	}

	public CorporateActionSavedParticipation getSavedParticipation() {
		return savedParticipation;
	}
}
