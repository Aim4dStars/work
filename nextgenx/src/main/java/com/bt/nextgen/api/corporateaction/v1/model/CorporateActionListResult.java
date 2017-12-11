package com.bt.nextgen.api.corporateaction.v1.model;

import com.bt.nextgen.service.integration.corporateaction.CorporateAction;

import java.util.List;


public class CorporateActionListResult {
	private Boolean hasSuperPension;
	private List<CorporateAction> corporateActions;

	public Boolean getHasSuperPension() {
		return hasSuperPension;
	}

	public void setHasSuperPension(Boolean hasSuperPension) {
		this.hasSuperPension = hasSuperPension;
	}

	public List<CorporateAction> getCorporateActions() {
		return corporateActions;
	}

	public void setCorporateActions(List<CorporateAction> corporateActions) {
		this.corporateActions = corporateActions;
	}
}
